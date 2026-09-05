# Practice–Rule Traceability Matrix

Maps each implemented rule to the paper practice it covers, its rule ID, test class, and automation status.

---

## Implemented Rules (18 total)

| Rule ID | Rule Name | Practice ID | Practice Description | Test Class | Status |
|---------|-----------|-------------|----------------------|------------|--------|
| REST-001 | Resource-oriented URI | P-1.1 | Use nouns for resources, avoid verbs | ResourceOrientedUriRuleTest | AUTOMATED |
| REST-002 | Plural resource names | P-1.2 | Use plural nouns for collections | PluralResourceNameRuleTest | AUTOMATED |
| REST-003 | HTTP method semantics | P-4.2 / P-2.6/2.7 | GET/DELETE no request body | HttpMethodSemanticsRuleTest | AUTOMATED |
| REST-004 | Lowercase path segments | P-1.3 | Use lowercase in URIs | LowercasePathRuleTest | AUTOMATED |
| REST-006 | Error response defined | P-6.4 | Document all error responses | — | AUTOMATED |
| REST-007 | Success response defined | P-5.1 | Document 2xx responses | — | AUTOMATED |
| REST-008 | No trailing slash | P-1.4 | No trailing slashes | NoTrailingSlashRuleTest | AUTOMATED |
| REST-009 | API version in info block | P-3.1 | Define API version | — | AUTOMATED |
| REST-011 | No file extension in URI | P-1.5 | No .json/.xml in URIs | NoFileExtensionRuleTest | AUTOMATED |
| REST-012 | No underscores in URI | P-1.6 | Use hyphens not underscores | NoUnderscoreInUriRuleTest | AUTOMATED |
| REST-013 | POST returns 201 Created | P-5.2 | POST should return 201 | PostReturns201RuleTest | AUTOMATED |
| REST-014 | DELETE returns 204 No Content | P-5.4 | DELETE should return 204 | DeleteReturns204RuleTest | AUTOMATED |
| REST-015 | GET documents 404 for not found | P-5.10 | GET /{id} should document 404 | Get404ForNotFoundRuleTest | AUTOMATED |
| REST-016 | HTTPS server URL | P-7.1 | Use HTTPS for all endpoints | HttpsServerRuleTest | AUTOMATED |
| REST-017 | Version not in URI path | P-3.2 | Avoid version in URI (paper recommends header) | VersionInUriRuleTest | AUTOMATED |
| REST-018 | Security scheme defined | P-7.2 | Define authentication mechanism | SecurityDefinedRuleTest | AUTOMATED |

## Non-Paper Rules (OpenAPI-specific validations)

| Rule ID | Rule Name | Practice ID | Description | Test Class |
|---------|-----------|-------------|-------------|------------|
| REST-005 | Operation ID present | OPENAPI-01 | operationId must be defined | — |
| REST-010 | Path parameters in URI | OPENAPI-02 | path params must match URI template | — |

---

## Paper Practices Not Yet Implemented (Automatable only)

| Practice ID | Practice | Difficulty to Automate |
|-------------|----------|----------------------|
| P-1.7 | Keep URIs shallow (max 3 levels) | Easy — check segment count |
| P-1.9 | Avoid query string for resource ID | Medium — detect if query used for ID |
| P-2.8 | POST should typically have request body | Easy — check requestBody presence |
| P-4.1 | Support JSON format | Easy — check for application/json |
| P-4.7 | Use proper Content-Type headers | Easy — check response content types |
| P-5.7 | Use 400 for bad requests | Easy — check 400 in error responses |
| P-5.8 | Use 401 for unauthenticated | Easy — check 401 in error responses |
| P-5.9 | Use 403 for unauthorized | Easy — check 403 in error responses |
| P-5.14 | Use 500 for server errors | Easy — check 500 in responses |
| P-6.1 | Provide structured error responses | Hard — check error body schema |
| P-6.2 | Include error code in response body | Hard — check error body schema |
| P-6.3 | Include human-readable error message | Hard — check error body schema |

---

## Coverage Summary

- **Paper automatable practices**: 35
- **Implemented rules covering paper practices**: 16
- **Coverage**: 16/35 = **45.7%**
- **Rules with full test coverage**: 16/16 = **100%**
