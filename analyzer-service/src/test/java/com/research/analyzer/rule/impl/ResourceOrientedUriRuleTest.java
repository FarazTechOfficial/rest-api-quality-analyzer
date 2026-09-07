package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceOrientedUriRuleTest {

    private ResourceOrientedUriRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new ResourceOrientedUriRule();
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
        endpoint.setPath("/getUsers");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void verbPrefixNounsAreFalsePositives() {
        ApiEndpoint updates = new ApiEndpoint();
        updates.setPath("/updates");
        updates.setMethod("GET");
        ApiEndpoint lists = new ApiEndpoint();
        lists.setPath("/lists");
        lists.setMethod("GET");
        ApiEndpoint getty = new ApiEndpoint();
        getty.setPath("/getty-images");
        getty.setMethod("GET");

        assertFalse(rule.evaluate(updates, specification).isPassed());
        assertFalse(rule.evaluate(lists, specification).isPassed());
        assertFalse(rule.evaluate(getty, specification).isPassed());
    }

    @Test
    void pathParameterNamesAreFalsePositives() {
        ApiEndpoint getUser = new ApiEndpoint();
        getUser.setPath("/users/{getUser}");
        getUser.setMethod("GET");
        ApiEndpoint listType = new ApiEndpoint();
        listType.setPath("/jobs/{listType}");
        listType.setMethod("GET");

        assertFalse(rule.evaluate(getUser, specification).isPassed());
        assertFalse(rule.evaluate(listType, specification).isPassed());
    }

    @Test
    void unlistedRpcActionsAreNotCovered() {
        String[] paths = {"/logout", "/login", "/refresh-token"};

        for (String path : paths) {
            ApiEndpoint endpoint = new ApiEndpoint();
            endpoint.setPath(path);
            endpoint.setMethod("POST");

            assertTrue(rule.evaluate(endpoint, specification).isPassed());
        }
    }
}
