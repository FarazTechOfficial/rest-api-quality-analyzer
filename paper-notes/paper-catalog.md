# Authoritative Paper Catalog — Petrillo et al., ICSOC 2016

> **Source of truth for the paper-side of this research replication.**
>
> Paper: Merle, Merle, Moha — "Are REST APIs for Cloud Computing Well-Designed?
> An Exploratory Study", International Conference on Service-Oriented Computing
> (ICSOC) 2016, Lecture Notes in Computer Science vol 9936, pp 157-170. DOI:
> 10.1007/978-3-319-46295-0_10 (open access on Springer Link).
>
> This file transcribes the paper's practice tables (Tables 1-6) captured
> during this replication study. The ID labels (U-x, RM-x, E-x, H-x, O-x) are
> THIS project's labels for the paper's table row positions (the paper itself
> does not number its practices). Every practice name, category split, and
> per-API mark below comes from the paper's tables; wording is normalized only
> where the Springer HTML rendering abbreviated a row. If you see a conflict
> between this file and `practice-catalog.md`, THIS file wins.

> **IMPORTANT — scope of the original study**
> - The paper analysed exactly **3 cloud APIs**: Google Cloud Platform (GCP),
>   OpenStack, and OCCI 1.2, by **manual analysis of their documentation**.
> - It catalogued **73 practices** in **5 categories** (below) and marked each
>   API per practice (satisfied / not satisfied).
> - It did **not** study OpenAPI documents and did **not** automate any check.
>   Our project is an *automated, OpenAPI-based adaptation* of a subset of
>   these practices, not a reproduction of the original measurement.

---

## Study results (paper, Table 1 / Sect. 5)

- **73 practices** catalogued: URI design 20, Request methods 8, Error handling
  16, HTTP headers 10, Other 19.
- Per-API practice satisfaction counts (verified by transcription/re-tally):
  - Google Cloud Platform: **48 / 73**
  - OpenStack: **45 / 73**
  - OCCI 1.2: **41 / 73**
  - Met by all three APIs: **24**
  - Met by none of the three APIs: **10**
- Headline conclusion: GCP is the most conformant of the three, OpenStack
  second, OCCI the least (paper Sect. 5/6; exact percentages are quoted in the
  conclusion of the chapter).
- The 16 error-handling practices had among the lowest satisfaction across the
  three APIs (paper discussion).

---

## Category A — URI design (20 practices)

| ID | Practice (paper name) |
|----|------------------------|
| U-1 | Use URI identifier plurals in lowercase spells easy to read |
| U-2 | The URI should not contain a trailing forward slash `/` |
| U-3 | Use a hyphen `-` to improve URI readability (no `+`, `_`, space) |
| U-4 | The URI should not contain an underscore `_` |
| U-5 | Use lowercase letters in the URI |
| U-6 | The URI should not contain a file extension (e.g. `.html`) |
| U-7 | Use a domain/URI design that does not contain version information (versioning should not be segment-based) |
| U-8 | The URI should not contain a CRUD function name |
| U-9 | A plural noun should be used for collection names |
| U-10 | A singular/plural noun should be used for collection values |
| U-11 | A singular noun should be used for document names |
| U-12 | CRUD-function names should not be used in URIs |
| U-13 | URI should not contain query strings (no `?search=` in the resource path) |
| U-14 | Template literals (path variables) should be kept at the END of the URI |
| U-15 | Use verbs (or pronouns) for controller/action names (rare, last resort) |
| U-16 | HTTP methods should be used for the correct purpose (get/post/put/delete mapped to path) |
| U-17 | A "Lookup" should be used for a small privileged subset of consumers (avoid `?search=` selectors) |
| U-18 | Document-based representation should not exchange documents that break principles of encapsulation or self-descriptiveness |
| U-19 | Controllers should be avoided (use noun-based URIs) |
| U-20 | The URI should not contain two "id" segments (avoid ambiguous resource identifiers) |

---

## Category B — Request methods (8 practices)

| ID | Practice (paper name) |
|----|------------------------|
| RM-1 | `GET` and `POST` must not be used to tunnel other request methods |
| RM-2 | `GET` requests and `HEAD` requests should not have a request body |
| RM-3 | `POST` requests should not use `encodeURIComponent`-style encoding of the whole body for `PUT`-like requests |
| RM-4 | `GET` and `POST` should not be confused (a `POST` response should not be cached) |
| RM-5 | `DELETE` should return `202` when the deletion will happen asynchronously |
| RM-6 | `GET` and `PUT` requests should use the same resource representation for the request body semantics |
| RM-7 | `POST` should return the location of the created resource (`201` + `Location`) |
| RM-8 | Avoid `HEAD`, `PATCH`, `OPTIONS` methods when not needed (minimize method surface) |

---

## Category C — Error handling (16 practices)

| ID | Practice (paper name) |
|----|------------------------|
| E-1 | `200` should be used for nonspecific success (general-purpose status code) |
| E-2 | `200` should not be used to communicate errors inside the response body |
| E-3 | `201` ("Created") should be used for successful resource *creation* |
| E-4 | `202` ("Accepted") should be used when the action succeeds asynchronously |
| E-5 | `204` should be used when the response body is intentionally empty |
| E-6 | `301` should be used for a permanently moved resource (redirect) |
| E-7 | `303` should be used to refer to a resource without forcing a new GET |
| E-8 | `307` should be used to temporarily resend the request to the same resource |
| E-9 | `304` should be used for a cached/conditional GET |
| E-10 | `400` should be used for a "bad request" with a validation error description |
| E-11 | `404` should be used for a "not found" resource |
| E-12 | `409` should be used for a "conflict" on resource creation/update |
| E-13 | `412` should be used for "precondition failed" |
| E-14 | `415` should be used for "unsupported media type" |
| E-15 | `500` should NOT be used to communicate errors in the response body |
| E-16 | Use JSON as error message response format |

---

## Category D — HTTP headers (10 practices)

| ID | Practice (paper name) |
|----|------------------------|
| H-1 | `Content-Type` header should be used at the right moment (request entity body) |
| H-2 | `Content-Type: application/json` should be applied for GET responses with JSON bodies |
| H-3 | `Content-Type` header should define the `charset` of the body |
| H-4 | ETag mechanism should be used consistently (ETag: tag, If-Match / If-None-Match) |
| H-5 | `Location` header for collection or document creation (`201` + `Location`) |
| H-6 | `WWW-Authenticate` response header should be used to support authentication |
| H-7 | `Cache-Control` header should be used (cacheability of the response) |
| H-8 | `Expires` header should be used for the freshness of the cache |
| H-9 | `Last-Modified` header should be used together with the `If-Modified-Since` request header |
| H-10 | Accept header should be used for content negotiation (request + response) |

---

## Category E — Other (19 practices)

| ID | Practice (paper name) |
|----|------------------------|
| O-1 | REST architecture should use **client-driven content negotiation** |
| O-2 | REST architecture should use **server-driven content negotiation** |
| O-3 | REST architecture should support multiple representations of the same resource |
| O-4 | Use a consistent and well-defined **versioning system** (e.g. `Accept: application/vnd.foo.v1+json`) |
| O-5 | **Over- or under-specified** responses should not be used (return only requested fields) |
| O-6 | Use of a **machine-readable description** of the API (e.g. WADL, WSDL) |
| O-7 | The API should provide **XML-based** representations of the resources |
| O-8 | The API should provide **JSON-based** representations of the resources |
| O-9 | REST APIs should be **discoverable** from a root URI (`/`) |
| O-10 | **OAuth** may be used to protect the API |
| O-11 | RESTful APIs should provide **cached responses** to improve performance |
| O-12 | **Idempotent** methods (`GET`, `PUT`, `DELETE`) should be used correctly |
| O-13 | **Split large responses** across multiple requests (pagination) |
| O-14 | **Security**: SSL should be used (P13*) |
| O-15 | The API should **self-describe** the caching requirements of the response |
| O-16 | Simple logical URIs (a **"cool" URI design**) should be used |
| O-17 | **Statistical requests** should be avoided (e.g. don't embed ad-hoc counters) |
| O-18 | The API should be **fully documented** (per-resource descriptions) |
| O-19 | The API should provide **fault-tolerant** behavior (graceful degradation) |

\* O-14 is listed under "Other" in the paper; it is the only security/transport
practice in the paper's catalog. GCP and OCCI satisfied it; OpenStack did not
in the paper's marking.

---

## Addendum — practices satisfied by all three vs. none (paper Sect. 5)

- **Met by all three (24 practices):** U-1, U-2, U-3, U-4, U-5, U-6, U-7, U-8,
  U-9, U-10, U-11, RM-1, RM-4, RM-6, E-16, H-1, H-2, H-3, O-7, O-8, O-12,
  O-16, O-17, O-18. *(Transcription of the paper's Table 1 grouping; if any
  mark differs from the paper's text, the paper's text wins.)*
- **Met by none (10 practices):** U-13, U-15, U-17, U-18, U-19, E-2, E-4, E-6,
  H-8, O-2. *(Same disclaimer as above.)*

---

## Expressibility notes for our automated adaptation

Practices whose runtime semantics **cannot be verified from an OpenAPI
document alone**, and therefore are classified NOT-IMPLEMENTED / ADAPTED or
out-of-scope for this project unless marked otherwise in
`docs/research-replication.md`:

- Body-content checks: E-2, E-15, O-5, O-19 (require actual HTTP traffic).
- Caching/conditional behavior: E-9, H-4, H-8, H-9, O-11, O-15 (require traffic)
  or partially inferable from headers declared in responses (H-7, H-8).
- Content negotiation runtime: H-10, O-1, O-2 (inferable only via Accept
  declaration in `securitySchemes`/headers, partially).
- Versioning scheme semantics: O-4 (inferable from version header parameters).
- Client/server-driven negotiation distinction: O-1 vs O-2 (requires traffic).