import type { PracticeStatus } from "../types";

const LABELS: Record<PracticeStatus, string> = {
  PASSED: "PASSED",
  FAILED: "FAILED",
  MANUAL_REVIEW: "MANUAL REVIEW",
  NOT_EVALUATED: "NOT EVALUATED",
  NOT_APPLICABLE: "NOT APPLICABLE",
};

export default function StatusBadge({ status }: { status: PracticeStatus }) {
  const label = LABELS[status] ?? status;
  return (
    <span className={`badge badge-${status.toLowerCase()}`}>
      <span className="badge-dot" aria-hidden="true" />
      {label}
    </span>
  );
}
