export default function ScoreBar({ score }: { score: number }) {
  const safeScore = Math.max(0, Math.min(100, Math.round(score)));
  return (
    <div className="score-row">
      <div>
        <div className="meta small">Compliance score</div>
        <div className="score-number">
          {safeScore}
          <span className="meta"> / 100</span>
        </div>
      </div>
      <div>
        <div className="scorebar-track" role="img" aria-label={`Compliance score ${safeScore} out of 100`}>
          <div className="scorebar-fill" style={{ width: `${safeScore}%` }} />
        </div>
        <p className="meta small" style={{ marginTop: 8 }}>
          Based on evaluated practices. This is a measurement of the current analysis —
          it is not a universal quality judgment of the API.
        </p>
      </div>
    </div>
  );
}
