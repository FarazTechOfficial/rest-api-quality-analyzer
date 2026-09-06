# REST API Design Quality Analyzer

A research prototype that accepts OpenAPI specifications, evaluates them against a set of empirically-motivated REST API design practices, and produces structured analysis reports.

This prototype is intended to support an empirical investigation of REST API design practices. It does **not** claim academic findings on its own.

## Problem Being Investigated

REST APIs are widely used in cloud computing, but their design quality varies. This tool accepts OpenAPI specifications, evaluates them against a configurable set of design rules, and produces structured analysis reports for research experiments.

Inspired by the exploratory study *"Are REST APIs for Cloud Computing Well-Designed? An Exploratory Study"* (Petrillo et al., ICSOC 2016). The rule engine implements a subset of those practices (16 of the paper's 73, plus 5 OpenAPI-spec-specific checks); the mapping between each rule and its practice is documented in `docs/practice-traceability.md`, and the full replication audit lives in `docs/research-replication.md`.

## Architecture

```
Browser (React + Vite, 5173)
      │  /api/* proxied to the backends
      ▼
Analyzer Service (8081)  ──HTTP──►  Report Service (8082)  ──►  MySQL
      │
      ├── OpenAPI Parser (swagger-parser)
      ├── Rule Engine (21 rules -> 16 paper practices + 5 OpenAPI checks)
      └── Scoring
```

Components:

- **Frontend** — `New-Project-source-code/` (React + Vite + TypeScript). Pages: Analyze, Results, Reports, Practices catalog, Dashboard.
- **Analyzer Service** — `analyzer-service/` (Spring Boot 3.3, port 8081). Parses OpenAPI 3.x specs and runs the rule engine.
- **Report Service** — `report-service/` (Spring Boot 3.3, port 8082). Persists analysis reports to MySQL and serves them back.

## Technologies

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- MySQL 8
- swagger-parser (OpenAPI 3.x)
- React 18 / Vite / TypeScript
- JUnit 5 + Mockito

## Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8 running locally
- Node.js 18+ (for the frontend)

## Database Setup

Update credentials in `report-service/src/main/resources/application.yml` if needed (default: `root` / `root` on `localhost:3306`, schema `rest_api_analyzer`).

## How to Run

**Terminal 1 — Report Service (port 8082):**

```bash
cd report-service
mvn spring-boot:run
```

**Terminal 2 — Analyzer Service (port 8081):**

```bash
cd analyzer-service
mvn spring-boot:run
```

**Terminal 3 — Frontend (port 5173):**

```bash
cd New-Project-source-code
npm install
npm run dev
```

Then open http://localhost:5173. The Vite dev server proxies `/api/*` to the two services.

## API Endpoints

### Analyzer Service (port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/analyze` | Analyze an OpenAPI specification; saves the report |

### Report Service (port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports` | List reports (newest first) |
| GET | `/api/reports?apiName=foo` | Filter by API name |
| GET | `/api/reports/{id}` | Get full report |
| GET | `/api/reports/{id}/violations` | Get failures only |
| GET | `/api/reports/{id}/export` | Export report as JSON |

## Example Analysis

```bash
curl -X POST http://localhost:8081/api/analyze \
  -H "Content-Type: application/json" \
  -d "{\"apiName\":\"Sample API\",\"source\":\"manual\",\"specification\":\"{...OpenAPI JSON...}\"}"
```

## Testing

Backend unit tests:

```bash
mvn test
```

Sample fixtures: `sample-apis/good-api.json`, `sample-apis/bad-api.json`, `sample-apis/mixed-api.json`

## Scoring

`score = (passedRules / totalRules) × 100`

Each endpoint is checked against all applicable per-endpoint rules; API-wide (spec-level) rules are evaluated once per specification so their identical per-endpoint results do not inflate the denominator. Each rule maps to exactly one practice ID from the paper catalog (`U-`/`RM-`/`E-`/`H-`/`O-`) or an implementation-specific `OPENAPI-` ID. Reports are saved automatically after every analysis.

## Limitations

- The source paper (Petrillo et al., ICSOC 2016) catalogues exactly 73 practices. `paper-notes/practice-catalog.md` is a broader reconstruction (83 rows) drawn from additional reference works — it is not a 1:1 copy of the paper's list.
- 21 rules cover 16 of the paper's 73 practices directly, plus 5 OpenAPI-spec-specific checks (REST-001..021, see `docs/practice-traceability.md`). The 16 paper practices are an *adaptation*: the paper assessed cloud APIs by manual documentation analysis, this tool infers the practices from an OpenAPI document.
- OpenAPI structure analysis only, not runtime behavior.
- The paper studied exactly three APIs (Google Cloud Platform, OpenStack, OCCI 1.2) manually; this project's sample fixtures are not a reproduction of that study. See `docs/research-replication.md` and `results/`.
- Research conclusions are your responsibility.