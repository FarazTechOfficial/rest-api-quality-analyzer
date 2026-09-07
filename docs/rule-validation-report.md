# Rule Validation Report

**Date:** 2026-09-07
**Scope:** Technical validation of the 16 rules mapped to paper practices before any real-world experiment.
**Artifacts:** 41 validation test methods added to the existing per-rule `*Test` classes (one class per rule, following the module convention), plus the new `LowercasePathRuleTest`.
**Baseline:** Full analyzer suite passes 100/100 (`mvn -o clean test`).

## Method

For each of the 16 paper-mapped rules I verified the implementation against the exact
Petrillo et al. practice recorded in `docs/practice-traceability.md` (source of truth
for what each rule is documented to check). For each rule I ran:

- a clearly **valid** case (should PASS),
- a clearly **invalid** case (should FAIL),
- an **edge** case (boundary / undecidable from a static OpenAPI subset / realistic
  adversarial input), including documented false-positive and false-negative probes
  for the string/heuristic rules.

Where behaviour disagreed with the documented rule definition I marked the row
"DEFECT". All other identified FP/FN behaviour is consistent with the documented
heuristic and is reported as a limitation, not silently changed (per the instruction
to fix only genuine defects).

## Per-rule validation results

| Rule | Paper Practice | Valid Test | Invalid Test | Edge Case | Result | Issue |
|------|----------------|------------|--------------|-----------|--------|-------|
| REST-001 Resource-oriented URI | U-12 | `/users`, `/orders/{orderId}` PASS | `/getUsers`, `/users/search`, `/users/{id}/delete` FAIL | `/updates`, `/lists`, `/getty-images`, `{getUser}` params are still flagged (verb `startsWith`); `/login`, `/logout`, `/refresh-token` pass (not in 12-verb list) | PASS (5 tests) | Heuristic FP on verb-prefixed nouns and path-parameter names; FN for unlisted RPC actions. Behaviour consistent with documented verb list — no change. |
| REST-002 Plural resource names | U-9 | `/users/{userId}`, `/orders/{id}/items`, `/v1/users` PASS | `/user/{id}`, `/user`, `/users/{id}/order` FAIL | `/profile` (singleton) FAIL; `/status/{id}`, `/news` PASS (end in "s"); `/equipment/{id}`, `/inventory` FAIL (mass nouns); `/USERS/{id}` FAIL (case-sensitive `endsWith`) | PASS (8 tests) | `endsWith("s"/"es"/"ies")` heuristic: false-positives on mass nouns and singletons, false-negatives on uninflected singulars, case-sensitive. Documented heuristic — no change. |
| REST-003 HTTP method semantics | RM-2 (ADAPTED) | GET/HEAD/DELETE without body, POST/PUT with body PASS | GET with body FAIL, HEAD with body FAIL | DELETE with body FAIL is the documented ADAPTED extension (reconstruction P-2.7), not in the paper's RM-2 | PASS (7 tests) | ADAPTED: RM-2 + body-on-DELETE guard. Only presence of `requestBody` is checkable statically. |
| REST-004 Lowercase path segments | U-5 | `/users/{id}` PASS | `/Users`, `/users/{id}/Orders` FAIL | Brace-delimited parameters skipped: `/users/{USER_ID}` PASS | PASS (4 tests) | Parameters excluded by design; a mixed-case parameter name is not flagged. |
| REST-008 No trailing slash | U-2 | `/users` PASS | `/users/`, `/users/{id}/` FAIL | Root `/` PASS (path length guard) | PASS (4 tests) | None — deterministic string check. |
| REST-011 No file extension | U-6 | `/users/{id}`, `/reports/2024` PASS | `/users.json`, `/reports/annual.xml` FAIL | `/users/{id.json}` PASS (param skipped); `/users.JSON` PASS (regex is case-sensitive) | PASS (6 tests) | Case-sensitive extension regex and skipped `{...}` segments; benign because uppercase/param paths are caught by other rules (REST-004). |
| REST-012 No underscore in URI | U-4 | `/order-items/{id}` PASS | `/order_items/{id}`, `/user_profiles` FAIL | `/users/{user_id}` PASS (param skipped) | PASS (3 tests) | Parameters excluded by design. |
| REST-013 POST returns 201 | E-3 | POST + 201 PASS | POST without 201, POST + 200-only FAIL | Non-POST ignored | PASS (4 tests) | Only checks that 201 is *declared*; runtime status not verifiable (paper assessed live APIs). |
| REST-014 DELETE returns 204 | E-5 (ADAPTED) | DELETE + 204 PASS | DELETE without 204, DELETE + 200-only FAIL | Non-DELETE ignored | PASS (4 tests) | ADAPTED: E-5 covers any empty-body success; check is scoped to DELETE. |
| REST-015 GET documents 404 | E-11 (ADAPTED) | `/users/{id}` + 404 PASS; nested `/users/{id}/posts/{postId}` + 404 PASS | Item GET without 404 FAIL | Collection GET N/A; concrete-id item paths `/users/me`, `/v2/users/12345` are treated as collections → 404 check skipped | PASS (5 tests) | ADAPTED + item detection relies on `/{param}`-suffix regex; concrete-id item paths are false negatives. |
| REST-016 HTTPS server URL | O-14 | All-`https` servers PASS | `http://` server, `ws://` server FAIL | No servers → PASS (cannot evaluate); relative `/v1` skipped; mixed https+http FAIL | PASS (7 tests) | Spec-level, evaluated once. Transport security unverifiable when servers absent/relative. Reproduced behaviour. |
| REST-017 Version not in URI | U-7 | `/api/users` PASS | `/v1/users`, `/api/v2/users`, `/v2beta/users` FAIL | `/V1/users` and `/api/1.0/users` PASS (uppercase/bare-number not matched); `/v8-engine` FAIL (resource name false-positive) | PASS (5 tests) | `v\d+.*` regex is case-sensitive and matches any segment starting `v`+digit. Documented heuristic — no change. |
| REST-018 Security scheme defined | O-10 (ADAPTED) | `bearerAuth` declared PASS | Empty `securitySchemes` FAIL | Spec-level rule — evaluated once per spec | PASS (2 tests) | ADAPTED: accepts any scheme (broader than the paper's OAuth wording); cannot verify a scheme is applied to operations. |
| REST-019 JSON representation | O-8 (ADAPTED) | Any endpoint declaring `application/json`, `application/problem+json`, case/charset variants PASS | No JSON media type anywhere FAIL | `*/*`-only responses do NOT count as JSON (FAIL) | PASS (7 tests) | Spec-level. Declared media-type subset only; runtime bodies not verifiable. |
| REST-020 Pagination parameters | O-13 (ADAPTED) | `/users` + `page`/`size`, `per_page`, `cursor` PASS | Collection GET with no pagination params (`/users`) FAIL | `/shoes?size=42` PASS — filter param `size` counts as pagination (FP); `/users?after=…`, `pagination_token` FAIL — cursor-style names not recognised (FN); `/news` treated as plural collection | PASS (10 tests) | ADAPTED + name-substring heuristic (`page|limit|offset|cursor|per_|rows|start|*size`). Presence-only, semantics not validated. |
| REST-021 Content-Type is JSON (GET) | H-2 (ADAPTED) | GET + `application/json` (incl. `; charset=`) PASS; JSON+XML both PASS | GET + `application/xml` or `text/plain` body FAIL | `*/*` PASS (springdoc default wildcard); body-less GET (204) PASS; non-GET N/A | PASS (9 tests) | ADAPTED: GET-response media-type subset; H-1/H-3 not checkable. Wildcard treated as pass. |

## Totals

- **Rules validated:** 16 / 16 paper-mapped rules.
- **Tests added:** 41 test methods added across the per-rule `*Test` classes
  (`LowercasePathRuleTest` is new; the other 15 classes were extended), giving
  90 rule tests in total. Rule-specific counts are in the Result column above.
- **Full analyzer suite:** 100/100 tests pass (`mvn -o clean test`), including the
  new validation cases. (Two initial failures during authoring were incorrect
  expectations in the tests, not rule defects — e.g. `/profile/creation` does not
  start with "create", and `{id}.xml` is a brace-skipped parameter. Both cases were
  corrected to match the documented definitions.)
- **Rules needing correction:** 0 — none of the 16 rules contradicts its own
  documented definition; no rule code was changed.
- **Rules with documented heuristic / FP-FN limitations:** 6 —
  REST-001 (verb-prefix FP / RPC FN), REST-002 (plural-suffix heuristic),
  REST-011 (case-sensitive extension regex, benign), REST-015 (concrete-id item FN),
  REST-017 (version regex FP/FN), REST-020 (pagination param-name FP/FN).
- **Rules approximated vs. paper (ADAPTED):** 7 — REST-003, REST-014, REST-015,
  REST-018, REST-019, REST-020, REST-021.
- **Production changes:** none. This validation produced tests and this report only;
  runtime behaviour of the analyzer is unchanged, and experiment results are unaffected.

## Verdict

**NOT READY FOR REAL-WORLD EXPERIMENT.**

Rationale: 9 of 16 rules faithfully reproduce their paper practice, but the 3 rules
flagged for special scrutiny — REST-001 (verb detection), REST-002 (plural detection),
REST-020 (pagination) — use string-matching heuristics that measurably mis-classify
realistic inputs (nouns such as `/updates` and `/lists`, mass nouns like `/equipment`,
filter parameters like `size`, cursor-style pagination like `after`), and 7 rules are
documented ADAPTED approximations. Using the current engine on an uncontrolled API set
would introduce unmeasured false positive / false negative rates into the compliance
numbers, weakening any claim about detecting the paper's practices.

Reasonable next step (recommended, not implemented): refine the three heuristics under
test —
1. REST-001: match whole segment against verbs (with a small allow-list of nouns like
   `lists`, `updates`) and exclude path-parameter names,
2. REST-002: use a small plural/uninflected-noun dictionary instead of `endsWith`,
3. REST-020: restrict pagination keywords to `page`, `pageSize`, `per_page`, `limit`,
   `offset`, `cursor` names only, dropping bare `size`/`rows`/`start` matches,
and re-run this suite before the real-world experiment.

## Summary of changes

1. Added 41 validation tests — split across the existing per-rule `*Test` classes
   (SRP: one focused test class per rule, following the module convention) plus a new
   `LowercasePathRuleTest` — covering valid, invalid, edge, and documented FP/FN cases
   for all 16 paper-mapped rules.
2. No changes to rule implementations, parser, engine, or experiment results.
3. Produced this report as the pre-experiment validation gate; verdict NOT READY with
   concrete refinement candidates.