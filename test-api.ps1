Write-Host "=== REST API Quality Analyzer - Smoke Test ===" -ForegroundColor Cyan

# Test 1: Good API
Write-Host "`n[1] Analyzing GOOD API..." -ForegroundColor Green
$spec = Get-Content -Raw "sample-apis\good-api.json"
$body = @{apiName="Good API";specification=$spec} | ConvertTo-Json -Depth 10
$good = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -ContentType "application/json" -Body $body
Write-Host "  Score: $($good.score)% | Passed: $($good.passedRules) | Failed: $($good.failedRules) | Skipped: $($good.skippedRules)"
Write-Host "  Violations:" -ForegroundColor DarkGray
$good.results | Where-Object { -not $_.passed } | ForEach-Object {
    Write-Host "    - [$($_.ruleId)] $($_.endpoint) $($_.method): $($_.message)" -ForegroundColor DarkRed
}

# Test 2: Bad API
Write-Host "`n[2] Analyzing BAD API..." -ForegroundColor Red
$spec = Get-Content -Raw "sample-apis\bad-api.json"
$body = @{apiName="Bad API";specification=$spec} | ConvertTo-Json -Depth 10
$bad = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -ContentType "application/json" -Body $body
Write-Host "  Score: $($bad.score)% | Passed: $($bad.passedRules) | Failed: $($bad.failedRules) | Skipped: $($bad.skippedRules)"
Write-Host "  Violations:" -ForegroundColor DarkGray
$bad.results | Where-Object { -not $_.passed } | ForEach-Object {
    Write-Host "    - [$($_.ruleId)] $($_.endpoint) $($_.method): $($_.message)" -ForegroundColor DarkRed
}

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
$reports | ForEach-Object {
    Write-Host "    - $($_.apiName) (Score: $($_.score)%) at $($_.analyzedAt)"
}

# Test 5: Get violations only for the first report
if ($reports.Count -gt 0) {
    $firstId = $reports[0].analysisId
    Write-Host "`n[5] Violations for first report ($firstId)..." -ForegroundColor Magenta
    $violations = Invoke-RestMethod -Uri "http://localhost:8082/api/reports/$firstId/violations"
    Write-Host "  Failed rules: $($violations.results.Count)"
}

Write-Host "`n=== All Tests Complete ===" -ForegroundColor Green
