interface ScoreGaugeProps {
  score: number;
}

/** Semantic color for a score: green ≥ 80, amber ≥ 50, red below. */
function tone(score: number): string {
  if (score >= 80) return "var(--color-success)";
  if (score >= 50) return "var(--color-warning)";
  return "var(--color-danger)";
}

export default function ScoreGauge({ score }: ScoreGaugeProps) {
  const clamped = Math.max(0, Math.min(100, Math.round(score)));
  const r = 52;
  const circumference = 2 * Math.PI * r;
  const filled = (clamped / 100) * circumference;

  return (
    <div className="card">
      <h2 className="card-title">Overall score</h2>
      <div className="gauge-wrap">
        <svg viewBox="0 0 140 140" role="img" aria-label={`Overall score ${clamped} out of 100`}>
          <circle
            cx="70"
            cy="70"
            r={r}
            fill="none"
            className="gauge-track"
            strokeWidth="12"
          />
          <circle
            cx="70"
            cy="70"
            r={r}
            fill="none"
            strokeWidth="12"
            stroke={tone(clamped)}
            strokeLinecap={clamped === 0 ? "butt" : "round"}
            strokeDasharray={`${filled} ${circumference - filled}`}
            transform="rotate(-90 70 70)"
          />
        </svg>
        <div className="gauge-center" aria-hidden="true">
          <span className="gauge-value">{clamped}</span>
          <span className="gauge-unit">out of 100</span>
        </div>
      </div>
      <p className="chart-note">Share of evaluated practices that passed.</p>
    </div>
  );
}
