package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiParameter;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class PaginationRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-020";
    }

    @Override
    public String getRuleName() {
        return "Pagination parameters";
    }

    @Override
    public String getPracticeId() {
        return "O-13";
    }

    @Override
    public String getDescription() {
        return "Collection GET endpoints should document pagination parameters.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"GET".equals(endpoint.getMethod())) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Not a GET operation; pagination check not applicable.");
            return result;
        }

        String lastSegment = lastPathSegment(endpoint.getPath());
        if (lastSegment == null || lastSegment.startsWith("{")) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Path targets a single resource; pagination check not applicable.");
            return result;
        }

        if (!isPlural(lastSegment)) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Path does not look like a collection endpoint; pagination check not applicable.");
            return result;
        }

        if (hasPaginationParameter(endpoint)) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Collection GET documents pagination parameters.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(false);
        result.setMessage("Collection GET '" + endpoint.getPath() + "' does not document pagination parameters "
                + "(e.g. page, limit, offset, cursor).");
        result.setRecommendation("Split large responses across requests by documenting pagination parameters like limit and offset.");
        return result;
    }

    private String lastPathSegment(String path) {
        String[] segments = path.split("/");
        for (int i = segments.length - 1; i >= 0; i--) {
            if (!segments[i].isEmpty()) {
                return segments[i];
            }
        }
        return null;
    }

    private boolean isPlural(String word) {
        if (word.endsWith("s") && word.length() > 2) {
            return true;
        }
        return word.endsWith("es") || word.endsWith("ies");
    }

    private boolean hasPaginationParameter(ApiEndpoint endpoint) {
        for (ApiParameter parameter : endpoint.getParameters()) {
            if ("query".equals(parameter.getLocation()) && isPaginationParameter(parameter.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isPaginationParameter(String name) {
        if (name == null) {
            return false;
        }
        String n = name.toLowerCase();
        return n.contains("page") || n.contains("limit") || n.contains("offset")
                || n.contains("cursor") || n.contains("per_") || n.contains("rows")
                || n.equals("start") || n.endsWith("size");
    }
}