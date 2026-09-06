# How to Run & Test the REST API Quality Analyzer

## Architecture

```
┌─────────────────┐     HTTP      ┌─────────────────┐
│ Analyzer Service │ ────────────> │ Report Service   │
│ (port 8081)     │               │ (port 8082)      │
│ - Parses OpenAPI│               │ - Stores results  │
│ - Runs 18 rules │               │ - H2 in-memory DB │
│ - Returns score │               │                   │
└─────────────────┘               └─────────────────┘
```

---

## Step 1: Start the Services

Open **two terminal windows**:

**Terminal 1 — Report Service:**
```cmd
cd /d D:\spring\Research
"C:\tools\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run -pl report-service -Dspring-boot.run.profiles=local
```

**Terminal 2 — Analyzer Service:**
```cmd
cd /d D:\spring\Research
"C:\tools\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run -pl analyzer-service
```

Wait for both to show "Started" messages (~10-15 seconds).

---

## Step 2: Test with the Sample APIs

### Test the GOOD API (should score ~90%+)

Open PowerShell and run:

```powershell
$spec = Get-Content -Raw "D:\spring\Research\sample-apis\good-api.json"
$body = @{
    apiName = "Pet Store API"
    version = "1.0.0"
    source = "sample-apis/good-api.json"
    specification = $spec
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | ConvertTo-Json -Depth 5
```

### Test the BAD API (should score low, many violations)

```powershell
$spec = Get-Content -Raw "D:\spring\Research\sample-apis\bad-api.json"
$body = @{
    apiName = "Bad Pet Store API"
    version = "1.0.0"
    source = "sample-apis/bad-api.json"
    specification = $spec
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | ConvertTo-Json -Depth 5
```

### Test the MIXED API (partial compliance)

```powershell
$spec = Get-Content -Raw "D:\spring\Research\sample-apis\mixed-api.json"
$body = @{
    apiName = "Mixed API"
    version = "1.0.0"
    source = "sample-apis/mixed-api.json"
    specification = $spec
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | ConvertTo-Json -Depth 5
```

---

## Step 3: Test with YOUR OWN OpenAPI Spec

```powershell
$spec = Get-Content -Raw "C:\path\to\your\api-spec.json"
$body = @{
    apiName = "My API"
    version = "2.0.0"
    source = "my-api-spec.json"
    specification = $spec
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" `
    -Method POST `
    -ContentType "application/json" `
    -Body $body | ConvertTo-Json -Depth 5
```

---

## Step 4: View Saved Reports

After analyzing, reports are saved in the Report Service (H2 in-memory DB):

```powershell
# List all saved reports
Invoke-RestMethod -Uri "http://localhost:8082/api/reports"

# Get a specific report by ID (use the analysisId from the analyze response)
Invoke-RestMethod -Uri "http://localhost:8082/api/reports/{analysisId}"

# Get only violations (failed rules)
Invoke-RestMethod -Uri "http://localhost:8082/api/reports/{analysisId}/violations"
```

---

## Step 5: What Each Rule Checks

| # | Rule | What it catches |
|---|------|----------------|
| 1 | Resource-oriented URI | Verbs in URIs like /getUsers |
| 2 | Plural resource names | Singular nouns like /user instead of /users |
| 3 | HTTP method semantics | GET with request body, DELETE with request body |
| 4 | Lowercase paths | Uppercase like /CreateOrder |
| 5 | Error response defined | No 4xx/5xx documented |
| 6 | Success response defined | No 2xx documented |
| 7 | No trailing slash | Paths ending with / |
| 8 | API version in info | Missing info.version in OpenAPI |
| 9 | No file extension | .json, .xml in URIs |
| 10 | No underscores | order_items instead of order-items |
| 11 | POST returns 201 | POST without 201 Created response |
| 12 | DELETE returns 204 | DELETE without 204 No Content |
| 13 | GET documents 404 | GET /{id} without 404 response |
| 14 | HTTPS server | Server URLs using http:// |
| 15 | No version in URI | /v2/orders in path |
| 16 | Security defined | No security schemes in OpenAPI |

---

## Step 6: Understanding the Output

```json
{
  "analysisId": "uuid-here",
  "apiName": "Pet Store API",
  "totalRules": 90,        // 18 rules × 5 endpoints
  "passedRules": 80,       // PASSED status
  "failedRules": 10,       // FAILED status
  "skippedRules": 0,       // NOT_EVALUATED / MANUAL_REVIEW
  "score": 88.89,          // passed / (passed + failed) × 100
  "results": [
    {
      "ruleId": "REST-011",
      "ruleName": "No file extension in URI",
      "practiceId": "U-6",
      "endpoint": "/order_items/{itemId}.json",
      "method": "GET",
      "status": "FAILED",
      "passed": false,
      "message": "Path segment 'order_items.json' contains a file extension.",
      "recommendation": "Remove the file extension. Use content negotiation via Accept header instead."
    }
  ]
}
```

---

## Quick Smoke Test Script

Save this as `test-api.ps1` in `D:\spring\Research`:

```powershell
Write-Host "=== REST API Quality Analyzer - Smoke Test ===" -ForegroundColor Cyan

# Test 1: Good API
Write-Host "`n[1] Analyzing GOOD API..." -ForegroundColor Green
$spec = Get-Content -Raw "sample-apis\good-api.json"
$body = @{apiName="Good API";specification=$spec} | ConvertTo-Json -Depth 10
$good = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -ContentType "application/json" -Body $body
Write-Host "  Score: $($good.score)% | Passed: $($good.passedRules) | Failed: $($good.failedRules) | Skipped: $($good.skippedRules)"

# Test 2: Bad API
Write-Host "`n[2] Analyzing BAD API..." -ForegroundColor Red
$spec = Get-Content -Raw "sample-apis\bad-api.json"
$body = @{apiName="Bad API";specification=$spec} | ConvertTo-Json -Depth 10
$bad = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -ContentType "application/json" -Body $body
Write-Host "  Score: $($bad.score)% | Passed: $($bad.passedRules) | Failed: $($bad.failedRules) | Skipped: $($bad.skippedRules)"

# Test 3: Mixed API
Write-Host "`n[3] Analyzing MIXED API..." -ForegroundColor Yellow
$spec = Get-Content -Raw "sample-apis\mixed-api.json"
$body = @{apiName="Mixed API";specification=$spec} | ConvertTo-Json -Depth 10
$mixed = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -ContentType "application/json" -Body $body
Write-Host "  Score: $($mixed.score)% | Passed: $($mixed.passedRules) | Failed: $($mixed.failedRules) | Skipped: $($mixed.skippedRules)"

# Test 4: List saved reports
Write-Host "`n[4] Listing saved reports..." -ForegroundColor Cyan
$reports = Invoke-RestMethod -Uri "http://localhost:8082/api/reports"
Write-Host "  Total saved reports: $($reports.Count)"

Write-Host "`n=== Done ===" -ForegroundColor Green
```

Run it with:
```cmd
cd /d D:\spring\Research
powershell -ExecutionPolicy Bypass -File test-api.ps1
```

---

## Stopping the Services

Close both terminal windows, or press `Ctrl+C` in each.
