package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PostReturns201RuleTest {

    private PostReturns201Rule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new PostReturns201Rule();
        specification = new ApiSpecification();
    }

    @Test
    void testPostReturns201() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("POST");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("201");
        endpoint.setResponses(List.of(response));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testPostReturns200() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("POST");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        endpoint.setResponses(List.of(response));

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testPostWithoutResponsesFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("POST");

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
