package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ContentTypeHeaderRuleTest {

    private ContentTypeHeaderRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new ContentTypeHeaderRule();
        specification = new ApiSpecification();
    }

    @Test
    void nonGetOperationIsNotApplicable() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("POST");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("201");
        response.setContentTypes(List.of("application/xml"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseDeclaringJsonPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/json"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseDeclaringXmlFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/xml"));
        endpoint.getResponses().add(response);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseWithNoDeclaredContentTypePasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void wildcardMediaTypeIsNotFlagged() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/incidencias/image/{id}");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("*/*"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseDeclaringTextPlainFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("text/plain"));
        endpoint.getResponses().add(response);

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseDeclaringJsonAndXmlPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/xml", "application/json"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void jsonMediaTypeWithCharsetPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiResponseInfo response = new ApiResponseInfo();
        response.setStatusCode("200");
        response.setContentTypes(List.of("application/json; charset=utf-8"));
        endpoint.getResponses().add(response);

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void getResponseWithoutDeclaredResponsesPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}