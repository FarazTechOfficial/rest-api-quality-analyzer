// ============================================================================
// Research practice catalog — static research metadata.
// Reconstructed from paper-notes/practice-catalog.md. The source paper reports
// 73 practices (35 automatable); the extraction from the reference works yields
// 83 rows (40 automatable) — the reconstruction is broader than a 1:1 copy of
// the paper's list, and NOT all entries can be tied back to the paper's exact
// 73. The counts in this file therefore describe the *extracted catalog*, not
// the paper's own figures.
//
// IMPORTANT: this is the *research catalog*, not a claim about the analyzer.
// Each row's `implemented` flag is derived from the rule→practice mapping in
// the actual Analyzer Service source (rule/impl/*.java), using the practice
// the rule most directly implements. The analyzer currently implements 18
// rules (REST-001..REST-018), of which 16 cover paper practice IDs. REST-005
// and REST-010 implement additional OpenAPI-specific checks (OPENAPI-01/02)
// that have no paper-practice counterpart.
// ============================================================================

import type { Practice } from "../types";

// practiceId -> ruleId. The rule is assigned to the practice it most directly
// implements, verified against the catalog rows (paper-notes/practice-catalog.md)
// and the Analyzer Service rule implementations.
const COVERED_BY_RULE: Record<string, string> = {
  "P-1.1": "REST-001",
  "P-1.2": "REST-002",
  "P-1.3": "REST-004",
  "P-1.4": "REST-008",
  "P-1.5": "REST-011",
  "P-1.6": "REST-012",
  "P-2.6": "REST-003",
  "P-3.1": "REST-009",
  "P-3.3": "REST-017",
  "P-5.1": "REST-007",
  "P-5.2": "REST-013",
  "P-5.4": "REST-014",
  "P-5.10": "REST-015",
  "P-6.4": "REST-006",
  "P-7.1": "REST-016",
  "P-7.2": "REST-018",
};

type Row = [
  id: string,
  name: string,
  automatable: boolean,
  priority: "HIGH" | "MEDIUM" | "LOW",
];

const CATEGORIES: { name: string; rows: Row[] }[] = [
  {
    name: "URI Design",
    rows: [
      ["P-1.1", "Use nouns for resources, avoid verbs in URIs", true, "HIGH"],
      ["P-1.2", "Use plural nouns for collection resources", true, "HIGH"],
      ["P-1.3", "Use lowercase letters in URI path segments", true, "HIGH"],
      ["P-1.4", "Do not use trailing slashes in URIs", true, "HIGH"],
      ["P-1.5", "Do not use file extensions in URIs (.json, .xml)", true, "HIGH"],
      ["P-1.6", "Use hyphens, not underscores, for word separation", true, "MEDIUM"],
      ["P-1.7", "Keep URIs shallow (max 3 levels recommended)", true, "LOW"],
      ["P-1.8", "Use query parameters for filtering, sorting, pagination", false, "MEDIUM"],
      ["P-1.9", "Avoid query string for resource identification", true, "MEDIUM"],
      ["P-1.10", "Use consistent naming conventions throughout API", false, "MEDIUM"],
      ["P-1.11", "Singular nouns for singleton resources", false, "LOW"],
      ["P-1.12", "Use sub-resources for nested relationships", false, "LOW"],
      ["P-1.13", "Do not expose internal implementation details in URIs", false, "HIGH"],
    ],
  },
  {
    name: "HTTP Methods",
    rows: [
      ["P-2.1", "Use GET for retrieval (safe, idempotent)", true, "HIGH"],
      ["P-2.2", "Use POST for creation", true, "HIGH"],
      ["P-2.3", "Use PUT for full update (idempotent)", true, "MEDIUM"],
      ["P-2.4", "Use PATCH for partial update", true, "MEDIUM"],
      ["P-2.5", "Use DELETE for removal (idempotent)", true, "HIGH"],
      ["P-2.6", "GET should not have request body", true, "HIGH"],
      ["P-2.7", "DELETE should not have request body", true, "HIGH"],
      ["P-2.8", "POST should typically have request body", true, "MEDIUM"],
      ["P-2.9", "Use HEAD for resource metadata retrieval", false, "LOW"],
      ["P-2.10", "Use OPTIONS for CORS preflight", false, "LOW"],
      ["P-2.11", "PUT replaces entire resource", false, "MEDIUM"],
      ["P-2.12", "PATCH only modifies specified fields", false, "MEDIUM"],
      ["P-2.13", "POST is not idempotent by design", false, "LOW"],
      ["P-2.14", "DELETE should remove the resource", false, "LOW"],
      ["P-2.15", "GET must not modify server state", false, "HIGH"],
      ["P-2.16", "Support HEAD on any GET endpoint", false, "LOW"],
      ["P-2.17", "Support OPTIONS for all endpoints", false, "LOW"],
      ["P-2.18", "Do not tunnel operations through POST", false, "MEDIUM"],
    ],
  },
  {
    name: "Metadata & Versioning",
    rows: [
      ["P-3.1", "Define API version", true, "HIGH"],
      ["P-3.2", "Use URI versioning (e.g. /v1/)", true, "HIGH"],
      ["P-3.3", "Use header versioning", false, "MEDIUM"],
      ["P-3.4", "Use media type versioning", false, "MEDIUM"],
      ["P-3.5", "Provide API documentation", true, "HIGH"],
      ["P-3.6", "Document all endpoints", true, "HIGH"],
      ["P-3.7", "Document request/response schemas", true, "HIGH"],
      ["P-3.8", "Provide example values", false, "MEDIUM"],
      ["P-3.9", "Document error codes", true, "HIGH"],
      ["P-3.10", "Use consistent response format", false, "MEDIUM"],
      ["P-3.11", "Include rate limiting headers", false, "MEDIUM"],
      ["P-3.12", "Document pagination mechanism", false, "MEDIUM"],
    ],
  },
  {
    name: "Representation",
    rows: [
      ["P-4.1", "Support JSON format", true, "HIGH"],
      ["P-4.2", "Use content negotiation", true, "MEDIUM"],
      ["P-4.3", "Support XML format", true, "LOW"],
      ["P-4.4", "Use HAL/HATEOAS for discoverability", false, "LOW"],
      ["P-4.5", "Provide resource links (hypermedia)", false, "LOW"],
      ["P-4.6", "Support gzip compression", false, "MEDIUM"],
      ["P-4.7", "Use proper Content-Type headers", true, "HIGH"],
      ["P-4.8", "Avoid proprietary media types", false, "LOW"],
    ],
  },
  {
    name: "Status Codes",
    rows: [
      ["P-5.1", "Use 200 for successful GET", true, "HIGH"],
      ["P-5.2", "Use 201 for successful POST (creation)", true, "HIGH"],
      ["P-5.3", "Use 202 for async operations", false, "MEDIUM"],
      ["P-5.4", "Use 204 for successful DELETE", true, "HIGH"],
      ["P-5.5", "Use 301 for permanent redirects", false, "LOW"],
      ["P-5.6", "Use 304 for conditional GET (ETag)", false, "LOW"],
      ["P-5.7", "Use 400 for bad requests", true, "HIGH"],
      ["P-5.8", "Use 401 for unauthenticated", true, "HIGH"],
      ["P-5.9", "Use 403 for unauthorized", true, "HIGH"],
      ["P-5.10", "Use 404 for not found", true, "HIGH"],
      ["P-5.11", "Use 405 for method not allowed", false, "LOW"],
      ["P-5.12", "Use 409 for conflicts", false, "MEDIUM"],
      ["P-5.13", "Use 429 for rate limiting", false, "MEDIUM"],
      ["P-5.14", "Use 500 for server errors", true, "HIGH"],
    ],
  },
  {
    name: "Error Handling",
    rows: [
      ["P-6.1", "Provide structured error responses", true, "HIGH"],
      ["P-6.2", "Include error code in response body", true, "HIGH"],
      ["P-6.3", "Include human-readable error message", true, "HIGH"],
      ["P-6.4", "Document all possible error responses", true, "HIGH"],
      ["P-6.5", "Use consistent error format across API", false, "MEDIUM"],
      ["P-6.6", "Include request ID for tracing", false, "LOW"],
      ["P-6.7", "Provide error links/docs URLs", false, "LOW"],
      ["P-6.8", "Validate input and return 400 with details", false, "MEDIUM"],
      ["P-6.9", "Do not expose stack traces in production", false, "HIGH"],
      ["P-6.10", "Use RFC 7807 Problem Details format", false, "MEDIUM"],
    ],
  },
  {
    name: "Security & Documentation",
    rows: [
      ["P-7.1", "Use HTTPS for all endpoints", true, "HIGH"],
      ["P-7.2", "Define authentication mechanism", true, "HIGH"],
      ["P-7.3", "Implement OAuth2 / JWT", false, "HIGH"],
      ["P-7.4", "Rate limit API access", false, "MEDIUM"],
      ["P-7.5", "Validate and sanitize all inputs", false, "HIGH"],
      ["P-7.6", "Use CORS headers properly", false, "MEDIUM"],
      ["P-7.7", "Provide SDK/client libraries", false, "LOW"],
      ["P-7.8", "Document authentication flow", false, "MEDIUM"],
    ],
  },
];

export const PRACTICES: Practice[] = CATEGORIES.flatMap((category) =>
  category.rows.map(([id, name, automatable, priority]) => {
    const ruleId = COVERED_BY_RULE[id] ?? null;
    return {
      id,
      name,
      category: category.name,
      automatable,
      priority,
      implemented: ruleId !== null,
      ruleId,
    };
  })
);

export const RESEARCH_CATALOG_COUNT: number = PRACTICES.length;
export const AUTOMATABLE_PRACTICES_COUNT: number = PRACTICES.filter(
  (p) => p.automatable
).length;
export const IMPLEMENTED_PRACTICES_COUNT: number = PRACTICES.filter(
  (p) => p.implemented
).length;

// Number of rules implemented in the Analyzer Service (REST-001..REST-018).
export const IMPLEMENTED_RULES_COUNT = 18;

// Practice ID -> category name, used to label backend rule results.
const CATEGORY_BY_PREFIX: Record<string, string> = {
  "P-1": "URI Design",
  "P-2": "HTTP Methods",
  "P-3": "Metadata & Versioning",
  "P-4": "Representation",
  "P-5": "Status Codes",
  "P-6": "Error Handling",
  "P-7": "Security & Documentation",
  OPENAPI: "OpenAPI conventions",
};

export function categoryForPracticeId(practiceId: string): string {
  if (!practiceId) return "";
  const fromCatalog = PRACTICES.find((p) => p.id === practiceId);
  if (fromCatalog) return fromCatalog.category;
  const prefix = practiceId.startsWith("OPENAPI")
    ? "OPENAPI"
    : practiceId.slice(0, 3);
  return CATEGORY_BY_PREFIX[prefix] ?? "";
}