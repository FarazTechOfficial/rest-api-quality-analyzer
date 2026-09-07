package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Get404ForNotFoundRuleTest {

    private Get404ForNotFoundRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new Get404ForNotFoundRule();
        specification = new ApiSpecification();
    }

    @Test
    void testGetWith404() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("404");
        endpoint.setResponses(List.of(response));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testGetWithout404() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        endpoint.setResponses(List.of(response));

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void nestedItemGetWith404Passes() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id}/posts/{postId}");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("404");
        endpoint.setResponses(List.of(response));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void concreteIdItemPathIsTreatedAsCollection() {
        ApiEndpoint me = new ApiEndpoint();
        me.setPath("/users/me");
        me.setMethod("GET");
        ApiEndpoint byId = new ApiEndpoint();
        byId.setPath("/v2/users/12345");
        byId.setMethod("GET");

        assertTrue(rule.evaluate(me, specification).isPassed());
        assertTrue(rule.evaluate(byId, specification).isPassed());
    }

    @Test
    void testCollectionGetIgnored() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}
