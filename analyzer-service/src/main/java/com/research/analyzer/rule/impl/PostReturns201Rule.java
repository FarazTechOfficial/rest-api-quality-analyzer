package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class PostReturns201Rule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-013";
    }

    @Override
    public String getRuleName() {
        return "POST returns 201 Created";
    }

    @Override
    public String getPracticeId() {
        return "E-3";
    }

    @Override
    public String getDescription() {
        return "POST operations that create resources should return 201 Created.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"POST".equals(endpoint.getMethod())) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Rule applies only to POST operations.");
            return result;
        }

        boolean has201 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("201".equals(response.getStatusCode())) {
                has201 = true;
                break;
            }
        }

        if (!has201) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("POST does not define a 201 Created response.");
            result.setRecommendation("Add a 201 Created response for resource creation operations.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("POST defines a 201 Created response.");
        return result;
    }
}