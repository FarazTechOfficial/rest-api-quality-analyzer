import type { RuleResult } from "../../types";

interface CategoryStat {
  name: string;
  total: number;
  passed: number;
  failed: number;
  rate: number;
}

function buildStats(results: RuleResult[]): CategoryStat[] {
  const map = new Map<string, CategoryStat>();
  for (const r of results) {
    const stat =
      map.get(r.category) ??
      { name: r.category, total: 0, passed: 0, failed: 0, rate: 0 };
    stat.total += 1;
    if (r.status === "PASSED") stat.passed += 1;
    else if (r.status === "FAILED") stat.failed += 1;
    map.set(r.category, stat);
  }
  return [...map.values()]
    .map((s) => ({ ...s, rate: s.total ? Math.round((s.passed / s.total) * 100) : 0 }))
    .sort((a, b) => b.rate - a.rate || b.total - a.total);
}

export default function CategoryBars({ results }: { results: RuleResult[] }) {
  const stats = buildStats(results);

  if (stats.length === 0) {
    return (
      <div className="card">
        <h2 className="card-title">Pass rate by category</h2>
        <div className="empty">No practice results to chart.</div>
      </div>
    );
  }

  return (
    <div className="card">
      <h2 className="card-title">Pass rate by category</h2>
      <div className="hbars">
        {stats.map((s) => {
          const passW = s.total ? (s.passed / s.total) * 100 : 0;
          const failW = s.total ? (s.failed / s.total) * 100 : 0;
          return (
            <div
              key={s.name}
              className="hbar-row"
              title={`${s.name}: ${s.passed} of ${s.total} passed`}
            >
              <span className="hbar-label">{s.name}</span>
              <div
                className="hbar-track"
                role="img"
                aria-label={`${s.name}: ${s.passed} of ${s.total} passed`}
              >
                <span className="hbar-pass" style={{ width: `${passW}%` }} />
                <span className="hbar-fail" style={{ width: `${failW}%` }} />
              </div>
              <span className="hbar-value">{s.rate}%</span>
            </div>
          );
        })}
      </div>
      <p className="chart-note">
        Green = passed, red = failed; the light track is skipped evaluation.
      </p>
    </div>
  );
}
