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
                return new RuleResultDto(
                        getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                        false,
                        "Path segment '" + segment + "' contains a file extension.",
                        "Remove the file extension. Use content negotiation via Accept header instead."
                );
            }
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "URI does not contain file extensions.",
                null
        );
    }
}
