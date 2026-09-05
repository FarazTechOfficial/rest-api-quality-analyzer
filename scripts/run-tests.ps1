$projectRoot = Split-Path -Parent $PSScriptRoot
Set-Location $projectRoot

$mvn = "mvn"
if (-not (Get-Command mvn -ErrorAction SilentlyContinue)) {
    $mvnPath = "C:\tools\apache-maven-3.9.16\bin\mvn.cmd"
    if (Test-Path $mvnPath) {
        $mvn = $mvnPath
    } else {
        Write-Error "Maven not found. Install Maven or update the path in this script."
        exit 1
    }
}

Write-Host "Running tests from $projectRoot ..."
& $mvn test
