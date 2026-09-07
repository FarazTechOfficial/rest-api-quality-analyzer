package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoUnderscoreInUriRuleTest {

    private NoUnderscoreInUriRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new NoUnderscoreInUriRule();
        specification = new ApiSpecification();
    }

    @Test
    void testValidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/order-items");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testInvalidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/order_items");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void bracedParameterUnderscoreIsSkipped() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{user_id}");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}
