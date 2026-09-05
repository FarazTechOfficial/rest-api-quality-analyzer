export default function LoadingSpinner({ label }: { label?: string }) {
  return (
    <div className="loading" role="status" aria-live="polite">
      <div className="spinner" aria-hidden="true" />
      {label && <div className="loading-label">{label}</div>}
    </div>
  );
}
