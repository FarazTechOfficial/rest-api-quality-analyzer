package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeleteReturns204RuleTest {

    private DeleteReturns204Rule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new DeleteReturns204Rule();
        specification = new ApiSpecification();
    }

    @Test
    void testDeleteReturns204() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/1");
        endpoint.setMethod("DELETE");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("204");
        endpoint.setResponses(List.of(response));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testDeleteReturns200() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/1");
        endpoint.setMethod("DELETE");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        endpoint.setResponses(List.of(response));

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testGetIgnored() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}
