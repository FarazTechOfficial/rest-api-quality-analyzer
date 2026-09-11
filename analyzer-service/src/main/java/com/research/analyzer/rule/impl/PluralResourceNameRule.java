package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class PluralResourceNameRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-002";
    }

    @Override
    public String getRuleName() {
        return "Plural resource names";
    }

    @Override
    public String getPracticeId() {
        return "U-9";
    }

    @Override
    public String getDescription() {
        return "Collection resource paths should use plural nouns.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();
        String[] segments = path.split("/");

        for (int i = 0; i < segments.length; i++) {
            String segment = segments[i];
            if (segment.isEmpty() || segment.startsWith("{")) {
                continue;
            }
            if (segment.matches("v\\d+")) {
                continue;
            }

            boolean isCollection = (i + 1 < segments.length && segments[i + 1].startsWith("{"));
            boolean isLastSegment = (i == segments.length - 1);

            if (isCollection || isLastSegment) {
                if (!isPlural(segment)) {
                    return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                            endpoint.getPath(), endpoint.getMethod(), false,
                            "The path segment '" + segment + "' looks singular for a collection resource.",
                            "Use a plural noun such as /users instead of /user.");
                }
            }
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "Resource path uses plural naming where appropriate.", null);
    }

    private boolean isPlural(String word) {
        if (word.endsWith("s") && word.length() > 2) {
            return true;
        }
        return word.endsWith("ies") || word.endsWith("es");
    }
}
