import type { PracticeStatus } from "../../types";

interface StatusDonutProps {
  passed: number;
  failed: number;
  skipped: number;
}

const SEGMENTS: { key: PracticeStatus; label: string; color: string }[] = [
  { key: "PASSED", label: "Passed", color: "var(--color-success)" },
  { key: "FAILED", label: "Failed", color: "var(--color-danger)" },
  { key: "NOT_EVALUATED", label: "Skipped", color: "var(--color-neutral)" },
];

const RADIUS = 52;
const CIRCUMFERENCE = 2 * Math.PI * RADIUS;
const GAP = 2;

export default function StatusDonut(props: StatusDonutProps) {
  const counts: Record<PracticeStatus, number> = {
    PASSED: props.passed,
    FAILED: props.failed,
    MANUAL_REVIEW: 0,
    NOT_EVALUATED: props.skipped,
    NOT_APPLICABLE: 0,
  };
  const total = props.passed + props.failed + props.skipped;

  if (total === 0) {
    return (
      <div className="card">
        <h2 className="card-title">Status breakdown</h2>
        <div className="empty">No practices have been evaluated yet.</div>
      </div>
    );
  }

  let acc = 0;
  const arcs = SEGMENTS.filter((s) => counts[s.key] > 0).map((s) => {
    const frac = counts[s.key] / total;
    const dash = Math.max(frac * CIRCUMFERENCE - GAP, 1);
    const arc = (
      <circle
        key={s.key}
        cx="70"
        cy="70"
        r={RADIUS}
        fill="none"
        stroke={s.color}
        strokeWidth="14"
        strokeDasharray={`${dash} ${CIRCUMFERENCE - dash}`}
        strokeDashoffset={-acc * CIRCUMFERENCE}
        transform="rotate(-90 70 70)"
      >
        <title>{`${s.label}: ${counts[s.key]} (${Math.round(frac * 100)}%)`}</title>
      </circle>
    );
    acc += frac;
    return arc;
  });

  return (
    <div className="card">
      <h2 className="card-title">Status breakdown</h2>
      <div className="donut-wrap">
        <svg viewBox="0 0 140 140" role="img" aria-label="Distribution of rule statuses">
          <circle
            cx="70"
            cy="70"
            r={RADIUS}
            fill="none"
            className="gauge-track"
            strokeWidth="14"
          />
          {arcs}
        </svg>
        <div className="gauge-center" aria-hidden="true">
          <span className="gauge-value">{total}</span>
          <span className="gauge-unit">checks</span>
        </div>
      </div>
      <ul className="legend">
        {SEGMENTS.map((s) => (
          <li key={s.key} className="legend-item">
            <span className="legend-dot" style={{ background: s.color }} />
            <span className="legend-label">{s.label}</span>
            <span className="legend-count">
              {counts[s.key]} · {Math.round((counts[s.key] / total) * 100)}%
            </span>
          </li>
        ))}
      </ul>
      <p className="chart-note">
        “Skipped” covers rules that could not be evaluated for an endpoint.
      </p>
    </div>
  );
}