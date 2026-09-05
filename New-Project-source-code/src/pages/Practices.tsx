import { useMemo, useState } from "react";
import {
  AUTOMATABLE_PRACTICES_COUNT,
  IMPLEMENTED_PRACTICES_COUNT,
  IMPLEMENTED_RULES_COUNT,
  PRACTICES,
  RESEARCH_CATALOG_COUNT,
} from "../data/practicesCatalog";
import type { Practice } from "../types";

export default function Practices() {
  const [query, setQuery] = useState("");
  const [selected, setSelected] = useState<Practice | null>(null);
  const [hideNotImplemented, setHideNotImplemented] = useState(false);

  const filtered = useMemo(() => {
    const q = query.trim().toLowerCase();
    return PRACTICES.filter((p) => {
      if (hideNotImplemented && !p.implemented) return false;
      if (!q) return true;
      return [p.id, p.name, p.category].some((v) => v.toLowerCase().includes(q));
    });
  }, [query, hideNotImplemented]);

  return (
    <>
      <div className="card">
        <h1>REST API Design Practices</h1>
        <p className="hero-desc">
          Research-based design practices reconstructed from the reference
          works. The source paper reports 73 practices (35 automatable); this
          extracted catalog lists 83 rows because the reconstruction draws on
          the broader rule references and is not a one-to-one copy of the
          paper's list. The counts below therefore describe this catalog, not
          the paper's figures. Only the practices with an implemented rule are
          actually evaluated by the analyzer.
        </p>
        <div className="stats stats-four">
          <div className="card stat-card">
            <div className="stat-value">{RESEARCH_CATALOG_COUNT}</div>
            <div className="stat-label">Catalog rows</div>
          </div>
          <div className="card stat-card">
            <div className="stat-value">{AUTOMATABLE_PRACTICES_COUNT}</div>
            <div className="stat-label">Automatable rows</div>
          </div>
          <div className="card stat-card">
            <div className="stat-value">{IMPLEMENTED_PRACTICES_COUNT}</div>
            <div className="stat-label">Practices automated</div>
          </div>
          <div className="card stat-card">
            <div className="stat-value">{IMPLEMENTED_RULES_COUNT}</div>
            <div className="stat-label">Rule checks implemented</div>
          </div>
        </div>
      </div>

      <div className="card">
        <div className="toolbar">
          <input
            className="input search"
            type="search"
            placeholder="Search by ID, name or category..."
            value={query}
            onChange={(e) => setQuery(e.target.value)}
            aria-label="Search practices"
          />
          <label className="check-row">
            <input
              type="checkbox"
              checked={hideNotImplemented}
              onChange={(e) => setHideNotImplemented(e.target.checked)}
            />
            Only implemented
          </label>
          <span className="meta small">{filtered.length} practice(s) shown</span>
        </div>

        <div className="table-wrap">
          <table className="table">
            <thead>
              <tr>
                <th>ID</th>
                <th>Practice</th>
                <th>Category</th>
                <th>Automatable</th>
                <th>Implementation</th>
                <th>
                  <span className="sr-only">Details</span>
                </th>
              </tr>
            </thead>
            <tbody>
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={6}>
                    <div className="empty">No practices match your search.</div>
                  </td>
                </tr>
              )}
              {filtered.map((p) => (
                <tr key={p.id} className="clickable" onClick={() => setSelected(p)}>
                  <td className="mono">{p.id}</td>
                  <td className="practice-name">{p.name}</td>
                  <td>{p.category}</td>
                  <td>{p.automatable ? "Yes" : "Manual"}</td>
                  <td>
                    <span
                      className={
                        "badge " +
                        (p.implemented
                          ? "badge-passed"
                          : "badge-not_evaluated")
                      }
                    >
                      <span className="badge-dot" aria-hidden="true" />
                      {p.implemented ? "IMPLEMENTED" : "NOT IMPLEMENTED"}
                    </span>
                  </td>
                  <td>
                    <button
                      className="btn btn-secondary btn-sm"
                      onClick={(e) => {
                        e.stopPropagation();
                        setSelected(p);
                      }}
                    >
                      Details
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {selected && (
        <div className="detail" aria-live="polite">
          <div className="detail-head">
            <h2 className="card-title">{selected.name}</h2>
            <button className="btn btn-secondary btn-sm" onClick={() => setSelected(null)}>
              Close
            </button>
          </div>
          <div className="detail-row">
            <span className="detail-label">Practice ID</span>
            <span className="mono">{selected.id}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Category</span>
            <span>{selected.category}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Automatable</span>
            <span>{selected.automatable ? "Yes" : "No (manual review)"}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Priority</span>
            <span>{selected.priority}</span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Implementation</span>
            <span>
              {selected.implemented && selected.ruleId
                ? `Implemented — analyzed by rule ${selected.ruleId}`
                : "Not implemented in the analyzer"}
            </span>
          </div>
          <div className="detail-row">
            <span className="detail-label">Research traceability</span>
            <span className="small">
              Research practice → software implementation → analysis result. The automated
              check follows the practice as implemented in this tool; it is not a claim
              that the original research methodology is reproduced exactly.
            </span>
          </div>
        </div>
      )}
    </>
  );
}