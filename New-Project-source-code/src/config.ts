// ============================================================================
// Backend configuration
// ============================================================================
// The frontend talks to two Spring Boot services:
//   - Analyzer Service on port 8081 (POST /api/analyze)
//   - Report Service  on port 8082 (GET /api/reports, ...)
//
// In development the Vite dev server proxies both services under the same
// origin (see vite.config.ts), so the empty default values work out of the
// box. To point at deployed services instead, set:
//   VITE_ANALYZER_URL=http://localhost:8081
//   VITE_REPORT_URL=http://localhost:8082
// (browser code can only read VITE_-prefixed variables.)
// ============================================================================

export const ANALYZER_URL: string =
  (import.meta.env.VITE_ANALYZER_URL as string | undefined)?.trim() ?? "";

export const REPORT_URL: string =
  (import.meta.env.VITE_REPORT_URL as string | undefined)?.trim() ?? "";

// Backend endpoint paths. These mirror the actual Spring Boot controllers.
export const ENDPOINTS = {
  analyze: "/api/analyze",
  reports: "/api/reports",
  reportById: (id: string): string => `/api/reports/${encodeURIComponent(id)}`,
};