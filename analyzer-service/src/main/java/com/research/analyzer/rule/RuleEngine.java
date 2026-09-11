package com.research.analyzer.rule;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class RuleEngine {

    private final List<RestApiRule> rules;

    public RuleEngine(List<RestApiRule> rules) {
        this.rules = rules;
    }

    public List<RuleResultDto> evaluateAll(ApiSpecification specification) {
        List<RuleResultDto> allResults = new ArrayList<>();

        for (RestApiRule rule : rules) {
            if (rule.isSpecLevel()) {
                if (!specification.getEndpoints().isEmpty()) {
                    ApiEndpoint firstEndpoint = specification.getEndpoints().get(0);
                    allResults.add(rule.evaluate(firstEndpoint, specification));
                }
                continue;
            }

            for (ApiEndpoint endpoint : specification.getEndpoints()) {
                RuleResultDto result = rule.evaluate(endpoint, specification);
                allResults.add(result);
            }
        }

        return allResults;
    }

    public int getRuleCount() {
        return rules.size();
    }
}
