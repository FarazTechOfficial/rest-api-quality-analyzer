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

        for (ApiEndpoint endpoint : specification.getEndpoints()) {
            for (RestApiRule rule : rules) {
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
