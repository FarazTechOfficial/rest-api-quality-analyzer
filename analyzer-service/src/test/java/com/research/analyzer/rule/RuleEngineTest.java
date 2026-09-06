package com.research.analyzer.rule;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import com.research.analyzer.rule.impl.ApiVersionPresentRule;
import com.research.analyzer.rule.impl.ErrorResponseDefinedRule;
import com.research.analyzer.rule.impl.HttpMethodSemanticsRule;
import com.research.analyzer.rule.impl.LowercasePathRule;
import com.research.analyzer.rule.impl.NoTrailingSlashRule;
import com.research.analyzer.rule.impl.OperationIdPresentRule;
import com.research.analyzer.rule.impl.PathParameterInUriRule;
import com.research.analyzer.rule.impl.PluralResourceNameRule;
import com.research.analyzer.rule.impl.ResourceOrientedUriRule;
import com.research.analyzer.rule.impl.SuccessResponseDefinedRule;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RuleEngineTest {

    private static final int ENDPOINT_LEVEL_RULES = 9; // 10 rules under test minus ApiVersionPresentRule (spec-level)

    @Test
    void testEvaluateAllRunsAllRules() {
        List<RestApiRule> rules = buildRules();

        RuleEngine engine = new RuleEngine(rules);

        ApiSpecification spec = new ApiSpecification();
        spec.setVersion("1.0.0");

        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/getUsers/");
        endpoint.setMethod("GET");
        endpoint.setHasRequestBody(true);
        spec.getEndpoints().add(endpoint);

        List<RuleResultDto> results = engine.evaluateAll(spec);

        assertEquals(ENDPOINT_LEVEL_RULES + 1, results.size());
        assertEquals(rules.size(), engine.getRuleCount());

        int failures = 0;
        for (RuleResultDto result : results) {
            if (!result.isPassed()) {
                failures++;
            }
        }
        assertFalse(failures == 0);
    }

    @Test
    void specLevelRulesAreEvaluatedOncePerSpecification() {
        List<RestApiRule> rules = buildRules();

        RuleEngine engine = new RuleEngine(rules);

        ApiSpecification spec = new ApiSpecification();
        spec.setVersion("1.0.0");

        ApiEndpoint first = new ApiEndpoint();
        first.setPath("/users");
        first.setMethod("GET");
        spec.getEndpoints().add(first);

        ApiEndpoint second = new ApiEndpoint();
        second.setPath("/orders/{orderId}");
        second.setMethod("GET");
        spec.getEndpoints().add(second);

        List<RuleResultDto> results = engine.evaluateAll(spec);

        // 9 endpoint-level rules x 2 endpoints + 1 spec-level rule = 19.
        assertEquals(ENDPOINT_LEVEL_RULES * 2 + 1, results.size(),
                "Spec-level rules must not be duplicated once per endpoint.");

        long apiVersionResults = results.stream()
                .filter(r -> "REST-009".equals(r.getRuleId()))
                .count();
        assertEquals(1, apiVersionResults);
    }

    private List<RestApiRule> buildRules() {
        return Arrays.asList(
                new ResourceOrientedUriRule(),
                new PluralResourceNameRule(),
                new HttpMethodSemanticsRule(),
                new LowercasePathRule(),
                new OperationIdPresentRule(),
                new ErrorResponseDefinedRule(),
                new SuccessResponseDefinedRule(),
                new NoTrailingSlashRule(),
                new ApiVersionPresentRule(),
                new PathParameterInUriRule()
        );
    }
}
