import { useEffect, useState } from "react";
import { Link } from "react-router";
import { api } from "../services/api";
import type { DashboardData } from "../services/api";
import ErrorAlert from "../components/ErrorAlert";
import EmptyState from "../components/EmptyState";

function formatDate(iso: string): string {
  return new Date(iso).toLocaleDateString(undefined, {
    day: "2-digit",
    month: "short",
    year: "numeric",
  });
}

export default function Dashboard() {
  const [data, setData] = useState<DashboardData | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;

    api
      .getDashboardData()
      .then((d) => {
        if (!cancelled) setData(d);
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

  const reports = data?.reports ?? [];
  const stats = data?.stats ?? null;

  return (
    <>
      <div className="card hero-card">
        <h1>REST API Design Quality Analyzer</h1>
        <p className="hero-desc">
          Analyze REST API specifications against research-based design practices and
          inspect the resulting compliance report.
        </p>
        <div className="actions">
          <Link className="btn btn-primary" to="/analyze">
            Analyze API
          </Link>
          <Link className="btn btn-secondary" to="/reports">
            View Reports
          </Link>
          <Link className="btn btn-secondary" to="/practices">
            View Practices
          </Link>
        </div>
      </div>

      {stats && (
        <div className="stats">
          <div className="card stat-card">
            <div className="stat-value">{stats.totalPractices}</div>
            <div className="stat-label">Catalog practices</div>
          </div>
          <div className="card stat-card">
            <div className="stat-value">{stats.implementedPractices}</div>
            <div className="stat-label">Practices automated</div>
          </div>
          <div className="card stat-card">
            <div className="stat-value">{stats.analyzedApis}</div>
            <div className="stat-label">APIs analyzed</div>
          </div>
        </div>
      )}

      <div className="card">
        <h2 className="card-title">Recent analyses</h2>
        {error && <ErrorAlert message={error} />}
        {loading && <p className="meta">Loading reports...</p>}
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
                  <th>Score</th>
                  <th>Date</th>
                  <th>
                    <span className="sr-only">Action</span>
                  </th>
                </tr>
              </thead>
              <tbody>
                {reports.slice(0, 5).map((r) => (
                  <tr key={r.analysisId}>
                    <td>{r.apiName}</td>
                    <td>{r.version}</td>
                    <td className="num">{Math.round(r.score)}%</td>
                    <td>{formatDate(r.analyzedAt)}</td>
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
