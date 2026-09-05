param(
    [ValidateSet("good", "bad", "mixed")]
    [string]$Sample = "good"
)

$projectRoot = Split-Path -Parent $PSScriptRoot
$specFile = Join-Path $projectRoot "sample-apis\$Sample-api.json"

if (-not (Test-Path $specFile)) {
    Write-Error "Sample file not found: $specFile"
    exit 1
}

$spec = Get-Content $specFile -Raw

$body = @{
    apiName = "$Sample Sample API"
    source = "sample-apis/$Sample-api.json"
    specification = $spec
} | ConvertTo-Json -Depth 20

Write-Host "Analyzing $Sample-api.json ..."
Write-Host "POST http://localhost:8081/api/analyze"
Write-Host ""

try {
    $response = Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -Body $body -ContentType "application/json; charset=utf-8"
    $response | ConvertTo-Json -Depth 10
    Write-Host ""
    Write-Host "Analysis ID: $($response.analysisId)"
    Write-Host "Score: $($response.score)"
    Write-Host ""
    Write-Host "Retrieve full report:"
    Write-Host "  curl http://localhost:8082/api/reports/$($response.analysisId)"
} catch {
    Write-Error "Request failed. Is Analyzer Service running on 8081? Is Report Service running on 8082?"
    Write-Error $_.Exception.Message
    exit 1
}
