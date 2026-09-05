package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class HttpMethodSemanticsRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-003";
    }

    @Override
    public String getRuleName() {
        return "HTTP method semantics";
    }

    @Override
    public String getPracticeId() {
        return "P-2.6";
    }

    @Override
    public String getDescription() {
        return "GET and DELETE should not define a request body. POST, PUT, PATCH typically should.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String method = endpoint.getMethod();
        boolean hasBody = endpoint.isHasRequestBody();

        if ("GET".equals(method) && hasBody) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "GET operation defines a request body, which is uncommon and often unsupported.",
                    "Remove the request body from GET or use POST for complex queries."
            );
        }

        if ("DELETE".equals(method) && hasBody) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "DELETE operation defines a request body, which is uncommon.",
                    "Use path or query parameters instead of a request body for DELETE."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "HTTP method usage appears consistent.",
                null
        );
    }
}
