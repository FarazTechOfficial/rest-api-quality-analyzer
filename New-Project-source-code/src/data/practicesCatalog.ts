// ============================================================================
// Research practice catalog — static research metadata.
//
// The source paper (Petrillo et al., ICSOC 2016, LNCS 9936) catalogues
// exactly 73 REST API design practices in 5 categories (see
// paper-notes/paper-catalog.md for the authoritative transcription).
//
// This file contains a BROADER RECONSTRUCTION (83 rows) derived from the
// reference works in paper-notes/practice-catalog.md. The reconstruction
// is NOT a 1:1 copy of the paper's 73 practices; the P-x.y practice IDs
// used here are THIS project's own labels for the reconstructed rows and
// must NOT be confused with the paper's practice IDs.
//
// Each row's `implemented` flag means: an implemented rule in the Analyzer
// Service evaluates this reconstructed concept. The Analyzer implements 21
// rules (REST-001..REST-021), of which 16 map to paper-practice IDs
// (U-/RM-/E-/H-/O-) and 5 are OpenAPI-spec-specific checks
// (OPENAPI-01/02/04/05/06). See paper-notes/paper-catalog.md for the
// authoritative rule→practice mapping.
// ============================================================================

import type { Practice } from "../types";

// practiceId -> ruleId.  Each entry means: the reconstructed catalog row
// (identified by its P-x.y id) is conceptually evaluated by the named rule.
// This is NOT a claim that the paper practice is reproduced; it means the
// analyzer checks the same design concern that the row describes.
// Added: 2026-09-06 — recovery of P-2.7 (already implemented via REST-003);
//                      2026-09-06 Priority 1 added P-4.1/P-3.12/P-4.7.
const COVERED_BY_RULE: Record<string, string> = {
  "P-1.1": "REST-001",   // verbs in URIs (conceptual)
  "P-1.2": "REST-002",   // plural collection names (conceptual)
  "P-1.3": "REST-004",   // lowercase path segments (conceptual)
  "P-1.4": "REST-008",   // no trailing slash (conceptual)
  "P-1.5": "REST-011",   // no file extension (conceptual)
  "P-1.6": "REST-012",   // no underscore (conceptual)
  "P-2.6": "REST-003",   // GET must not carry a body (RM-2)
  "P-2.7": "REST-003",   // DELETE must not carry a body (implementation check)
  "P-3.1": "REST-009",   // API version must be defined (conceptual)
  "P-3.12": "REST-020",  // pagination mechanism via query params (O-13)
  "P-4.1": "REST-019",   // JSON representations (O-8)
  "P-4.7": "REST-021",   // proper Content-Type headers on GET responses (H-2)
  "P-5.2": "REST-013",   // POST returns 201 (conceptual)
  "P-5.4": "REST-014",   // DELETE returns 204 when body empty (conceptual)
  "P-5.10": "REST-015",  // 404 for not found (conceptual)
  "P-6.4": "REST-006",   // error responses documented (conceptual)
  "P-7.1": "REST-016",   // HTTPS (conceptual)
  "P-7.2": "REST-018",   // security scheme defined (conceptual)
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

// Number of practices in the source paper (Petrillo et al., ICSOC 2016).
export const PAPER_PRACTICE_COUNT = 73;

// Number of the paper's 73 practices that our 21 rules map to directly.
// 16 paper practices (U-2, U-4, U-5, U-6, U-7, U-9, U-12, RM-2, E-3, E-5,
// E-11, O-10, O-14, O-8, O-13, H-2) + 5 OpenAPI-specific rules
// (OPENAPI-01/02/04/05/06).
// Source: paper-notes/paper-catalog.md rule→practice mapping.
export const PAPER_PRACTICES_EVALUATED_COUNT = 16;

// Number of rows in this reconstructed catalog (not the paper's count).
export const RESEARCH_CATALOG_COUNT: number = PRACTICES.length;

// Number of reconstructed catalog rows flagged automatable (reconstruction
// heuristic; this is NOT a figure from the paper).
export const AUTOMATABLE_PRACTICES_COUNT: number = PRACTICES.filter(
  (p) => p.automatable
).length;

// Number of reconstructed catalog rows with a rule covering the concept.
export const IMPLEMENTED_PRACTICES_COUNT: number = PRACTICES.filter(
  (p) => p.implemented
).length;

// Number of distinct rules implemented in the Analyzer Service.
export const IMPLEMENTED_RULES_COUNT = 21;

// Practice ID -> category name, used to label backend rule results.
// Handles both the reconstruction P-x.y ids (used in the catalog table) and
// the new paper/OPENAPI ids (U-*, RM-*, E-*, H-*, O-*, OPENAPI-*) returned
// by the Analyzer Service after the 2026-09-06 practiceId fix.
const CATEGORY_BY_PREFIX: Record<string, string> = {
  U: "URI Design",
  RM: "Request Methods",
  E: "Error Handling",
  H: "HTTP Headers",
  O: "Others",
  OPENAPI: "OpenAPI conventions",
  "P-1": "URI Design",
  "P-2": "HTTP Methods",
  "P-3": "Metadata & Versioning",
  "P-4": "Representation",
  "P-5": "Status Codes",
  "P-6": "Error Handling",
  "P-7": "Security & Documentation",
};

export function categoryForPracticeId(practiceId: string): string {
  if (!practiceId) return "";
  const fromCatalog = PRACTICES.find((p) => p.id === practiceId);
  if (fromCatalog) return fromCatalog.category;
  const prefix = practiceId.startsWith("OPENAPI")
    ? "OPENAPI"
    : practiceId.startsWith("RM")
      ? "RM"
      : practiceId.slice(0, 1);
  return CATEGORY_BY_PREFIX[prefix] ?? "";
}