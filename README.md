# REST API Design Quality Analyzer

A research prototype that accepts OpenAPI specifications, evaluates them against a set of empirically-motivated REST API design practices, and produces structured analysis reports.

This prototype is intended to support an empirical investigation of REST API design practices. It does **not** claim academic findings on its own.

## Problem Being Investigated

REST APIs are widely used in cloud computing, but their design quality varies. This tool accepts OpenAPI specifications, evaluates them against a configurable set of design rules, and produces structured analysis reports for research experiments.

Inspired by the exploratory study *"Are REST APIs for Cloud Computing Well-Designed? An Exploratory Study"* (Petrillo et al.). The rule engine is implemented for the automatable practices in the reconstructed catalog (`paper-notes/practice-catalog.md`); the mapping between each rule and its catalog practice is documented in `docs/practice-traceability.md`.

## Architecture

```
Browser (React + Vite, 5173)
      │  /api/* proxied to the backends
      ▼
Analyzer Service (8081)  ──HTTP──►  Report Service (8082)  ──►  MySQL
      │
      ├── OpenAPI Parser (swagger-parser)
      ├── Rule Engine (18 rules -> 16 practices)
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

Each endpoint is checked against all applicable rules; each rule maps to exactly one catalog practice. Reports are saved automatically after every analysis.

## Limitations

- The catalog (`paper-notes/practice-catalog.md`) is a reconstruction: the source paper reports 73 practices, while the extracted catalog lists 83 rows because it draws on the broader rule references and is not a one-to-one copy.
- 18 rules cover 16 of the 83 catalog practices; the remaining practices are catalog metadata and are not evaluated.
- OpenAPI structure analysis only, not runtime behavior.
- Research conclusions are your responsibility.