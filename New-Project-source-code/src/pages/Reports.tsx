import { useEffect, useState } from "react";
import { Link } from "react-router";
import { api } from "../services/api";
import type { ReportSummary } from "../types";
import LoadingSpinner from "../components/LoadingSpinner";
import ErrorAlert from "../components/ErrorAlert";
import EmptyState from "../components/EmptyState";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString(undefined, {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export default function Reports() {
  const [reports, setReports] = useState<ReportSummary[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    api
      .getReports()
      .then((list) => {
        if (!cancelled) setReports(list);
      })
      .catch((err: unknown) => {
        if (!cancelled)
          setError(err instanceof Error ? err.message : "Could not load reports.");
      })
      .finally(() => {
        if (!cancelled) setLoading(false);
      });
    return () => {
      cancelled = true;
    };
  }, []);

  return (
    <>
      <div className="card">
        <h1>Analysis Reports</h1>
        <p className="hero-desc">Previous analyses of OpenAPI specifications.</p>
      </div>

      {error && <ErrorAlert message={error} />}

      <div className="card">
        {loading && <LoadingSpinner label="Loading reports..." />}

        {!loading && reports.length === 0 && !error && (
          <EmptyState
            title="No analyses yet"
            message="Analyze your first OpenAPI specification to generate a report."
            actionLabel="Analyze API"
            to="/analyze"
          />
        )}

        {reports.length > 0 && (
          <div className="table-wrap">
            <table className="table">
              <thead>
                <tr>
                  <th>API</th>
                  <th>Version</th>
                  <th>Date</th>
                  <th>Score</th>
                  <th>Passed</th>
                  <th>Failed</th>
                  <th>
                    <span className="sr-only">Action</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {reports.map((r) => (
                  <tr key={r.analysisId}>
                    <td className="practice-name">{r.apiName}</td>
                    <td>{r.version}</td>
                    <td>{formatDate(r.analyzedAt)}</td>
                    <td className="num">{Math.round(r.score)}%</td>
                    <td className="num">{r.passedRules}</td>
                    <td className="num">{r.failedRules}</td>
                    <td>
                      <Link className="btn btn-secondary btn-sm" to={`/result/${r.analysisId}`}>
                        View
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </>
  );
}
