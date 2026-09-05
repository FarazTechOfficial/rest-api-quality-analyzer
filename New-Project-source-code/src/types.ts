// ============================================================================
// Shared types.
// These mirror the Spring Boot DTOs:
//   Analyzer: AnalysisSummaryResponse, RuleResultDto
//   Report:   AnalysisSummaryDto, AnalysisReportResponse
// The backend is the source of truth for every analysis value; the frontend
// only displays what the services return.
// ============================================================================

export type PracticeStatus =
  | "PASSED"
  | "FAILED"
  | "MANUAL_REVIEW"
  | "NOT_EVALUATED"
  | "NOT_APPLICABLE";

export interface RuleResult {
  ruleId: string;
  ruleName: string;
  practiceId: string;
  category: string;
  endpoint: string;
  method: string;
  status: PracticeStatus;
  passed: boolean;
  message: string;
  recommendation: string;
}

export interface ReportSummary {
  analysisId: string;
  apiName: string;
  version: string;
  source: string;
  analyzedAt: string;
  totalRules: number;
  passedRules: number;
  failedRules: number;
  skippedRules: number;
  score: number;
}

export interface AnalysisReport extends ReportSummary {
  results: RuleResult[];
}

export interface Practice {
  id: string;
  name: string;
  category: string;
  automatable: boolean;
  priority: "HIGH" | "MEDIUM" | "LOW";
  implemented: boolean;
  ruleId: string | null;
}

export interface Stats {
  totalPractices: number;
  implementedPractices: number;
  analyzedApis: number;
}