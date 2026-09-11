package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class SuccessResponseDefinedRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-007";
    }

    @Override
    public String getRuleName() {
        return "At least one success response documented";
    }

    @Override
    public String getPracticeId() {
        return "OPENAPI-05";
    }

    @Override
    public String getDescription() {
        return "Operations should document at least one 2xx success response.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        boolean hasSuccessResponse = false;

        for (ApiResponseInfo response : endpoint.getResponses()) {
            if (response.isSuccess()) {
                hasSuccessResponse = true;
                break;
            }
        }

        if (!hasSuccessResponse) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(false);
            result.setMessage("No 2xx success response is documented for this operation.");
            result.setRecommendation("Document a success response such as 200 or 201.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("At least one success response is documented.");
        return result;
    }
}