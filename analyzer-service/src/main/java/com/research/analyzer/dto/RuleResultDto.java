package com.research.analyzer.dto;

import com.research.analyzer.model.RuleResultStatus;

public class RuleResultDto {

    private String ruleId;
    private String ruleName;
    private String practiceId;
    private String endpoint;
    private String method;
    private RuleResultStatus status;
    private boolean passed;
    private String message;
    private String recommendation;

    public RuleResultDto() {
    }

    public RuleResultDto(String ruleId, String ruleName, String practiceId,
                         String endpoint, String method,
                         RuleResultStatus status, String message, String recommendation) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.practiceId = practiceId;
        this.endpoint = endpoint;
        this.method = method;
        this.status = status;
        this.passed = (status == RuleResultStatus.PASSED);
        this.message = message;
        this.recommendation = recommendation;
    }

    public RuleResultDto(String ruleId, String ruleName, String practiceId,
                         String endpoint, String method,
                         boolean passed, String message, String recommendation) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.practiceId = practiceId;
        this.endpoint = endpoint;
        this.method = method;
        this.status = passed ? RuleResultStatus.PASSED : RuleResultStatus.FAILED;
        this.passed = passed;
        this.message = message;
        this.recommendation = recommendation;
    }

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(String practiceId) {
        this.practiceId = practiceId;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public RuleResultStatus getStatus() {
        return status;
    }

    public void setStatus(RuleResultStatus status) {
        this.status = status;
        this.passed = (status == RuleResultStatus.PASSED);
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }
}
