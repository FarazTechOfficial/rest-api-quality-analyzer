package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class JsonRepresentationRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-019";
    }

    @Override
    public String getRuleName() {
        return "JSON representation";
    }

    @Override
    public String getPracticeId() {
        return "O-8";
    }

    @Override
    public String getDescription() {
        return "The API should provide JSON-based representations of its resources.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        for (ApiEndpoint candidate : specification.getEndpoints()) {
            for (String mediaType : candidate.getRequestContentTypes()) {
                if (isJsonMediaType(mediaType)) {
                    RuleResultDto pass = new RuleResultDto();
                    pass.setRuleId(getRuleId());
                    pass.setRuleName(getRuleName());
                    pass.setPracticeId(getPracticeId());
                    pass.setEndpoint(endpoint.getPath());
                    pass.setMethod(endpoint.getMethod());
                    pass.setPassed(true);
                    pass.setMessage("The API provides JSON-based representations of its resources.");
                    return pass;
                }
            }
            for (ApiResponseInfo response : candidate.getResponses()) {
                for (String mediaType : response.getContentTypes()) {
                    if (isJsonMediaType(mediaType)) {
                        RuleResultDto pass = new RuleResultDto();
                        pass.setRuleId(getRuleId());
                        pass.setRuleName(getRuleName());
                        pass.setPracticeId(getPracticeId());
                        pass.setEndpoint(endpoint.getPath());
                        pass.setMethod(endpoint.getMethod());
                        pass.setPassed(true);
                        pass.setMessage("The API provides JSON-based representations of its resources.");
                        return pass;
                    }
                }
            }
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(false);
        result.setMessage("No request body or response declares a JSON media type (application/json).");
        result.setRecommendation("Declare application/json content for request or response bodies.");
        return result;
    }

    static boolean isJsonMediaType(String mediaType) {
        String base = baseType(mediaType);
        return base.startsWith("application/json") || base.endsWith("+json");
    }

    static String baseType(String mediaType) {
        if (mediaType == null) {
            return "";
        }
        return mediaType.toLowerCase().split(";")[0].trim();
    }

    @Override
    public boolean isSpecLevel() {
        return true;
    }
}