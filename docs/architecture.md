# Architecture

## Overview

Two Spring Boot microservices communicate via REST. The Analyzer Service is stateless; the Report Service owns persistence.

## Analyzer Service

```
POST /api/analyze
       │
       ▼
AnalysisController
       │
       ▼
AnalysisService
   ├── OpenApiParserService  →  ApiSpecification
   ├── RuleEngine            →  List<RuleResultDto>
   ├── Score calculation
   └── ReportServiceClient   →  HTTP POST to Report Service
```

### Rule Engine

Each rule implements `RestApiRule` with a single `evaluate(endpoint, specification)` method. Spring auto-discovers all `@Component` rule implementations and injects them into `RuleEngine`.

To add a new rule:
1. Create a class in `rule/impl/` implementing `RestApiRule`
2. Annotate with `@Component`
3. Write unit tests with pass and fail cases

## Report Service

```
POST /api/reports          → save analysis + rule results
GET  /api/reports/{id}     → full report
GET  /api/reports          → list summaries
GET  /api/reports/{id}/violations  → failures only
GET  /api/reports/{id}/export      → JSON export
```

### Database Tables

- `api_analysis` — one row per analysis run
- `rule_violation` — one row per rule × endpoint evaluation

## Inter-Service Flow

1. Client sends OpenAPI spec to Analyzer
2. Analyzer parses, evaluates, scores
3. Analyzer POSTs full results to Report Service
4. Report Service persists to MySQL
5. Analyzer returns summary to client
6. Client retrieves full report from Report Service by analysisId

## Scoring

Transparent formula: `passed / total × 100`. No weighting.

Each (endpoint, rule) pair produces one result. API-level score aggregates all endpoint-level results.
