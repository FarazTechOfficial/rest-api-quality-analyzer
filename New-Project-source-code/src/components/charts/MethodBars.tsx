import type { RuleResult } from "../../types";

const METHOD_ORDER = ["GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "HEAD", "TRACE"];
const CHART_HEIGHT = 155;

function buildMethods(results: RuleResult[]): { method: string; count: number }[] {
  const map = new Map<string, number>();
  for (const r of results) {
    const method = (r.method || "ANY").toUpperCase();
    map.set(method, (map.get(method) ?? 0) + 1);
  }
  return [...map.entries()]
    .map(([method, count]) => ({ method, count }))
    .sort((a, b) => {
      const ia = METHOD_ORDER.indexOf(a.method);
      const ib = METHOD_ORDER.indexOf(b.method);
      if (ia === -1 && ib === -1) return a.method.localeCompare(b.method);
      if (ia === -1) return 1;
      if (ib === -1) return -1;
      return ia - ib;
    });
}

export default function MethodBars({ results }: { results: RuleResult[] }) {
  const methods = buildMethods(results);
  const max = methods.reduce((m, e) => Math.max(m, e.count), 0);

  if (methods.length === 0) {
    return (
      <div className="card">
        <h2 className="card-title">Coverage by HTTP method</h2>
        <div className="empty">No endpoint data to chart.</div>
      </div>
    );
  }

  return (
    <div className="card">
      <h2 className="card-title">Coverage by HTTP method</h2>
      <div className="mbars">
        {methods.map((e) => (
          <div key={e.method} className="mbar-col">
            <span className="mbar-count">{e.count}</span>
            <div
              className="mbar-bar"
              role="img"
              aria-label={`${e.count} checks for ${e.method}`}
              title={`${e.method}: ${e.count} checks`}
              style={{ height: `${Math.max(4, (e.count / max) * CHART_HEIGHT)}px` }}
            />
            <span className="mbar-label">{e.method}</span>
          </div>
        ))}
      </div>
      <p className="chart-note">
        Number of practice checks recorded per HTTP method. “ANY” covers
        whole-specification checks.
      </p>
    </div>
  );
}
