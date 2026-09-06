package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HttpMethodSemanticsRuleTest {

    private HttpMethodSemanticsRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new HttpMethodSemanticsRule();
        specification = new ApiSpecification();
    }

    @Test
    void testValidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        endpoint.setHasRequestBody(false);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testInvalidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        endpoint.setHasRequestBody(true);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testHeadWithRequestBodyFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("HEAD");
        endpoint.setHasRequestBody(true);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testHeadWithoutRequestBodyPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("HEAD");
        endpoint.setHasRequestBody(false);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testDeleteWithRequestBodyFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/1");
        endpoint.setMethod("DELETE");
        endpoint.setHasRequestBody(true);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }
}
