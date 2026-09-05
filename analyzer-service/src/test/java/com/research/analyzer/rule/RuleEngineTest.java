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

    @Test
    void testEvaluateAllRunsAllRules() {
        List<RestApiRule> rules = Arrays.asList(
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

        RuleEngine engine = new RuleEngine(rules);

        ApiSpecification spec = new ApiSpecification();
        spec.setVersion("1.0.0");

        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/getUsers/");
        endpoint.setMethod("GET");
        endpoint.setHasRequestBody(true);
        spec.getEndpoints().add(endpoint);

        List<RuleResultDto> results = engine.evaluateAll(spec);

        assertEquals(10, results.size());
        assertEquals(10, engine.getRuleCount());

        int failures = 0;
        for (RuleResultDto result : results) {
            if (!result.isPassed()) {
                failures++;
            }
        }
        assertFalse(failures == 0);
    }
}
