package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class HttpsServerRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-016";
    }

    @Override
    public String getRuleName() {
        return "HTTPS server URL";
    }

    @Override
    public String getPracticeId() {
        return "P-7.1";
    }

    @Override
    public String getDescription() {
        return "API servers should use HTTPS for transport security.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (specification.getServerUrls().isEmpty()) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "No server URLs defined; cannot evaluate HTTPS usage.",
                    null
            );
        }

        for (String url : specification.getServerUrls()) {
            if (!url.toLowerCase().startsWith("https://")) {
                return new RuleResultDto(
                        getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                        false,
                        "Server URL '" + url + "' does not use HTTPS.",
                        "Use HTTPS for all server URLs to ensure transport security."
                );
            }
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "All server URLs use HTTPS.",
                null
        );
    }
}
