import { useEffect, useMemo, useState } from "react";
import { Link, useParams } from "react-router";
import { api } from "../services/api";
import type { AnalysisReport, PracticeStatus, RuleResult } from "../types";
import StatusBadge from "../components/StatusBadge";
import ScoreBar from "../components/ScoreBar";
import LoadingSpinner from "../components/LoadingSpinner";
import ErrorAlert from "../components/ErrorAlert";
import ScoreGauge from "../components/charts/ScoreGauge";
import StatusDonut from "../components/charts/StatusDonut";
import CategoryBars from "../components/charts/CategoryBars";
import MethodBars from "../components/charts/MethodBars";

const FILTERS: { key: PracticeStatus | "ALL"; label: string }[] = [
  { key: "ALL", label: "All" },
  { key: "PASSED", label: "Passed" },
  { key: "FAILED", label: "Failed" },
];

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString(undefined, {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export default function Result() {
  const { id } = useParams<{ id: string }>();
  const [report, setReport] = useState<AnalysisReport | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [status, setStatus] = useState<PracticeStatus | "ALL">("ALL");
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<RuleResult | null>(null);

  useEffect(() => {
    let cancelled = false;
    setLoading(true);
    setError("");
    api
      .getReport(id ?? "")
      .then((r) => {
        if (!cancelled) {
          setReport(r);
          setSelected(null);
        }
      })
      .catch((err: unknown) => {
        if (!cancelled)
          setError(err instanceof Error ? err.message : "Could not load the report.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, [id]);

  const filtered = useMemo(() => {
    if (!report) return [];
    const q = query.trim().toLowerCase();
    return report.results.filter((r) => {
      if (status !== "ALL" && r.status !== status) return false;
      if (!q) return true;
      return [r.ruleName, r.ruleId, r.practiceId, r.endpoint, r.method, r.category].some(
        (v) => v.toLowerCase().includes(q)
      );
    });
  }, [report, status, query]);

  if (loading) return <LoadingSpinner label="Loading analysis report..." />;

  if (error) {
    return (
      <>
        <ErrorAlert message={error} />
        <Link className="btn btn-secondary" to="/reports">
          Back to reports
        </Link>
      </>
    );
  }

  if (!report) {
    return (
      <>
        <ErrorAlert message="The requested report could not be found." />
        <Link className="btn btn-secondary" to="/reports">
          Back to reports
        </Link>
      </>
    );
  }

  return (
    <>
      <div className="card">
        <h1>{report.apiName}</h1>
        <p className="meta">
          Version {report.version} · Analyzed on {formatDate(report.analyzedAt)}
        </p>
        <ScoreBar score={report.score} />
        <div className="summary-chips">
          <span className="chip">
            <b>{report.passedRules}</b> Passed
          </span>
          <span className="chip">
            <b>{report.failedRules}</b> Failed
          </span>
          <span className="chip">
            <b>{report.skippedRules}</b> Skipped
          </span>
        </div>
      </div>

      <div className="charts-grid">
        <ScoreGauge score={report.score} />
        <StatusDonut
          passed={report.passedRules}
          failed={report.failedRules}
          skipped={report.skippedRules}
        />
        <CategoryBars results={report.results} />
        <MethodBars results={report.results} />
      </div>

      <div className="card">
        <h2 className="card-title">Rule results</h2>

        <div className="toolbar">
          <input
            className="input search"
            type="search"
            placeholder="Search rule, endpoint or method..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            aria-label="Search rule results"
          />
        </div>

        <div className="tabs" role="group" aria-label="Filter by status">
          {FILTERS.map((f) => (
            <button
              key={f.key}
              className={"tab" + (status === f.key ? " active" : "")}
              onClick={() => setStatus(f.key)}
              aria-pressed={status === f.key}
            >
              {f.label}
            </button>
          ))}
        </div>

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>Rule</th>
                <th>Status</th>
                <th>Endpoint</th>
                <th>Method</th>
                <th>
                  <span className="sr-only">Details</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={5}>
                    <div className="empty">
                      No results match the current filter. Try a different status or
                      search term.
                    </div>
                  </td>
                </tr>
              )}
              {filtered.map((r) => (
                <tr
                  key={`${r.ruleId}-${r.endpoint}-${r.method}`}
                  className="clickable"
                  onClick={() => setSelected(r)}
                >
                  <td>
                    <div className="practice-name">{r.ruleName}</div>
                    <div className="meta small">
                      {r.ruleId} · {r.category}
                    </div>
                  </td>
                  <td>
                    <StatusBadge status={r.status} />
                  </td>
                  <td className="mono">{r.endpoint || "—"}</td>
                  <td>{r.method || "—"}</td>
                  <td>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        setSelected(r);
                      }}
                    >
                      Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {selected && (
        <div className="detail" aria-live="polite">
          <div className="detail-head">
            <h2 className="card-title">{selected.ruleName}</h2>
            <StatusBadge status={selected.status} />
            <button className="btn btn-secondary btn-sm" onClick={() => setSelected(null)}>
              Close
            </button>
          </div>
          <div className="detail-row">
            <span className="detail-label">Rule ID</span>
            <span className="mono">{selected.ruleId}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Practice ID</span>
            <span className="mono">{selected.practiceId}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Category</span>
            <span>{selected.category}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Endpoint</span>
            <span className="mono">{selected.endpoint || "—"}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Method</span>
            <span>{selected.method || "—"}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Message</span>
            <span>{selected.message}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Recommendation</span>
            <span>{selected.recommendation}</span>
          </div>
        </div>
      )}
    </>
  );
}