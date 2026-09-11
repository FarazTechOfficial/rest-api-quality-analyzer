package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class DeleteReturns204Rule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-014";
    }

    @Override
    public String getRuleName() {
        return "DELETE returns 204 No Content";
    }

    @Override
    public String getPracticeId() {
        return "E-5";
    }

    @Override
    public String getDescription() {
        return "DELETE operations should return 204 No Content on success.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"DELETE".equals(endpoint.getMethod())) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Rule applies only to DELETE operations.");
            return result;
        }

        boolean has204 = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if ("204".equals(response.getStatusCode())) {
                has204 = true;
                break;
            }
        }

        if (!has204) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("DELETE does not define a 204 No Content response.");
            result.setRecommendation("Add a 204 No Content response for successful deletion.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("DELETE defines a 204 No Content response.");
        return result;
    }
}