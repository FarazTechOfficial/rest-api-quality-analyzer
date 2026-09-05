package com.research.analyzer.service;

import com.research.analyzer.model.ApiSpecification;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class OpenApiParserServiceTest {

    @Autowired
    private OpenApiParserService parserService;

    @Test
    void testParseGoodApi() throws IOException {
        ClassPathResource resource = new ClassPathResource("good-api.json");
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        ApiSpecification spec = parserService.parse(content);

        assertFalse(spec.getEndpoints().isEmpty());
        assertTrue(spec.getEndpoints().size() >= 2);
    }

    @Test
    void testParseInvalidSpec() {
        try {
            parserService.parse("not valid openapi");
            assertFalse(true, "Should have thrown exception");
        } catch (Exception ex) {
            assertTrue(ex.getMessage().contains("Invalid OpenAPI"));
        }
    }
}
