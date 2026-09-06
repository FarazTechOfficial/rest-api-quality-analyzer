package com.research.analyzer.rule.impl;

import com.research.analyzer.dto.RuleResultDto;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonRepresentationRuleTest {

    private JsonRepresentationRule rule;
    private ApiSpecification specification;
    private ApiEndpoint endpoint;

    @BeforeEach
    void setUp() {
        rule = new JsonRepresentationRule();
        specification = new ApiSpecification();
        endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        specification.getEndpoints().add(endpoint);
    }

    @Test
    void failsWhenNoJsonMediaTypeIsDocumented() {
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/xml"));
        endpoint.getResponses().add(response);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void passesWhenAResponseDeclaresJson() {
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/json"));
        endpoint.getResponses().add(response);

        RuleResultDto result = rule.evaluate(endpoint, specification);
        assertTrue(result.isPassed());
    }

    @Test
    void passesWhenARequestBodyDeclaresJson() {
        endpoint.setRequestContentTypes(List.of("application/json"));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void vendorJsonSuffixCountsAsJson() {
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/hal+json"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void wildcardMediaTypeIsNotJson() {
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("*/*"));
        endpoint.getResponses().add(response);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }
}