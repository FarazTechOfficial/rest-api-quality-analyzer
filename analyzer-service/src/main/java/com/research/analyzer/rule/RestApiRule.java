package com.research.analyzer.rule;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;

public interface RestApiRule {

    String getRuleId();

    String getRuleName();

    String getPracticeId();

    String getDescription();

    RuleResultDto evaluate(ApiEndpoint endpoint, ApiSpecification specification);

    /**
     * Whether this rule is API-level: its result depends only on the whole
     * specification, not on a per-endpoint property. The rule engine evaluates
     * spec-level rules exactly once per specification (not once per endpoint),
     * otherwise their identical results would inflate score denominators.
     */
    default boolean isSpecLevel() {
        return false;
    }
}
