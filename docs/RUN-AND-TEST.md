# Run and Test Guide

Step-by-step instructions to run and test the REST API Design Quality Analyzer on your machine.

## Prerequisites Checklist

- [ ] **Java 17** installed — run `java -version` (should show 17.x)
- [ ] **Maven** installed — run `mvn -version`  
  If not in PATH, use: `C:\tools\apache-maven-3.9.16\bin\mvn.cmd`
- [ ] **MySQL 8** running locally
- [ ] MySQL credentials match `report-service/src/main/resources/application.yml`

Default DB settings:

```yaml
url: jdbc:mysql://localhost:3306/rest_api_analyzer?createDatabaseIfNotExist=true
username: root
password: root
```

Change `username` and `password` if your MySQL uses different credentials.

---

## Step 1 — Run Unit Tests (no MySQL needed)

From the project root:

```powershell
cd D:\spring\Research
mvn test
```

Or with full Maven path:

```powershell
C:\tools\apache-maven-3.9.16\bin\mvn.cmd test
```

**Expected:** `BUILD SUCCESS`, 61 tests pass (59 analyzer + 2 report).

---

## Step 2 — Start MySQL

Make sure MySQL is running. The database `rest_api_analyzer` is created automatically on first startup.

Optional manual check:

```sql
SHOW DATABASES;
-- after first run you should see: rest_api_analyzer
```

---

## Step 3 — Start Report Service (Terminal 1)

```powershell
cd D:\spring\Research\report-service
mvn spring-boot:run
```

Wait until you see:

```
Started ReportApplication
```

Report Service runs on **http://localhost:8082**

Quick check — open in browser or curl:

```powershell
curl http://localhost:8082/api/reports
```

Expected: `[]` (empty list)

---

## Step 4 — Start Analyzer Service (Terminal 2)

Open a **new** terminal:

```powershell
cd D:\spring\Research\analyzer-service
mvn spring-boot:run
```

Wait until you see:

```
Started AnalyzerApplication
```

Analyzer Service runs on **http://localhost:8081**

---

## Step 5 — Analyze a Sample API (Postman or curl)

### Option A — PowerShell script (easiest)

From project root:

```powershell
.\scripts\analyze-sample.ps1 -Sample good
```

Also try: `-Sample bad` or `-Sample mixed`

### Option B — curl manually

```powershell
$spec = Get-Content "D:\spring\Research\sample-apis\good-api.json" -Raw
$body = @{
    apiName = "Good Sample API"
    source = "sample-apis/good-api.json"
    specification = $spec
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Uri "http://localhost:8081/api/analyze" -Method POST -Body $body -ContentType "application/json"
```

Save the `analysisId` from the response.

### Option C — Postman

1. **POST** `http://localhost:8081/api/analyze`
2. Headers: `Content-Type: application/json`
3. Body (raw JSON):

```json
{
  "apiName": "Good Sample API",
  "source": "sample-apis/good-api.json",
  "specification": "<paste entire contents of sample-apis/good-api.json here>"
}
```

Note: `specification` must be the OpenAPI JSON as a **string** (escaped if needed), or paste the raw JSON object depending on your client.

---

## Step 6 — Retrieve the Stored Report

Replace `{analysisId}` with the ID from Step 5:

```powershell
curl http://localhost:8082/api/reports/{analysisId}
```

Other useful endpoints:

```powershell
# List all analyses
curl http://localhost:8082/api/reports

# Failures only
curl http://localhost:8082/api/reports/{analysisId}/violations

# Export for experiments
curl http://localhost:8082/api/reports/{analysisId}/export
```

---

## What to Expect from Sample APIs

| Sample | Expected behavior |
|--------|-------------------|
| `good-api.json` | High score, few failures |
| `bad-api.json` | Low score, many failures (verbs in URI, trailing slash, GET with body, etc.) |
| `mixed-api.json` | Medium score, mix of pass/fail |

---

## Troubleshooting

| Problem | Fix |
|---------|-----|
| `mvn` not recognized | Use full path: `C:\tools\apache-maven-3.9.16\bin\mvn.cmd` |
| Report Service won't start | Check MySQL is running and credentials in `application.yml` |
| Analyzer returns 503 | Report Service must be running on port 8082 first |
| Port already in use | Stop other apps on 8081/8082 or change ports in `application.yml` |
| Invalid OpenAPI | Ensure spec is valid OpenAPI 3.x JSON |

---

## Sprint / Git History

Commits are organized by development phase. See `git log --oneline` for the sprint-wise history.
