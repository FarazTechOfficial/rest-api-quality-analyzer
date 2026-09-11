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
        return "GET responses with a body should declare application/json as Content-Type.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        if (!"GET".equals(endpoint.getMethod())) {
            return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                    endpoint.getPath(), endpoint.getMethod(), true,
                    "Not a GET operation; Content-Type check not applicable.", null);
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
                return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                        endpoint.getPath(), endpoint.getMethod(), false,
                        "GET response declares the non-JSON media type '" + concreteNonJsonType + "'.",
                        "Declare application/json as the Content-Type for GET responses with JSON bodies.");
            }
        }

        if (sawDeclaredBody) {
            return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                    endpoint.getPath(), endpoint.getMethod(), true,
                    "GET response bodies declare application/json.", null);
        }

        return new RuleResultDto(getRuleId(), getRuleName(), getPracticeId(),
                endpoint.getPath(), endpoint.getMethod(), true,
                "No GET response declares a body Content-Type; nothing to check.", null);
    }

    private boolean isWildcard(String mediaType) {
        return "*/*".equals(JsonRepresentationRule.baseType(mediaType));
    }
}