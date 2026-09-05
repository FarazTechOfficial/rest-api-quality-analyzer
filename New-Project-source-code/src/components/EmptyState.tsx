import { Link } from "react-router";

interface Props {
  title: string;
  message: string;
  actionLabel?: string;
  to?: string;
}

export default function EmptyState({ title, message, actionLabel, to }: Props) {
  return (
    <div className="empty">
      <div className="empty-title">{title}</div>
      <p>{message}</p>
      {actionLabel && to && (
        <div className="empty-action">
          <Link className="btn btn-primary" to={to}>
            {actionLabel}
          </Link>
        </div>
      )}
    </div>
  );
}
