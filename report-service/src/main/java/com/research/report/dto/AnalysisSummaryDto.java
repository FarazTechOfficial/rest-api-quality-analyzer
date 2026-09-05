package com.research.report.dto;

import java.time.LocalDateTime;

public class AnalysisSummaryDto {

    private String analysisId;
    private String apiName;
    private String version;
    private String source;
    private LocalDateTime analyzedAt;
    private int totalRules;
    private int passedRules;
    private int failedRules;
    private int skippedRules;
    private double score;

    public String getAnalysisId() {
        return analysisId;
    }

    public void setAnalysisId(String analysisId) {
        this.analysisId = analysisId;
    }

    public String getApiName() {
        return apiName;
    }

    public void setApiName(String apiName) {
        this.apiName = apiName;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }

    public int getTotalRules() {
        return totalRules;
    }

    public void setTotalRules(int totalRules) {
        this.totalRules = totalRules;
    }

    public int getPassedRules() {
        return passedRules;
    }

    public void setPassedRules(int passedRules) {
        this.passedRules = passedRules;
    }

    public int getFailedRules() {
        return failedRules;
    }

    public void setFailedRules(int failedRules) {
        this.failedRules = failedRules;
    }

    public int getSkippedRules() {
        return skippedRules;
    }

    public void setSkippedRules(int skippedRules) {
        this.skippedRules = skippedRules;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }
}
