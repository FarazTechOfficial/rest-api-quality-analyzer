"""Reproducible experiment runner for the REST API Quality Analyzer.

Runs the Analyzer Service (localhost:8081 by default) over a configured list of
OpenAPI fixtures, stores every raw response under results/raw/, and writes an
aggregated results/summary.json plus results/experiment-summary.md.

Every number in the outputs is produced by this script against the live
Analyzer Service at run time. Nothing is hardcoded or invented here.

Run:
    cd experiments
    python run_experiment.py            # uses experiments/apis.jsonc config
    python run_experiment.py --url http://localhost:8081
"""

import argparse
import json
import os
import sys
import traceback
import urllib.error
import urllib.request
from datetime import datetime, timezone

HERE = os.path.dirname(os.path.abspath(__file__))
ROOT = os.path.dirname(HERE)
DEFAULT_URL = "http://localhost:8081"

# Fixture corpus used for this plumbing validation run. Swap the file list for
# a real study corpus to change what the experiment measures.
FIXTURES = [
    {"name": "good-api", "file": "sample-apis/good-api.json"},
    {"name": "mixed-api", "file": "sample-apis/mixed-api.json"},
    {"name": "bad-api", "file": "sample-apis/bad-api.json"},
    {"name": "my-api", "file": "sample-apis/my-api.json"},
    {"name": "request-body", "file": "sample-apis/request-body.json"},
]


def analyze(url: str, api_name: str, spec: dict) -> dict:
    body = json.dumps(
        {"apiName": api_name, "specification": json.dumps(spec)}
    ).encode("utf-8")
    req = urllib.request.Request(
        url + "/api/analyze", data=body, headers={"Content-Type": "application/json"}
    )
    with urllib.request.urlopen(req, timeout=120) as resp:
        return json.loads(resp.read())


def practice_coverage(results: list) -> dict:
    """Aggregate results per distinct practice id: passed/failed/total and
    per-category rollups. Category is inferred from the practice id prefix;
    OPENAPI ids are the tool's own spec-hygiene checks (not paper practices)."""
    by_practice: dict[str, dict] = {}
    categories: dict[str, dict] = {}
    for r in results:
        pid = r.get("practiceId") or "UNKNOWN"
        cat = _category(pid)
        entries = by_practice.setdefault(pid, {"passed": 0, "failed": 0})
        if r.get("passed"):
            entries["passed"] += 1
        else:
            entries["failed"] += 1
        catstat = categories.setdefault(cat, {"passed": 0, "failed": 0})
        if r.get("passed"):
            catstat["passed"] += 1
        else:
            catstat["failed"] += 1
    return {
        "byPractice": {
            pid: {"status": "PASSED" if st["failed"] == 0 else "FAILED", **st}
            for pid, st in sorted(by_practice.items())
        },
        "byCategory": {
            cat: {"status": "PASSED" if st["failed"] == 0 else "FAILED", **st}
            for cat, st in sorted(categories.items())
        },
    }


def _category(pid: str) -> str:
    upper = pid.upper()
    if upper.startswith("OPENAPI"):
        return "OpenAPI conventions"
    if upper.startswith("RM"):
        return "Request methods"
    if upper.startswith("E"):
        return "Error handling"
    if upper.startswith("H"):
        return "HTTP headers"
    if upper.startswith("O"):
        return "Other"
    if upper.startswith("U"):
        return "URI design"
    return "Unknown"


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--url", default=DEFAULT_URL, help="Analyzer Service base URL")
    parser.add_argument("--config", default=os.path.join(HERE, "apis.jsonc"))
    args = parser.parse_args()

    if os.path.exists(args.config):
        raw = open(args.config, "r", encoding="utf-8").read()
        # strip // comments for a minimal jsonc reader
        stripped = "\n".join(
            line for line in raw.splitlines() if not line.strip().startswith("//")
        )
        apis = json.loads(stripped)
        label = "configured corpus"
    else:
        apis = FIXTURES
        label = "sample-apis fixture corpus"

    raw_dir = os.path.join(ROOT, "results", "raw")
    os.makedirs(raw_dir, exist_ok=True)

    generated_at = datetime.now(timezone.utc).isoformat(timespec="seconds")
    summaries = []
    for entry in apis:
        name = entry["name"]
        file = os.path.join(ROOT, entry["file"])
        with open(file, "r", encoding="utf-8-sig") as fh:
            spec = json.load(fh)
        print(f"[analyzer] running {name} ...")
        try:
            report = analyze(args.url, name, spec)
        except urllib.error.HTTPError as exc:
            detail = exc.read().decode("utf-8", errors="replace")[:300]
            print(f"[error] {name}: HTTP {exc.code} — {detail}")
            summaries.append(
                {
                    "name": name,
                    "source": entry.get("source", entry["file"]),
                    "generatedAt": generated_at,
                    "rawPath": None,
                    "error": f"HTTP {exc.code}: {detail}",
                    "score": None,
                }
            )
            continue
        except Exception as exc:  # noqa: BLE001 — record any analyzer failure
            print(f"[error] {name}: {exc}")
            traceback.print_exc()
            summaries.append(
                {
                    "name": name,
                    "source": entry.get("source", entry["file"]),
                    "generatedAt": generated_at,
                    "rawPath": None,
                    "error": str(exc),
                    "score": None,
                }
            )
            continue
        ts = generated_at.replace(":", "").replace("+", "")
        raw_path = os.path.join(raw_dir, f"{name}-{ts}.json")
        with open(raw_path, "w", encoding="utf-8") as fh:
            json.dump(report, fh, indent=2)
        coverage = practice_coverage(report.get("results") or [])
        summaries.append(
            {
                "name": name,
                "source": entry.get("source", entry["file"]),
                "generatedAt": generated_at,
                "rawPath": raw_path.replace("\\", "/").replace(ROOT.replace("\\", "/") + "/", ""),
                "totalRules": report.get("totalRules"),
                "passedRules": report.get("passedRules"),
                "failedRules": report.get("failedRules"),
                "skippedRules": report.get("skippedRules"),
                "score": report.get("score"),
                **coverage,
            }
        )

    summary = {
        "generatedAt": generated_at,
        "analyzerUrl": args.url,
        "dataset": label,
        "apis": summaries,
    }
    summary_path = os.path.join(ROOT, "results", "summary.json")
    with open(summary_path, "w", encoding="utf-8") as fh:
        json.dump(summary, fh, indent=2)

    md_path = os.path.join(ROOT, "results", "experiment-summary.md")
    with open(md_path, "w", encoding="utf-8") as fh:
        fh.write("# Experiment summary\n\n")
        fh.write(
            f"- Generated: {generated_at} (UTC) by `experiments/run_experiment.py`\n"
        )
        fh.write(f"- Analyzer: {args.url}\n")
        fh.write(f"- Dataset: {label}\n\n")
        header = "| API | Score | Passed | Failed | Skipped | Total |"
        rule = "|-----|-------|--------|--------|---------|-------|"
        rows = []
        for s in summaries:
            if "error" in s:
                rows.append(f"| {s['name']} | **ERR** | — | — | — | — |")
                continue
            rows.append(
                f"| {s['name']} | {s['score']}% | {s['passedRules']} | "
                f"{s['failedRules']} | {s['skippedRules'] or 0} | {s['totalRules']} |"
            )
        fh.write(header + "\n" + rule + "\n" + "\n".join(rows) + "\n")
        fh.write(
            "\n> These numbers are tool outputs over the configured corpus; they are "
            "NOT a reproduction of the paper's study on GCP/OpenStack/OCCI.\n"
        )

    print()
    print(f"Wrote {summary_path} and {md_path}")
    return 0


if __name__ == "__main__":
    sys.exit(main())