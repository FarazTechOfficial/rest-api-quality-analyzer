# Practice-Rule Traceability Matrix

Maps every implemented rule (Analyzer Service, `rule/impl/*.java`) to its
practice. Practice IDs `U-`, `RM-`, `E-`, `H-`, `O-` come from the paper's own
tables (see `paper-notes/paper-catalog.md`). Practice IDs `OPENAPI-01/02/04/05/06`
have **no direct counterpart in the paper's 73 practices**: they are
OpenAPI-spec-specific checks this project added.

This mapping is verified by `analyzer-service/src/test/java/com/research/analyzer/rule/RulePracticeMappingValidationTest.java`.

> **How to read the status column**
> - `REPRODUCED` — the code checks exactly what the paper practice states.
> - `ADAPTED` — the code approximates a paper practice; the paper practice
>   describes runtime/documentation behavior that is only partially inferable
>   from an OpenAPI document. The paper itself did not automate any check.
> - `NOT IN PAPER` — implementation-specific OpenAPI check (OPENAPI-*).

| Rule ID | Rule Name | Practice ID | Paper practice (paper-catalog.md) | Status |
|---------|-----------|-------------|-----------------------------------|--------|
| REST-001 | Resource-oriented URI | U-12 | CRUD-function names should not be used in URIs | REPRODUCED |
| REST-002 | Plural resource names | U-9 | A plural noun should be used for collection names | REPRODUCED |
| REST-003 | HTTP method semantics | RM-2 | `GET`/`HEAD` requests should not have a request body | ADAPTED (adds a body-on-DELETE guard, which is reconstruction row P-2.7) |
| REST-004 | Lowercase path segments | U-5 | Use lowercase letters in the URI | REPRODUCED |
| REST-005 | Operation ID present | OPENAPI-01 | — | NOT IN PAPER |
| REST-006 | At least one error response documented | OPENAPI-04 | (documenting error responses — cf. paper discussion; not an explicit paper practice ID) | NOT IN PAPER |
| REST-007 | At least one success response documented | OPENAPI-05 | (documenting success responses) | NOT IN PAPER |
| REST-008 | No trailing slash | U-2 | The URI should not contain a trailing forward slash | REPRODUCED |
| REST-009 | API version in info block | OPENAPI-06 | (spec-level info.version present) | NOT IN PAPER |
| REST-010 | Path parameters in URI | OPENAPI-02 | — | NOT IN PAPER |
| REST-011 | No file extension in URI | U-6 | The URI should not contain a file extension | REPRODUCED |
| REST-012 | No underscore in URI | U-4 | The URI should not contain an underscore | REPRODUCED |
| REST-013 | POST returns 201 Created | E-3 | 201 ("Created") should be used for successful resource creation | REPRODUCED |
| REST-014 | DELETE returns 204 No Content | E-5 | 204 should be used when the response body is intentionally empty | ADAPTED (scoped to DELETE) |
| REST-015 | GET documents 404 for not found | E-11 | 404 should be used for a "not found" resource | ADAPTED (scoped to item GET) |
| REST-016 | HTTPS server URL | O-14 | SSL should be used | REPRODUCED (server URL https; relative server URLs skipped) |
| REST-017 | Version not in URI path | U-7 | The API should not contain version information in the URI (versioning should not be segment-based) | REPRODUCED |
| REST-018 | Security scheme defined | O-10 | OAuth may be used to protect the API | ADAPTED (any declared security scheme, broader than OAuth) |
| REST-019 | JSON representation | O-8 | The API should provide JSON-based representations | ADAPTED (declared media-type subset; cannot verify runtime bodies) |
| REST-020 | Pagination parameters | O-13 | Split large responses across multiple requests (pagination) | ADAPTED (pagination-parameter subset on collection GETs) |
| REST-021 | Content-Type is JSON (GET responses) | H-2 | `Content-Type: application/json` for GET responses with JSON bodies | ADAPTED (response-media-type subset; H-1/H-3 not checkable) |

Rules marked `(spec-level)` evaluate once per specification, not once per endpoint
(REST-009, REST-016, REST-018, REST-019).

---

## Summary

- **Implemented rules**: 21
- **Rules mapped to paper practices**: 16 — U-2, U-4, U-5, U-6, U-7, U-9,
  U-12, RM-2, E-3, E-5, E-11, O-10, O-14, O-8, O-13, H-2
- **OpenAPI-specific rules (no direct paper counterpart)**: 5 —
  OPENAPI-01/02/04/05/06
- **Paper practices covered**: 16 / 73 (**21.9 %**) — the paper's remaining 57
  practices are not automated by this prototype. The paper itself automated
  none of them; it assessed 3 cloud APIs manually via documentation analysis.
- The reference reconstruction in `paper-notes/practice-catalog.md` (83 rows)
  is a different taxonomy; 18 of its rows are conceptually covered by a rule
  (mapped in the frontend `practicesCatalog.ts`), which is not the same number
  as the 16 paper practices covered.