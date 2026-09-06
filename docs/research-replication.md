# Research Replication Audit

**Project:** REST API Design Quality Analyzer
**Replicated paper:** Merle, Merle, Moha & Gueheneuc — "Are REST APIs for Cloud Computing Well-Designed? An Exploratory Study", **ICSOC 2016**, LNCS vol 9936, pp. 157-170, DOI 10.1007/978-3-319-46295-0_10 (open access).
**Audit date:** 2026-09-06
**Version:** practice-ID normalization + replication documentation (commit history in git).

> **How to read this document.** The paper is the source of truth. Every claim
> in this file either (a) states what the paper reports, (b) states what this
> implementation does, or (c) classifies the relationship between them using
> exactly one of:
>
> - `REPRODUCED` — our check matches the paper practice as stated.
> - `ADAPTED` — our check approximates the paper practice; the paper practice
>   describes behavior not fully inferable from an OpenAPI document.
> - `PARTIALLY REPRODUCED` — a subset of the practice is checked.
> - `NOT IMPLEMENTED` — no check exists.
> - `DIFFERENT FROM PAPER` — the practice/claim deviates from the paper.
> - `UNCLEAR` — the paper does not provide sufficient information to verify
>   this practice; verification requires the full chapter text beyond the
>   extracted tables.

---

## 1. Executive summary

1. The paper catalogued **73 REST API design practices** in 5 categories
   (URI design 20, Request methods 8, Error handling 16, HTTP headers 10,
   Others 19) and manually assessed **three cloud APIs** — Google Cloud
   Platform, OpenStack, OCCI 1.2 — against them via documentation analysis.
2. This project is an **automated, OpenAPI-spec-based adaptation** of a
   subset. It is **not** a reproduction of the paper's measurement: it does
   not analyze the paper's dataset, uses a different artifact type (OpenAPI
   documents vs. cloud documentation), and automates practices the paper
   checked by hand.
3. The project's rule engine implements **18 rules**, mapping to **13 of the
   paper's 73 practices** (17.8 %) plus **5 OpenAPI-specific checks** that
   have no paper counterpart.
4. The repository's reconstruction catalog (`paper-notes/practice-catalog.md`,
   83 rows from Kotstein & Bogner 2021 / Masse 2011) is **not** the paper's
   73-practice list; it is a separate, broader taxonomy. This file reconciles
   the two.
5. As of the audit date, **no reproduction results exist**: `experiments/` and
   `results/` held only placeholders. Sample-API analyses are dev fixtures,
   not study data. Any result claimed for the paper's research questions
   before a designed experiment is run would be **RESULTS NOT YET AVAILABLE**.

---

## 2. The original study

| Attribute | Paper (source of truth) |
|-----------|--------------------------|
| Title | Are REST APIs for Cloud Computing Well-Designed? An Exploratory Study |
| Venue / year | ICSOC 2016, LNCS 9936, pp. 157-170 |
| DOI | 10.1007/978-3-319-46295-0_10 (open access) |
| Studied APIs | Google Cloud Platform (GCP), OpenStack, OCCI 1.2 — exactly three |
| Artifact studied | API documentation, analyzed **manually** |
| Practices catalogued | **73** across 5 categories (Table 1) |
| Findings (headline) | Practice counts: GCP 48/73, OpenStack 45/73, OCCI 41/73; 24 practices met by all three, 10 met by none; GCP most, OCCI least conformant; error-handling practices among the least-satisfied. (Exact compliance percentages quoted in the paper's conclusion.) |
| Practices source | Masse "REST API Design Rulebook" (2011) plus related references |
| RQs | Numbered RQ1-RQ4 in the paper's Methods; exact wording must be quoted from the chapter when citations are written (the paper does not provide sufficient information to verify the wording from the tables alone) |

**Headline conclusion we must NOT misattribute:** the paper's 61%/56%/44%
(GCP/OpenStack/OCCI) compliance-style figures and its research questions are
the paper's own results for its three manually-studied cloud APIs. They are
**not** results of this tool and must not be presented as such.

---

## 3. Scope of this replication

| Aspect | Original paper | This project | Classification |
|--------|----------------|--------------|----------------|
| API sample | 3 cloud APIs (manual doc analysis) | Arbitrary OpenAPI specs (sample fixtures) | `DIFFERENT FROM PAPER` (intentional adaptation) |
| Analysis unit | REST practice conformance per API | Rule result per endpoint per spec | `DIFFERENT FROM PAPER` |
| Measurement method | Manual reading of documentation | Automated checks on OpenAPI structure | `ADAPTED` |
| Practice catalog | 73 practices (Tables 1-6) | 83-row reconstruction + 13 paper practices automated | see Section 6 |
| Result artifacts | Per-API practice tables | JSON analysis reports + experiment runner | `ADAPTED` |
| Research questions | Paper's RQ1-RQ4 | Not yet answered by a designed experiment | `NOT IMPLEMENTED` (results pending) |

---

## 4. Practice coverage classification (paper's 73 practices)

Legend: `REPRODUCED` / `ADAPTED` / `PARTIALLY REPRODUCED` / `NOT IMPLEMENTED` /
`DIFFERENT FROM PAPER` / `UNCLEAR`. Rule ids refer to `analyzer-service/.../rule/impl/`.

### 4.1 URI design (20)

| ID | Paper practice | Status | Rule / note |
|----|----------------|--------|-------------|
| U-1 | Plural, lowercase, easy-to-read URI identifiers | NOT IMPLEMENTED | implied by U-5/U-9 checks |
| U-2 | No trailing forward slash | REPRODUCED | REST-008 |
| U-3 | Hyphens over `+`/`_`/space for readability | PARTIALLY REPRODUCED | underscore part via REST-012 |
| U-4 | No underscore in URI | REPRODUCED | REST-012 |
| U-5 | Lowercase letters in URI | REPRODUCED | REST-004 |
| U-6 | No file extension in URI | REPRODUCED | REST-011 |
| U-7 | No segment-based version information | REPRODUCED | REST-017 |
| U-8 | No CRUD function name in URI | PARTIALLY REPRODUCED | overlap with U-12; REST-001 |
| U-9 | Plural noun for collection names | REPRODUCED | REST-002 |
| U-10 | Noun for collection values | NOT IMPLEMENTED | |
| U-11 | Singular noun for document names | NOT IMPLEMENTED | |
| U-12 | No CRUD-function names in URIs | REPRODUCED | REST-001 |
| U-13 | No query strings in resource path | NOT IMPLEMENTED | |
| U-14 | Template literals kept at the END of the URI | NOT IMPLEMENTED | |
| U-15 | Verbs for controller/action names (last resort) | NOT IMPLEMENTED | |
| U-16 | HTTP methods used for correct purpose | PARTIALLY REPRODUCED | method semantics via REST-003, 013, 014, 015 |
| U-17 | Use a Lookup for privileged subsets (no `?search=`) | NOT IMPLEMENTED | |
| U-18 | Document-based representations respect encapsulation | NOT IMPLEMENTED | requires semantic analysis |
| U-19 | Controllers avoided (noun-based URIs) | PARTIALLY REPRODUCED | effective via REST-001/C-1 |
| U-20 | No two "id" segments in one URI | NOT IMPLEMENTED | |

### 4.2 Request methods (8)

| ID | Paper practice | Status | Rule / note |
|----|----------------|--------|-------------|
| RM-1 | `GET`/`POST` must not tunnel other methods | NOT IMPLEMENTED | distinct from body rules |
| RM-2 | `GET` and `HEAD` must not have a request body | ADAPTED | REST-003 (also forbids body on DELETE) |
| RM-3 | `POST` must not body-encode `PUT`-like requests | NOT IMPLEMENTED | |
| RM-4 | `GET` and `POST` must not be confused | NOT IMPLEMENTED | |
| RM-5 | `DELETE` returns `202` for async deletion | NOT IMPLEMENTED | |
| RM-6 | `GET`/`PUT` share request representation semantics | NOT IMPLEMENTED | |
| RM-7 | `POST` returns location of created resource | PARTIALLY REPRODUCED | 201 part via REST-013; `Location` header unchecked |
| RM-8 | Minimize needless `HEAD`/`PATCH`/`OPTIONS` | NOT IMPLEMENTED | |

### 4.3 Error handling (16)

| ID | Paper practice | Status | Rule / note |
|----|----------------|--------|-------------|
| E-1 | `200` for nonspecific success | NOT IMPLEMENTED | |
| E-2 | `200` must not communicate error bodies | NOT IMPLEMENTED | requires runtime behavior |
| E-3 | `201` for successful creation | REPRODUCED | REST-013 |
| E-4 | `202` for async success | NOT IMPLEMENTED | |
| E-5 | `204` when body intentionally empty | ADAPTED | REST-014 (scoped to DELETE) |
| E-6 | `301` for permanently moved resource | NOT IMPLEMENTED | |
| E-7 | `303` to refer without forcing a new GET | NOT IMPLEMENTED | |
| E-8 | `307` for temporary resend | NOT IMPLEMENTED | |
| E-9 | `304` for cached/conditional GET | NOT IMPLEMENTED | |
| E-10 | `400` for bad request with validation description | NOT IMPLEMENTED | |
| E-11 | `404` for not found | ADAPTED | REST-015 (scoped to item GET) |
| E-12 | `409` for resource conflict | NOT IMPLEMENTED | |
| E-13 | `412` for precondition failed | NOT IMPLEMENTED | |
| E-14 | `415` for unsupported media type | NOT IMPLEMENTED | |
| E-15 | `500` must not carry error bodies | NOT IMPLEMENTED | requires runtime behavior |
| E-16 | JSON as error message format | NOT IMPLEMENTED | |

### 4.4 HTTP headers (10) — all NOT IMPLEMENTED

H-1 Content-Type at the right moment · H-2 JSON Content-Type on GET · H-3
charset in Content-Type · H-4 consistent ETag · H-5 `Location` on creation ·
H-6 `WWW-Authenticate` · H-7 `Cache-Control` · H-8 `Expires` · H-9
`Last-Modified` + `If-Modified-Since` · H-10 `Accept` negotiation.

Status: `NOT IMPLEMENTED` for all ten. Several are partially expressible in
OpenAPI (declared response headers: H-5, H-7, H-9) but are not currently
checked; the paper does not provide sufficient information to verify exact
header semantics from tables alone, so any future implementation should treat
each as `ADAPTED` (H-7/H-8/H-9/H-10) or `UNCLEAR` (H-1, H-3, H-6).

### 4.5 Other (19)

| ID | Paper practice | Status | Rule / note |
|----|----------------|--------|-------------|
| O-1 | Client-driven content negotiation | NOT IMPLEMENTED | |
| O-2 | Server-driven content negotiation | NOT IMPLEMENTED | |
| O-3 | Multiple representations of a resource | NOT IMPLEMENTED | |
| O-4 | Consistent, well-defined versioning system | ADAPTED (guidance) | REST-017 recommends non-URI versioning; no positive check (`UNCLEAR` for media-type specifics) |
| O-5 | No over/under-specified responses | NOT IMPLEMENTED | requires runtime |
| O-6 | Machine-readable API description | NOT IMPLEMENTED | tool requires OpenAPI as input; not a conformance check |
| O-7 | XML representations | NOT IMPLEMENTED | |
| O-8 | JSON representations | NOT IMPLEMENTED | |
| O-9 | Discoverable from root URI | NOT IMPLEMENTED | |
| O-10 | OAuth may be used | ADAPTED | REST-018 (any declared security scheme) |
| O-11 | Cached responses for performance | NOT IMPLEMENTED | |
| O-12 | Correct idempotent method usage | NOT IMPLEMENTED | |
| O-13 | Split large responses (pagination) | NOT IMPLEMENTED | |
| O-14 | SSL should be used | REPRODUCED | REST-016 (https server URL) |
| O-15 | Self-describe caching requirements | NOT IMPLEMENTED | |
| O-16 | Simple logical "cool" URIs | NOT IMPLEMENTED | |
| O-17 | Avoid statistical requests | NOT IMPLEMENTED | |
| O-18 | Fully documented API | PARTIALLY REPRODUCED | documentation-presence subset via OPENAPI-04/05/06 |
| O-19 | Fault-tolerant behavior | NOT IMPLEMENTED | requires runtime |

**Coverage summary:** 13 of 73 paper practices (17.8 %) are automated by a
direct rule: **9 REPRODUCED** (U-2, U-4, U-5, U-6, U-7, U-9, U-12, E-3, O-14)
and **4 ADAPTED** (RM-2, E-5, E-11, O-10). The remaining 60 practices are
`NOT IMPLEMENTED` (or only `PARTIALLY REPRODUCED` through overlapping rules,
never claimed as full coverage).

---

## 5. Reconciliation: the 83-row reconstruction vs. the paper's 73

`paper-notes/practice-catalog.md` reconstructs practice lists from Kotstein &
Bogner (2021), which reproduces Masse's 82 rules — the paper's source
material, but **not the paper's own catalog**.

- Paper: **73 practices**, 5 categories (URI 20 / Request methods 8 / Error
  handling 16 / HTTP headers 10 / Others 19).
- Reconstruction: **83 rows**, 7 categories with its own `P-x.y` ids.

The 83 rows are therefore a **superset/sibling**, not a copy: some rows
correspond to paper practices, some merge two practices, some are from the
broader rulebook without a paper-practice counterpart. The reconstruction is
kept as development metadata; **it must never be quoted as the paper's count.**
(The repo previously mis-stated "73 practices / 35 automatable" while listing
83 rows and mis-cited the venue as CLOSER 2018 — both corrected 2026-09-06.)

Mapping rule instance: for any row, treat the paper tables in
`paper-notes/paper-catalog.md` as authoritative; classify each of the 83 rows
against Section 4, marking `DIFFERENT FROM PAPER` where a row has no
counterpart.

---

## 6. Rule-by-rule implementation audit

Each rule file: `analyzer-service/src/main/java/com/research/analyzer/rule/impl/<Rule>.java`.

| Rule | Practice | What the code checks | Known limitations / false-positive risk | Status |
|------|----------|----------------------|-----------------------------------------|--------|
| REST-001 | U-12 | Flags CRUD-ish verbs (`get`, `create`, `delete`...) in path segments | Heuristic keyword list; may miss synonyms or over-match method names | REPRODUCED (heuristic) |
| REST-002 | U-9 | Collection endpoints with plural-noun names | Pluralizer heuristics on English words; non-English names risk FPs | REPRODUCED (heuristic) |
| REST-003 | RM-2 | GET/DELETE must have no `requestBody`; POST/PUT/PATCH should | DELETE-guard is an extension (paper covers GET/HEAD); OpenAPI only | ADAPTED |
| REST-004 | U-5 | Path segments in lowercase | Skips `{pathParams}`; mixed-case names flagged | REPRODUCED |
| REST-005 | OPENAPI-01 | `operationId` present on each operation | OpenAPI-hygiene, no paper counterpart | NOT IN PAPER |
| REST-006 | OPENAPI-04 | At least one 4xx/5xx response documented per operation | Documenting ≠ correct semantics; spec may document only some errors | NOT IN PAPER |
| REST-007 | OPENAPI-05 | At least one 2xx response documented per operation | Same note | NOT IN PAPER |
| REST-008 | U-2 | No trailing `/` in paths | trivial to satisfy | REPRODUCED |
| REST-009 | OPENAPI-06 | `info.version` present | Spec metadata, no paper counterpart | NOT IN PAPER |
| REST-010 | OPENAPI-02 | Path template params match declared `parameters` | Spec hygiene | NOT IN PAPER |
| REST-011 | U-6 | No `.ext` file-like suffix in last path segment | | REPRODUCED |
| REST-012 | U-4 | No `_` in path segments | | REPRODUCED |
| REST-013 | E-3 | Documented `201` for POST operations | 201 documented but body semantics unchecked | REPRODUCED (doc-level) |
| REST-014 | E-5 | DELETE documents `204` or empty body | OpenAPI can't prove an empty body at runtime | ADAPTED |
| REST-015 | E-11 | Item GET (`{id}`) documents `404` | Cannot detect wrong successful codes returned at runtime | ADAPTED |
| REST-016 | O-14 | `servers[].url` uses https | Servers list may be empty/relative | REPRODUCED |
| REST-017 | U-7 | Flags `v\d+` segments in path | Query/matrix versioning not versioning-by-segment is allowed | REPRODUCED |
| REST-018 | O-10 | Some `securitySchemes` declared | Broader than OAuth; spec may declare but never enforce | ADAPTED |

**Cross-cutting limitation (all rules):** checks inspect OpenAPI structure, not
runtime HTTP behavior. Every practice that the paper expresses about actual
messages (E-2, E-15, O-5, O-19...) is outside the expressible subset and is
marked `NOT IMPLEMENTED` accordingly.

---

## 7. Frontend audit for research misrepresentation

Findings and fixes applied 2026-09-06 (`New-Project-source-code/`):

| Claim/UI element | Before | After |
|------------------|--------|-------|
| Practices page hero | "The source paper reports 73 practices (35 automatable)" while listing 83 rows | Reports 73 paper practices and 83 reconstruction rows, with an explicit disclaimer that P-x.y ids are project labels |
| Stat cards | "Automatable rows 40" presented next to paper claims | Stat cards: Paper practices (73) / Catalog rows (83) / Paper practices automated (13) / Rule checks (18) |
| "Practices automated" (Dashboard) | 16, ambiguous ("practices automated" vs "paper practices automated") | Relabeled and derived from the corrected 13-practice mapping |
| Result categories | Backend returned reconstructed `P-x.y` practice ids | Backend now returns paper ids (`U-2`...) and `OPENAPI-*`; category labels derived from paper categories |
| practicesCatalog.ts header | Claimed 16 rules covered paper ids (7 of 18 `OPENAPI`) | Documents 13 paper + 5 OpenAPI and the reconstruction disclaimer |
| Charts (CategoryBars etc.) | Data-driven from rule results | Unchanged — already data-driven, no research figures hardcoded. |

No fabricated research statistics were found in the frontend. All displayed
counts now trace to either the paper catalog (73), the reconstruction (83),
or the live rule engine (13/18).

---

## 8. Research-validation test suite

`analyzer-service/src/test/java/com/research/analyzer/rule/RulePracticeMappingValidationTest.java`

- Locks the exact rule->practice matrix (18 rows).
- Fails on: an unexpected rule id, practice-id drift, duplicate practice ids,
  a residual `P-` (reconstruction) id leaking into results, or a malformed
  non-`OPENAPI` paper id.
- Run with `cd analyzer-service && mvn test`.

---

## 9. Experimental setup and reproducibility

### 9.1 What exists

- Sample OpenAPI fixtures: `sample-apis/good-api.json`, `bad-api.json`,
  `mixed-api.json`, `my-api.json`, `request-body.json` (+ `analyze.py`,
  `analysis-result.json`). These are **development fixtures**, not a study
  dataset; they are not the paper's three APIs.
- `experiments/run_experiment.py` — invokes the Analyzer Service over a
  configured API list and writes `results/raw/<api>-<ts>.json` plus
  `results/summary.json`.
- No manufactured findings: `results/*.json` and `results/experiment-summary.md`
  contain only real outputs of the runner.

### 9.2 How to run

```bash
# services on 8081 (analyzer) and 8082 (report)
cd experiments && python run_experiment.py
```

### 9.3 Dataset honesty

A valid replication dataset for the paper's RQs would need the paper's three
APIs (or a documented replacement corpus) plus a manual ground-truth pass for
the non-automatable practices. That dataset does **not** exist in this
repository. Reports therefore say "RESULTS NOT YET AVAILABLE"; any numbers in
`results/` are tool self-checks, not paper-finding reproductions.

---

## 10. Results

Run 2026-09-06 (UTC) by `experiments/run_experiment.py` against the Analyzer
Service at `http://localhost:8081`, over the `sample-apis/` fixture corpus.
Full data: `results/summary.json`, `results/raw/*.json`,
`results/experiment-summary.md`. The runner regenerates everything; nothing
below is hardcoded.

| API | Score | Rule-check totals (P/F/T) | Practices failed (of 18) |
|-----|-------|---------------------------|--------------------------|
| good-api | 100.0 % | 72 / 0 / 72 | none |
| mixed-api | 83.33 % | 45 / 9 / 54 | O-10, O-14, U-12, U-5, U-9 |
| bad-api | 62.04 % | 67 / 41 / 108 | 16 of 18 (only OPENAPI-05 and U-6 pass at practice level) |
| my-api | 76.33 % | 316 / 98 / 414 | E-11, E-3, E-5, O-10, O-14, OPENAPI-04, U-5, U-9 |
| request-body.json | **ERR** | invalid OpenAPI (missing `openapi` attribute) — recorded, not scored | — |

The fixture reports exercise all 18 rules and 5 paper-derived result
categories (URI design, Request methods, Error handling, HTTP headers, Other,
plus OpenAPI conventions). **These numbers are tool self-checks, not a
reproduction of the paper's study.** The paper's dataset (GCP, OpenStack,
OCCI 1.2) was never loaded into this tool; its research questions therefore
remain **RESULTS NOT YET AVAILABLE**. Reproduce this run with:

```bash
cd experiments && python run_experiment.py
```

---

## 11. Threats to validity

- **Construct validity:** the paper's practices describe documentation and
  runtime behavior; our tool approximates many from OpenAPI structure alone.
  4 practices are `ADAPTED`, several statuses are doc-level approximations
  (E-3/E-5/E-11). Heuristic keyword lists (REST-001/002) risk
  false positives/negatives.
- **Internal validity:** no ground-truth labeling exists for the samples;
  rule outputs are not cross-validated against manual annotations.
- **External validity:** sample fixtures are not the paper's dataset; findings
  cannot generalize to real cloud APIs.
- **Conclusion validity:** no designed experiment with the paper's RQs has
  been run; any comparative claim against the paper's 61%/56%/44%
  GCP/OpenStack/OCCI figures would be invalid until a proper corpus (and a
  documented protocol for the non-automatable practices) is created.

---

## 12. Future work

1. Build a documented corpus replicating the paper's three APIs where public
   OpenAPI/spec artifacts exist, and a manual-label protocol for the
   non-automatable practices.
2. Add ADAPTED checks for header practices expressible in OpenAPI (H-5, H-7,
   H-9, H-10).
3. Report scores per paper category and per practice for the corpus, and
   compare qualitatively (never statistically) with the paper's marks.
4. Freeze and version the rule->practice matrix (already enforced by the
   validation test).

---

## 13. References

- [1] F. Petrillo, P. Merle, N. Moha, Y.-G. Gueheneuc. Are REST APIs for Cloud
  Computing Well-Designed? An Exploratory Study. _ICSOC 2016_, LNCS 9936,
  157-170. DOI 10.1007/978-3-319-46295-0_10.
- [2] S. Masse. REST API Design Rulebook. O'Reilly, 2011 (source of the
  73 practices).
- [3] S. Kotstein, C. Bogner. (2021) — 82-rule reproduction used for the
  repository's reconstructions.