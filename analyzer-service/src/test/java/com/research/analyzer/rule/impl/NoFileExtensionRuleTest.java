package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoFileExtensionRuleTest {

    private NoFileExtensionRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new NoFileExtensionRule();
        specification = new ApiSpecification();
    }

    @Test
    void testValidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testInvalidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/order-items/item.json");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void nonExtensionResourcePathPasses() {
        ApiEndpoint paramPath = new ApiEndpoint();
        paramPath.setPath("/users/{id}");
        paramPath.setMethod("GET");
        ApiEndpoint numericPath = new ApiEndpoint();
        numericPath.setPath("/reports/2024");
        numericPath.setMethod("GET");

        assertTrue(rule.evaluate(paramPath, specification).isPassed());
        assertTrue(rule.evaluate(numericPath, specification).isPassed());
    }

    @Test
    void annualReportExtensionFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/reports/annual.xml");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void bracedParameterWithDotIsSkipped() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id.json}");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void uppercaseExtensionIsNotDetected() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users.JSON");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}
