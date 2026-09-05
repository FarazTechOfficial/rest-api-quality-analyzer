import { NavLink } from "react-router";

export default function Nav() {
  const linkClass = ({ isActive }: { isActive: boolean }): string =>
    "nav-link" + (isActive ? " active" : "");

  return (
    <header className="topbar">
      <div className="container topbar-inner">
        <span className="brand">
          REST API Design Quality Analyzer
          <small>research prototype</small>
        </span>
        <nav className="nav" aria-label="Main navigation">
          <NavLink to="/" end className={linkClass}>
            Dashboard
          </NavLink>
          <NavLink to="/analyze" className={linkClass}>
            Analyze API
          </NavLink>
          <NavLink to="/reports" className={linkClass}>
            Reports
          </NavLink>
          <NavLink to="/practices" className={linkClass}>
            Practices
          </NavLink>
        </nav>
      </div>
    </header>
  );
}
