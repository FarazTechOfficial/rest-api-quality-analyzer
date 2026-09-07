package com.research.analyzer.rule.impl;

import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PluralResourceNameRuleTest {

    private PluralResourceNameRule rule;
    private ApiSpecification specification;

    @BeforeEach
    void setUp() {
        rule = new PluralResourceNameRule();
        specification = new ApiSpecification();
    }

    @Test
    void testValidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id}");
        endpoint.setMethod("GET");

        assertTrue(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void testInvalidEndpoint() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/user/{id}");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void nestedCollectionPathsPass() {
        ApiEndpoint nested = new ApiEndpoint();
        nested.setPath("/orders/{id}/items");
        nested.setMethod("GET");
        ApiEndpoint versioned = new ApiEndpoint();
        versioned.setPath("/v1/users");
        versioned.setMethod("GET");

        assertTrue(rule.evaluate(nested, specification).isPassed());
        assertTrue(rule.evaluate(versioned, specification).isPassed());
    }

    @Test
    void singularNestedCollectionFails() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/users/{id}/order");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void massNounCollectionsAreFalsePositives() {
        ApiEndpoint equipment = new ApiEndpoint();
        equipment.setPath("/equipment/{id}");
        equipment.setMethod("GET");
        ApiEndpoint inventory = new ApiEndpoint();
        inventory.setPath("/inventory");
        inventory.setMethod("GET");

        assertFalse(rule.evaluate(equipment, specification).isPassed());
        assertFalse(rule.evaluate(inventory, specification).isPassed());
    }

    @Test
    void singletonResourceIsFalsePositive() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/profile");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }

    @Test
    void uninflectedSingularNounsAcceptedAsPlural() {
        ApiEndpoint status = new ApiEndpoint();
        status.setPath("/status/{id}");
        status.setMethod("GET");
        ApiEndpoint news = new ApiEndpoint();
        news.setPath("/news");
        news.setMethod("GET");

        assertTrue(rule.evaluate(status, specification).isPassed());
        assertTrue(rule.evaluate(news, specification).isPassed());
    }

    @Test
    void uppercasePluralFailsOnCaseSensitiveCheck() {
        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath("/USERS/{id}");
        endpoint.setMethod("GET");

        assertFalse(rule.evaluate(endpoint, specification).isPassed());
    }
}
