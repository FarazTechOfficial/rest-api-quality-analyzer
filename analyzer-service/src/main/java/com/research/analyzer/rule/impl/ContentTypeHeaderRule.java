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
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("Not a GET operation; Content-Type check not applicable.");
            return result;
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
                RuleResultDto result = new RuleResultDto();
                result.setRuleId(getRuleId());
                result.setRuleName(getRuleName());
                result.setPracticeId(getPracticeId());
                result.setEndpoint(endpoint.getPath());
                result.setMethod(endpoint.getMethod());
                result.setPassed(false);
                result.setMessage("GET response declares the non-JSON media type '" + concreteNonJsonType + "'.");
                result.setRecommendation("Declare application/json as the Content-Type for GET responses with JSON bodies.");
                return result;
            }
        }

        if (sawDeclaredBody) {
            RuleResultDto result = new RuleResultDto();
            result.setRuleId(getRuleId());
            result.setRuleName(getRuleName());
            result.setPracticeId(getPracticeId());
            result.setEndpoint(endpoint.getPath());
            result.setMethod(endpoint.getMethod());
            result.setPassed(true);
            result.setMessage("GET response bodies declare application/json.");
            return result;
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("No GET response declares a body Content-Type; nothing to check.");
        return result;
    }

    private boolean isWildcard(String mediaType) {
        return "*/*".equals(JsonRepresentationRule.baseType(mediaType));
    }
}