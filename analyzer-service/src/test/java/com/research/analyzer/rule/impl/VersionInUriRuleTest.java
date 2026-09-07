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

    @Test
    void nestedAndSuffixedVersionSegmentsFail() {
        ApiEndpoint nested = new ApiEndpoint();
        nested.setPath("/api/v2/orders");
        nested.setMethod("GET");
        ApiEndpoint suffixed = new ApiEndpoint();
        suffixed.setPath("/v2beta/orders");
        suffixed.setMethod("GET");

        assertFalse(rule.evaluate(nested, specification).isPassed());
        assertFalse(rule.evaluate(suffixed, specification).isPassed());
    }

    @Test
    void resourceStartingWithVIsFalsePositive() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/v8-engine");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void uppercaseOrBareNumericVersionIsNotDetected() {
        ApiEndpoint uppercase = new ApiEndpoint();
        uppercase.setPath("/V1/users");
        uppercase.setMethod("GET");
        ApiEndpoint bare = new ApiEndpoint();
        bare.setPath("/api/1.0/users");
        bare.setMethod("GET");

        assertTrue(rule.evaluate(uppercase, specification).isPassed());
        assertTrue(rule.evaluate(bare, specification).isPassed());
    }
}
