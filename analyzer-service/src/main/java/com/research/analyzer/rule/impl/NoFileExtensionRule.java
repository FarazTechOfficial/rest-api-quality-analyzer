package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class NoFileExtensionRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-011";
    }

    @Override
    public String getRuleName() {
        return "No file extension in URI";
    }

    @Override
    public String getPracticeId() {
        return "U-6";
    }

    @Override
    public String getDescription() {
        return "URIs should not contain file extensions like .json, .xml, .html.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();
        String[] segments = path.split("/");

        for (String segment : segments) {
            if (segment.isEmpty() || segment.startsWith("{")) {
                continue;
            }
            if (segment.matches(".*\\.(json|xml|html|csv|yaml|yml|txt|pdf|png|jpg|jpeg|gif|svg)$")) {
                RuleResultDto result = new RuleResultDto();
                result.setRuleId(getRuleId());
                result.setRuleName(getRuleName());
                result.setPracticeId(getPracticeId());
                result.setEndpoint(endpoint.getPath());
                result.setMethod(endpoint.getMethod());
                result.setPassed(false);
                result.setMessage("Path segment '" + segment + "' has a file extension.");
                result.setRecommendation("Remove the file extension and use content negotiation instead.");
                return result;
            }
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("URI has no file extensions.");
        return result;
    }
}
