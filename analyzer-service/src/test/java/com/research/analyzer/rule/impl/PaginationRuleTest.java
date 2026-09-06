package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiParameter;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PaginationRuleTest {

    private PaginationRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new PaginationRule();
        specification = new ApiSpecification();
    }

    @Test
    void nonGetOperationIsNotApplicable() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("POST");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void itemEndpointIsNotApplicable() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{userId}");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void singleResourcePathIsNotApplicable() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/profile");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void collectionGetWithoutPaginationParametersFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void collectionGetWithPaginationParametersPasses() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users");
        endpoint.setMethod("GET");
        ApiParameter limit = new ApiParameter();
        limit.setName("limit");
        limit.setLocation("query");
        ApiParameter offset = new ApiParameter();
        offset.setName("offset");
        offset.setLocation("query");
        endpoint.setParameters(Arrays.asList(limit, offset));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void pageAndPageSizeCountAsPagination() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/incidencias/allIncidencias");
        endpoint.setMethod("GET");
        ApiParameter pageNo = new ApiParameter();
        pageNo.setName("pageNo");
        pageNo.setLocation("query");
        ApiParameter pageSize = new ApiParameter();
        pageSize.setName("pageSize");
        pageSize.setLocation("query");
        endpoint.setParameters(Arrays.asList(pageNo, pageSize));

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }
}