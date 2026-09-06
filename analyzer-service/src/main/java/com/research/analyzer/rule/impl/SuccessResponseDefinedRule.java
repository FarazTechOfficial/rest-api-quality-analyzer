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
        return "Operations should document at least one 2xx success response. "
                + "This checks presence of a 2xx response only; it does NOT verify "
                + "correctness of the success status semantics.";
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
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "No 2xx success response is documented for this operation.",
                    "Document a success response such as 200 or 201."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "At least one success response is documented.",
                null
        );
    }
}
