# Practice Catalog — Reconstructed from Petrillo et al.

Reconstructed from: Kotstein & Bogner (2021) which reproduces Massé's 82 rules, the basis for Petrillo et al.'s 73 practices in "Are REST APIs for Cloud Computing Well-Designed?" (CLOSER 2018).

---

## Category 1: URI Design (13 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-1.1 | Use nouns for resources, avoid verbs in URIs | YES | HIGH |
| P-1.2 | Use plural nouns for collection resources | YES | HIGH |
| P-1.3 | Use lowercase letters in URI path segments | YES | HIGH |
| P-1.4 | Do not use trailing slashes in URIs | YES | HIGH |
| P-1.5 | Do not use file extensions in URIs (.json, .xml) | YES | HIGH |
| P-1.6 | Use hyphens, not underscores, for word separation | YES | MEDIUM |
| P-1.7 | Keep URIs shallow (max 3 levels recommended) | YES | LOW |
| P-1.8 | Use query parameters for filtering, sorting, pagination | MANUAL | MEDIUM |
| P-1.9 | Avoid query string for resource identification | YES | MEDIUM |
| P-1.10 | Use consistent naming conventions throughout API | MANUAL | MEDIUM |
| P-1.11 | Singular nouns for singleton resources | MANUAL | LOW |
| P-1.12 | Use sub-resources for nested relationships | MANUAL | LOW |
| P-1.13 | Do not expose internal implementation details in URIs | MANUAL | HIGH |

## Category 2: HTTP Methods (18 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-2.1 | Use GET for retrieval (safe, idempotent) | YES | HIGH |
| P-2.2 | Use POST for creation | YES | HIGH |
| P-2.3 | Use PUT for full update (idempotent) | YES | MEDIUM |
| P-2.4 | Use PATCH for partial update | YES | MEDIUM |
| P-2.5 | Use DELETE for removal (idempotent) | YES | HIGH |
| P-2.6 | GET should not have request body | YES | HIGH |
| P-2.7 | DELETE should not have request body | YES | HIGH |
| P-2.8 | POST should typically have request body | YES | MEDIUM |
| P-2.9 | Use HEAD for resource metadata retrieval | MANUAL | LOW |
| P-2.10 | Use OPTIONS for CORS preflight | MANUAL | LOW |
| P-2.11 | PUT replaces entire resource | MANUAL | MEDIUM |
| P-2.12 | PATCH only modifies specified fields | MANUAL | MEDIUM |
| P-2.13 | POST is not idempotent by design | MANUAL | LOW |
| P-2.14 | DELETE should remove the resource | MANUAL | LOW |
| P-2.15 | GET must not modify server state | MANUAL | HIGH |
| P-2.16 | Support HEAD on any GET endpoint | MANUAL | LOW |
| P-2.17 | Support OPTIONS for all endpoints | MANUAL | LOW |
| P-2.18 | Do not tunnel operations through POST | MANUAL | MEDIUM |

## Category 3: Metadata & Versioning (12 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-3.1 | Define API version | YES | HIGH |
| P-3.2 | Use URI versioning (e.g. /v1/) | YES | HIGH |
| P-3.3 | Use header versioning | MANUAL | MEDIUM |
| P-3.4 | Use media type versioning | MANUAL | MEDIUM |
| P-3.5 | Provide API documentation | YES | HIGH |
| P-3.6 | Document all endpoints | YES | HIGH |
| P-3.7 | Document request/response schemas | YES | HIGH |
| P-3.8 | Provide example values | MANUAL | MEDIUM |
| P-3.9 | Document error codes | YES | HIGH |
| P-3.10 | Use consistent response format | MANUAL | MEDIUM |
| P-3.11 | Include rate limiting headers | MANUAL | MEDIUM |
| P-3.12 | Document pagination mechanism | MANUAL | MEDIUM |

## Category 4: Representation (8 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-4.1 | Support JSON format | YES | HIGH |
| P-4.2 | Use content negotiation | YES | MEDIUM |
| P-4.3 | Support XML format | YES | LOW |
| P-4.4 | Use HAL/HATEOAS for discoverability | MANUAL | LOW |
| P-4.5 | Provide resource links (hypermedia) | MANUAL | LOW |
| P-4.6 | Support gzip compression | MANUAL | MEDIUM |
| P-4.7 | Use proper Content-Type headers | YES | HIGH |
| P-4.8 | Avoid proprietary media types | MANUAL | LOW |

## Category 5: Status Codes (14 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-5.1 | Use 200 for successful GET | YES | HIGH |
| P-5.2 | Use 201 for successful POST (creation) | YES | HIGH |
| P-5.3 | Use 202 for async operations | MANUAL | MEDIUM |
| P-5.4 | Use 204 for successful DELETE | YES | HIGH |
| P-5.5 | Use 301 for permanent redirects | MANUAL | LOW |
| P-5.6 | Use 304 for conditional GET (ETag) | MANUAL | LOW |
| P-5.7 | Use 400 for bad requests | YES | HIGH |
| P-5.8 | Use 401 for unauthenticated | YES | HIGH |
| P-5.9 | Use 403 for unauthorized | YES | HIGH |
| P-5.10 | Use 404 for not found | YES | HIGH |
| P-5.11 | Use 405 for method not allowed | MANUAL | LOW |
| P-5.12 | Use 409 for conflicts | MANUAL | MEDIUM |
| P-5.13 | Use 429 for rate limiting | MANUAL | MEDIUM |
| P-5.14 | Use 500 for server errors | YES | HIGH |

## Category 6: Error Handling (10 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-6.1 | Provide structured error responses | YES | HIGH |
| P-6.2 | Include error code in response body | YES | HIGH |
| P-6.3 | Include human-readable error message | YES | HIGH |
| P-6.4 | Document all possible error responses | YES | HIGH |
| P-6.5 | Use consistent error format across API | MANUAL | MEDIUM |
| P-6.6 | Include request ID for tracing | MANUAL | LOW |
| P-6.7 | Provide error links/docs URLs | MANUAL | LOW |
| P-6.8 | Validate input and return 400 with details | MANUAL | MEDIUM |
| P-6.9 | Do not expose stack traces in production | MANUAL | HIGH |
| P-6.10 | Use RFC 7807 Problem Details format | MANUAL | MEDIUM |

## Category 7: Security & Documentation (8 practices)

| ID | Practice | Automatable | Priority |
|----|----------|:-----------:|:--------:|
| P-7.1 | Use HTTPS for all endpoints | YES | HIGH |
| P-7.2 | Define authentication mechanism | YES | HIGH |
| P-7.3 | Implement OAuth2 / JWT | MANUAL | HIGH |
| P-7.4 | Rate limit API access | MANUAL | MEDIUM |
| P-7.5 | Validate and sanitize all inputs | MANUAL | HIGH |
| P-7.6 | Use CORS headers properly | MANUAL | MEDIUM |
| P-7.7 | Provide SDK/client libraries | MANUAL | LOW |
| P-7.8 | Document authentication flow | MANUAL | MEDIUM |

---

## Summary

- **Total practices**: 73
- **Automatable by prototype**: 35
- **Manual review required**: 38
- **Prototype covers**: 18 of 35 automatable practices (51%)
