# REST API Design Quality Analyzer

A research prototype for analyzing REST API specifications against selected design practices.

This prototype is intended to support an empirical investigation of REST API design practices. It does **not** claim academic findings on its own.

## Problem Being Investigated

REST APIs are widely used in cloud computing, but their design quality varies. This tool accepts OpenAPI specifications, evaluates them against a configurable set of design rules, and produces structured analysis reports for research experiments.

Inspired by the exploratory study: *"Are REST APIs for Cloud Computing Well-Designed? An Exploratory Study"* (Petrillo et al.). The initial rules are **placeholder implementations** for engine testing — replace them with your selected practices after studying the paper.

## Architecture

```
Client (Postman)
      │
      ▼
Analyzer Service (8081)  ──HTTP──►  Report Service (8082)  ──►  MySQL
      │
      ├── OpenAPI Parser
      ├── Rule Engine (10 placeholder rules)
      └── Scoring
```

## Technologies

- Java 17
- Spring Boot 3.3
- Spring Data JPA
- MySQL 8
- swagger-parser (OpenAPI 3.x)
- JUnit 5 + Mockito
- Maven (multi-module)

## Prerequisites

- JDK 17+
- Maven 3.8+
- MySQL 8 running locally

## Database Setup

Update credentials in `report-service/src/main/resources/application.yml` if needed (default: `root` / `root`).

## How to Run

**Terminal 1 — Report Service:**

```bash
cd report-service
mvn spring-boot:run
```

**Terminal 2 — Analyzer Service:**

```bash
cd analyzer-service
mvn spring-boot:run
```

## API Endpoints

### Analyzer Service (port 8081)

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/analyze` | Analyze an OpenAPI specification |

### Report Service (port 8082)

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/reports` | List all reports |
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

```bash
mvn test
```

Sample fixtures: `sample-apis/good-api.json`, `bad-api.json`, `mixed-api.json`

## Placeholder Rules

10 rules (REST-001 to REST-010) are generic REST conventions for engine testing. **Not claimed to come from the paper.** Replace after you study the catalog.

## Scoring

`score = (passedRules / totalRules) × 100`

## Limitations

- Placeholder rules only
- OpenAPI structure analysis, not runtime behavior
- No auth, no CSV export, no Docker
- Research conclusions are your responsibility
