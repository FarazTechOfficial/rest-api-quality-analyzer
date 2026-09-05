// ============================================================================
// Service layer — the single place that talks to the Spring Boot backend.
//
// Two Spring Boot services are used:
//   - Analyzer Service (POST /api/analyze) — runs the rule engine
//   - Report Service   (GET /api/reports)  — stores and returns reports
//
// In development the Vite dev server proxies both services under the same
// origin (see vite.config.ts). ANALYZER_URL / REPORT_URL can be set to point
// at deployed services instead.
//
// The backend is the source of truth. No analysis values are invented here:
// scores, counts and rule results are taken from the service responses.
// ============================================================================

import { ANALYZER_URL, ENDPOINTS, REPORT_URL } from "../config";
import {
  IMPLEMENTED_PRACTICES_COUNT,
  RESEARCH_CATALOG_COUNT,
  categoryForPracticeId,
} from "../data/practicesCatalog";
import type {
  AnalysisReport,
  PracticeStatus,
  ReportSummary,
  RuleResult,
  Stats,
} from "../types";

export class ApiError extends Error {
  status?: number;

  constructor(message: string, status?: number) {
    super(message);
    this.name = "ApiError";
    this.status = status;
  }
}

const VALID_STATUSES: PracticeStatus[] = [
  "PASSED",
  "FAILED",
  "MANUAL_REVIEW",
  "NOT_EVALUATED",
  "NOT_APPLICABLE",
];

function normalizeStatus(value: unknown): PracticeStatus {
  const s = String(value ?? "").toUpperCase().replace(/[\s-]+/g, "_");
  return VALID_STATUSES.includes(s as PracticeStatus)
    ? (s as PracticeStatus)
    : "NOT_EVALUATED";
}

function toInt(value: unknown): number {
  const n = typeof value === "number" ? value : Number(value);
  return Number.isFinite(n) ? Math.round(n) : 0;
}

function toScore(value: unknown): number {
  const n = Number(value);
  return Number.isFinite(n) ? n : 0;
}

function errorMessageFor(status: number): string {
  if (status === 400)
    return "The request was rejected. Check the uploaded OpenAPI specification and try again.";
  if (status === 404) return "The requested report could not be found.";
  if (status === 502 || status === 504)
    return "The backend is temporarily unreachable. Make sure both the Analyzer and Report services are running.";
  if (status === 503)
    return "The Analyzer service could not reach the Report service. Make sure both services are running.";
  if (status === 500)
    return "The backend reported an internal error. If this keeps happening, make sure both services are running.";
  return "The backend returned an unexpected error.";
}

async function request<T>(
  baseUrl: string,
  path: string,
  options?: RequestInit
): Promise<T> {
  let res: Response;
  try {
    res = await fetch(`${baseUrl}${path}`, options);
  } catch {
    throw new ApiError(
      "Unable to connect to the backend. Please make sure the Analyzer and Report services are running."
    );
  }

  if (!res.ok) {
    let message = errorMessageFor(res.status);
    try {
      const body = (await res.json()) as { message?: unknown };
      if (
        typeof body?.message === "string" &&
        body.message.trim().length > 0
      ) {
        message = body.message;
      }
    } catch {
      // response body was not JSON — keep the default message
    }
    throw new ApiError(message, res.status);
  }

  return res.json() as Promise<T>;
}

// ------------------------------ Parsers ------------------------------------
// Field names follow the Spring Boot DTOs exactly.

function parseRuleResult(raw: Record<string, unknown>): RuleResult {
  const practiceId = String(raw.practiceId ?? "");
  return {
    ruleId: String(raw.ruleId ?? ""),
    ruleName: String(raw.ruleName ?? ""),
    practiceId,
    category: categoryForPracticeId(practiceId),
    endpoint: String(raw.endpoint ?? ""),
    method: String(raw.method ?? ""),
    status: normalizeStatus(raw.status),
    passed: raw.passed === true || raw.passed === "true" || raw.status === "PASSED",
    message: String(raw.message ?? ""),
    recommendation: String(raw.recommendation ?? ""),
  };
}

function parseSummary(raw: Record<string, unknown>): ReportSummary {
  return {
    analysisId: String(raw.analysisId ?? ""),
    apiName: String(raw.apiName ?? ""),
    version: String(raw.version ?? ""),
    source: String(raw.source ?? ""),
    analyzedAt: String(raw.analyzedAt ?? ""),
    totalRules: toInt(raw.totalRules),
    passedRules: toInt(raw.passedRules),
    failedRules: toInt(raw.failedRules),
    skippedRules: toInt(raw.skippedRules),
    score: toScore(raw.score),
  };
}

function parseReport(raw: Record<string, unknown>): AnalysisReport {
  const list = Array.isArray(raw.results) ? raw.results : [];
  return {
    ...parseSummary(raw),
    results: list.map((item) => parseRuleResult(item as Record<string, unknown>)),
  };
}

function listFrom(raw: unknown): unknown[] {
  return Array.isArray(raw) ? raw : [];
}

// ---------------------------------- API -------------------------------------

export interface AnalyzePayload {
  apiName: string;
  version: string;
  spec: string;
}

export interface DashboardData {
  stats: Stats;
  reports: ReportSummary[];
}

export const api = {
  async analyze(payload: AnalyzePayload): Promise<AnalysisReport> {
    const raw = await request<Record<string, unknown>>(
      ANALYZER_URL,
      ENDPOINTS.analyze,
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          apiName: payload.apiName,
          version: payload.version,
          specification: payload.spec,
        }),
      }
    );
    return parseReport(raw);
  },

  async getReports(): Promise<ReportSummary[]> {
    const raw = await request<unknown>(REPORT_URL, ENDPOINTS.reports);
    return listFrom(raw)
      .map((item) => parseSummary(item as Record<string, unknown>))
      .sort((a, b) => {
        const ta = new Date(a.analyzedAt).getTime();
        const tb = new Date(b.analyzedAt).getTime();
        return Number.isFinite(ta) && Number.isFinite(tb) ? tb - ta : 0;
      });
  },

  async getReport(id: string): Promise<AnalysisReport> {
    const raw = await request<Record<string, unknown>>(
      REPORT_URL,
      ENDPOINTS.reportById(id)
    );
    return parseReport(raw);
  },

  async getDashboardData(): Promise<DashboardData> {
    const reports = await this.getReports();
    return {
      stats: {
        totalPractices: RESEARCH_CATALOG_COUNT,
        implementedPractices: IMPLEMENTED_PRACTICES_COUNT,
        analyzedApis: reports.length,
      },
      reports,
    };
  },
};