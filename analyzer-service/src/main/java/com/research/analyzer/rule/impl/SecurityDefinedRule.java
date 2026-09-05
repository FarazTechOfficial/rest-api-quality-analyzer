package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class SecurityDefinedRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-018";
    }

    @Override
    public String getRuleName() {
        return "Security scheme defined";
    }

    @Override
    public String getPracticeId() {
        return "P-7.2";
    }

    @Override
    public String getDescription() {
        return "API should define at least one security scheme for authentication/authorization.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (specification.getSecuritySchemes().isEmpty()) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    false,
                    "No security schemes are defined in the API specification.",
                    "Define at least one security scheme (e.g., OAuth2, API key, JWT Bearer)."
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "Security scheme(s) are defined: " + String.join(", ", specification.getSecuritySchemes()),
                null
        );
    }
}
