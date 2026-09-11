package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiParameter;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.RestApiRule;
import org.springframework.stereotype.Component;

@Component
public class PathParameterInUriRule implements RestApiRule {

    @Override
    public String getRuleId() {
        return "REST-010";
    }

    @Override
    public String getRuleName() {
        return "Path parameters in URI";
    }

    @Override
    public String getPracticeId() {
        return "OPENAPI-02";
    }

    @Override
    public String getDescription() {
        return "Path parameters should appear in the URI template.";
    }

    @Override
    public RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification) {
        String path = endpoint.getPath();

        for (ApiParameter param : endpoint.getParameters()) {
            if ("path".equals(param.getLocation())) {
                String placeholder = "{" + param.getName() + "}";
                if (!path.contains(placeholder)) {
                    RuleResultDto result = new RuleResultDto();
                    result.setRuleId(getRuleId());
                    result.setRuleName(getRuleName());
                    result.setPracticeId(getPracticeId());
                    result.setEndpoint(endpoint.getPath());
                    result.setMethod(endpoint.getMethod());
                    result.setPassed(false);
                    result.setMessage("Path param '" + param.getName() + "' is declared but not found in the URI.");
                    result.setRecommendation("Add {" + param.getName() + "} to the path template.");
                    return result;
                }
            }
        }

        RuleResultDto result = new RuleResultDto();
        result.setRuleId(getRuleId());
        result.setRuleName(getRuleName());
        result.setPracticeId(getPracticeId());
        result.setEndpoint(endpoint.getPath());
        result.setMethod(endpoint.getMethod());
        result.setPassed(true);
        result.setMessage("Path parameters match the URI template.");
        return result;
    }
}
