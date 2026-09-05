export default function ErrorAlert({ message }: { message: string }) {
  if (!message) return null;
  return (
    <div className="alert alert-error" role="alert">
      <span aria-hidden="true">!</span>
      <span>{message}</span>
    </div>
  );
}
