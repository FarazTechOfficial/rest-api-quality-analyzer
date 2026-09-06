package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class ContentTypeHeaderRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-021";
    }

    @Override
    public String getRuleName() {
        return "Content-Type is JSON (GET responses)";
    }

    @Override
    public String getPracticeId() {
        return "H-2";
    }

    @Override
    public String getDescription() {
        return "For GET responses with a body, the declared Content-Type should be application/json "
                + "(paper practice H-2). Request-side Content-Type (H-1) and charset (H-3) are not "
                + "verifiable from the parsed OpenAPI subset.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"GET".equals(endpoint.getMethod())) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "Not a GET operation; Content-Type check not applicable (H-2 targets GET responses).",
                    null
            );
        }

        boolean sawDeclaredBody = false;
        for (ApiResponseInfo response : endpoint.getResponses()) {
            if (response.getContentTypes().isEmpty()) {
                continue;
            }
            sawDeclaredBody = true;

            boolean hasJson = false;
            String concreteNonJsonType = null;
            for (String mediaType : response.getContentTypes()) {
                if (JsonRepresentationRule.isJsonMediaType(mediaType)) {
                    hasJson = true;
                    break;
                }
                if (!isWildcard(mediaType) && concreteNonJsonType == null) {
                    concreteNonJsonType = mediaType;
                }
            }
            if (hasJson) {
                continue;
            }
            if (concreteNonJsonType != null) {
                return new RuleResultDto(
                        getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                        false,
                        "GET response documents the non-JSON media type '" + concreteNonJsonType
                                + "'; Content-Type for GET responses with JSON bodies should be application/json.",
                        "Declare application/json as the Content-Type for GET responses with JSON bodies (paper practice H-2)."
                );
            }
        }

        if (sawDeclaredBody) {
            return new RuleResultDto(
                    getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                    true,
                    "GET response bodies declare application/json.",
                    null
            );
        }

        return new RuleResultDto(
                getRuleId(), getRuleName(), getPracticeId(), endpoint.getPath(), endpoint.getMethod(),
                true,
                "No GET response declares a body Content-Type; nothing to check (H-2).",
                null
        );
    }

    private boolean isWildcard(String mediaType) {
        return "*/*".equals(JsonRepresentationRule.baseType(mediaType));
    }
}