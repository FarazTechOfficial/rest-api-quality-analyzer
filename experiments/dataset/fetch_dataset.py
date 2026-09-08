"""Reproducible acquisition of the real-world OpenAPI dataset (experiments/dataset).

Dataset source: APIs.guru openapi-directory (https://api.apis.guru).
Every spec file is downloaded raw from the canonical APIs.guru URL and stored
byte-for-byte, unmodified. Nothing here fabricates or rewrites a specification.

Two phases:

  phase 1  python fetch_dataset.py download
           - resolves the curated API list to its preferred version in the
             APIs.guru index (cached in .cache/ if present, otherwise fetched)
           - pins the resolved version keys into selection.csv
           - downloads each spec to specs/<api_id>/openapi.json (original bytes)
           - runs a structural sanity check (JSON + openapi/swagger + paths)
           - writes a preliminary manifest.csv

  phase 2  <run tool/ probe:  mvn -o -q compile exec:java ... see README>
           - parses every spec with the exact parser the Analyzer uses
             (io.swagger.parser.v3 OpenAPIV3Parser, resolve=true) and writes
             tool/validation.json with authoritative per-spec results

  phase 3  python fetch_dataset.py finalize
           - consumes tool/validation.json and writes the final manifest.csv
             keyed to the parser's authoritative version + endpoint counts
           - any spec the analyzer's parser rejects is excluded and reported

Run from anywhere; paths are anchored to this file's directory.
Only the Python standard library is used.
"""

import argparse
import csv
import gzip
import json
import os
import re
import sys
import urllib.request
from collections import Counter
from datetime import datetime, timezone

HERE = os.path.dirname(os.path.abspath(__file__))
INDEX_URL = "https://api.apis.guru/v2/list.json"
SPEC_BASE = "https://api.apis.guru/v2/specs"
CACHE_DIR = os.path.join(HERE, ".cache")
INDEX_CACHE = os.path.join(CACHE_DIR, "api.apis.guru-v2-list.json")

# Curated selection: API keys exactly as they appear in the APIs.guru index.
# One representative spec per distinct real-world product (eBay and Google
# appear multiple times because each entry is a genuinely distinct service).
SELECTED = [
    "github.com",
    "spotify.com",
    "twitter.com:current",
    "slack.com",
    "stripe.com",
    "sendgrid.com",
    "twilio.com:api",
    "bitbucket.org",
    "box.com",
    "docker.com:engine",
    "docusign.net",
    "ebay.com:sell-account",
    "ebay.com:sell-fulfillment",
    "ebay.com:commerce-charity",
    "googleapis.com:youtube",
    "googleapis.com:gmail",
    "googleapis.com:calendar",
    "googleapis.com:drive",
    "googleapis.com:translate",
    "googleapis.com:vision",
    "googleapis.com:sheets",
    "googleapis.com:books",
    "googleapis.com:kgsearch",
    "okta.local",
    "xero.com:xero_accounting",
    "xero.com:xero-payroll-au",
    "klarna.com:payments",
    "svix.com",
    "telnyx.com",
    "openai.com",
    "impala.travel:hotels",
    "here.com:positioning",
    "visualcrossing.com:weather",
    "interzoid.com:getweathercity",
    "salesforce.local:einstein",
    "datumbox.com",
    "instagram.com",
    "gitlab.com",
    "here.com:tracking",
    "weatherbit.io",
]

# HTTP methods the Analyzer counts as operations (OpenApiParserService).
ANALYZED_METHODS = ("get", "put", "post", "delete", "options", "head", "patch")

MANIFEST_HEADER = ["api_id", "api_name", "source_url", "local_file",
                   "openapi_version", "endpoint_count"]


def slug(api_key: str) -> str:
    """Deterministic filesystem-safe id derived from the index key."""
    s = re.sub(r"[^a-z0-9]+", "-", api_key.lower()).strip("-")
    return s or "api"


def iso_now() -> str:
    return datetime.now(timezone.utc).strftime("%Y-%m-%dT%H:%M:%SZ")


def http_bytes(url: str, timeout: int = 120, retries: int = 2) -> bytes:
    last = None
    for attempt in range(retries + 1):
        try:
            req = urllib.request.Request(
                url,
                headers={
                    "Accept-Encoding": "identity",
                    "User-Agent": "research-dataset-builder/1.0",
                },
            )
            with urllib.request.urlopen(req, timeout=timeout) as resp:
                raw = resp.read()
                if resp.headers.get("Content-Encoding", "").lower() == "gzip":
                    raw = gzip.decompress(raw)
                return raw
        except Exception as exc:  # noqa: BLE001 - network layer, retry
            last = exc
    raise last


def load_index(force: bool = False) -> dict:
    if not force and os.path.exists(INDEX_CACHE):
        print(f"[index] cached: {INDEX_CACHE}")
        with open(INDEX_CACHE, "r", encoding="utf-8") as fh:
            return json.load(fh)
    os.makedirs(CACHE_DIR, exist_ok=True)
    print(f"[index] downloading {INDEX_URL} ...")
    raw = http_bytes(INDEX_URL)
    data = json.loads(raw)
    with open(INDEX_CACHE, "wb") as fh:
        fh.write(raw)
    print(f"[index] cached {len(raw)} bytes -> {INDEX_CACHE}")
    return data


def structural_check(spec: dict) -> list:
    """Mirror of what the Analyzer requires before it can parse a doc."""
    problems = []
    if not isinstance(spec, dict):
        problems.append("root is not an object")
        return problems
    if "openapi" not in spec and "swagger" not in spec:
        problems.append("missing 'openapi'/'swagger' version field")
    paths = spec.get("paths")
    if not isinstance(paths, dict):
        problems.append("'paths' is not an object")
    return problems


def count_endpoints(spec: dict) -> int:
    """Structural endpoint count (raw, $refs unresolved). The parser probe
    in finalize supersedes this with the Analyzer's own resolved counts."""
    total = 0
    for path_item in spec.get("paths", {}).values():
        if not isinstance(path_item, dict):
            continue
        for method in ANALYZED_METHODS:
            if isinstance(path_item.get(method), dict):
                total += 1
    return total


def resolve_preferred(index: dict, api_key: str) -> dict | None:
    entry = index.get(api_key)
    if not entry:
        return None
    versions = entry.get("versions", {})
    preferred = entry.get("preferred") or next(iter(versions), None)
    ver = versions.get(preferred) if preferred else None
    if preferred is None or ver is None:
        return None
    return {
        "key": api_key,
        "version_key": str(preferred),
        "openapi_ver": ver.get("openapiVer", ""),
        "updated": ver.get("updated", ""),
        "swagger_url": ver.get("swaggerUrl", ""),
        "title": (ver.get("info") or {}).get("title", ""),
    }


def cmd_download(args) -> int:
    index = load_index(force=args.force_index)
    print(f"[index] entries available: {len(index)}")

    resolved = []
    unresolved = []
    for key in SELECTED:
        info = resolve_preferred(index, key)
        if info is None:
            unresolved.append(key)
            continue
        info["api_id"] = slug(key)
        resolved.append(info)

    if unresolved:
        print("ERROR: not in index:", unresolved)
        return 1

    ids = Counter(r["api_id"] for r in resolved)
    dups = [i for i, c in ids.items() if c > 1]
    if dups:
        print("ERROR: non-unique api_id slug collisions:", dups)
        return 1

    specs_dir = os.path.join(HERE, "specs")
    os.makedirs(specs_dir, exist_ok=True)

    rows = []
    problems = []
    for r in resolved:
        if len(r["swagger_url"]) == 0:
            problems.append((r["api_id"], "preferred version has no swaggerUrl"))
            continue
        fname = "openapi.json"
        out = os.path.join(specs_dir, r["api_id"], fname)
        if args.force or not os.path.exists(out):
            os.makedirs(os.path.dirname(out), exist_ok=True)
            print(f"[fetch] {r['api_id']} <- {r['swagger_url']}")
            raw = http_bytes(r["swagger_url"])
            with open(out, "wb") as fh:
                fh.write(raw)
        else:
            print(f"[fetch] exists (skip): {out}")

        try:
            with open(out, "r", encoding="utf-8") as fh:
                spec = json.load(fh)
        except Exception as exc:  # noqa: BLE001 - recorded per file
            problems.append((r["api_id"], f"failed to decode JSON: {exc}"))
            continue

        issues = structural_check(spec)
        if issues:
            problems.append((r["api_id"], "; ".join(issues)))
            continue

        with open(out, "rb") as fh:
            raw = fh.read()
        # Byte-for-byte verification: the stored file must decode to the same
        # spec object that the structural check validated.
        if json.loads(raw) != spec:
            problems.append((r["api_id"], "byte-level re-read mismatch"))
            continue

        title = (spec.get("info") or {}).get("title", r["title"])
        version = spec.get("openapi") or spec.get("swagger") or ""
        rows.append({
            "api_id": r["api_id"],
            "api_name": title,
            "source_url": r["swagger_url"],
            "local_file": os.path.join("specs", r["api_id"], fname)
                              .replace("\\", "/"),
            "openapi_version": version,
            "endpoint_count": count_endpoints(spec),
        })

    # Duplicate removal: same normalized title twice in the selection is a
    # duplicate API; the second occurrence is dropped.
    seen = {}
    kept = []
    dropped = []
    for row in rows:
        norm = re.sub(r"[^a-z0-9]", "", (row["api_name"] or "").lower())
        if norm and norm in seen:
            dropped.append((row["api_id"], seen[norm], row["api_name"]))
            continue
        if norm:
            seen[norm] = row["api_id"]
        kept.append(row)

    with open(os.path.join(HERE, "selection.csv"), "w", newline="",
              encoding="utf-8") as fh:
        w = csv.writer(fh)
        w.writerow(["api_id", "api_key", "version_key", "openapi_ver",
                    "updated", "swagger_url"])
        for r in resolved:
            w.writerow([r["api_id"], r["key"], r["version_key"],
                        r["openapi_ver"], r["updated"], r["swagger_url"]])

    _write_manifest(kept)

    print(f"\n[download] resolved: {len(resolved)}"
          f" | structurally valid kept: {len(kept)}"
          f" | dropped as duplicates: {len(dropped)}")
    for api_id, first, title in dropped:
        print(f"  duplicate title '{title}': {api_id} dropped (first: {first})")
    if problems:
        print("[download] per-file problems:")
        for api_id, msg in problems:
            print(f"  {api_id}: {msg}")
        return 1
    print("[download] preliminary manifest.csv written.")
    return 0


def _write_manifest(rows: list) -> None:
    rows = sorted(rows, key=lambda r: r["api_id"])
    with open(os.path.join(HERE, "manifest.csv"), "w", newline="",
              encoding="utf-8") as fh:
        w = csv.DictWriter(fh, fieldnames=MANIFEST_HEADER)
        w.writeheader()
        w.writerows(rows)


def cmd_finalize(args) -> int:
    validation_path = os.path.join(HERE, "tool", "validation.json")
    if not os.path.exists(validation_path):
        print(f"ERROR: {validation_path} not found. Run the tool/ probe "
              "(see experiments/dataset/README.md) before 'finalize'.")
        return 1
    with open(validation_path, "r", encoding="utf-8") as fh:
        validation = json.load(fh)

    probe = validation.get("probe", "?")
    print(f"[finalize] probe: {probe}")
    by_id = {r["api_id"]: r for r in validation.get("results", [])}

    with open(os.path.join(HERE, "manifest.csv"), "r", newline="",
              encoding="utf-8") as fh:
        rows = list(csv.DictReader(fh))

    final = []
    excluded = []
    for row in rows:
        probe_row = by_id.get(row["api_id"])
        if probe_row is None:
            excluded.append((row["api_id"], "missing from probe output"))
            continue
        if not probe_row.get("ok"):
            messages = "; ".join(probe_row.get("messages", [])) or "parse failed"
            excluded.append((row["api_id"], messages))
            continue
        row["api_name"] = probe_row.get("title") or row["api_name"]
        row["openapi_version"] = probe_row.get("openapi")
        row["endpoint_count"] = str(probe_row.get("endpoint_count", 0))
        final.append(row)

    _write_manifest(final)

    ver_counts = Counter(r["openapi_version"] for r in final)
    total_endpoints = sum(int(r["endpoint_count"]) for r in final)
    print(f"[finalize] kept: {len(final)} | excluded: {len(excluded)}")
    print(f"[finalize] total endpoints (parser counts): {total_endpoints}")
    for v, c in sorted(ver_counts.items()):
        print(f"[finalize]   openapi {v}: {c} spec(s)")
    for api_id, reason in excluded:
        print(f"[finalize] EXCLUDED {api_id}: {reason}")

    if excluded:
        print("[finalize] WARNING: some specs were excluded from manifest.csv.")
        return 1
    print("[finalize] manifest.csv finalized with authoritative parse results.")
    return 0


def main(argv=None) -> int:
    parser = argparse.ArgumentParser(
        description="Reproducible acquisition of the real-world OpenAPI dataset.")
    sub = parser.add_subparsers(dest="command", required=True)

    d = sub.add_parser("download", help="resolve + download specs, write preliminary manifest")
    d.add_argument("--force-index", action="store_true",
                   help="re-download the APIs.guru index even if cached")
    d.add_argument("--force", action="store_true",
                   help="re-download spec files even if already present")
    d.set_defaults(func=cmd_download)

    f = sub.add_parser("finalize", help="apply tool/ probe results to manifest.csv")
    f.set_defaults(func=cmd_finalize)

    args = parser.parse_args(argv)
    return args.func(args)


if __name__ == "__main__":
    sys.exit(main())