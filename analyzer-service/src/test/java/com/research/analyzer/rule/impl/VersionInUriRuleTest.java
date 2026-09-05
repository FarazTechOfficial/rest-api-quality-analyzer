package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersionInUriRuleTest {

    private VersionInUriRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new VersionInUriRule();
        specification = new ApiSpecification();
    }

    @Test
    void testNoVersionInUri() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testVersionInUri() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/v2/orders");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }
}
