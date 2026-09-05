# API Design Notes

Document your REST API design decisions for this prototype here.

## Analyzer Service API

- `POST /api/analyze` — accepts OpenAPI JSON as a string in the request body

## Report Service API

- `POST /api/reports` — internal endpoint called by Analyzer Service
- `GET /api/reports/{id}` — retrieve stored analysis
- `GET /api/reports` — list analyses with optional `apiName` filter

## Error Response Format

```json
{
  "timestamp": "2026-09-04T12:00:00Z",
  "status": 400,
  "message": "Description of the error"
}
```
