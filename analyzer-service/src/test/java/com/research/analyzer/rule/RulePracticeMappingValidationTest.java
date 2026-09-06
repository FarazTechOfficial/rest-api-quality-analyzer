package com.research.analyzer.rule;

import com.research.analyzer.rule.impl.ApiVersionPresentRule;
import com.research.analyzer.rule.impl.ContentTypeHeaderRule;
import com.research.analyzer.rule.impl.DeleteReturns204Rule;
import com.research.analyzer.rule.impl.ErrorResponseDefinedRule;
import com.research.analyzer.rule.impl.Get404ForNotFoundRule;
import com.research.analyzer.rule.impl.HttpMethodSemanticsRule;
import com.research.analyzer.rule.impl.HttpsServerRule;
import com.research.analyzer.rule.impl.JsonRepresentationRule;
import com.research.analyzer.rule.impl.LowercasePathRule;
import com.research.analyzer.rule.impl.NoFileExtensionRule;
import com.research.analyzer.rule.impl.NoTrailingSlashRule;
import com.research.analyzer.rule.impl.NoUnderscoreInUriRule;
import com.research.analyzer.rule.impl.OperationIdPresentRule;
import com.research.analyzer.rule.impl.PaginationRule;
import com.research.analyzer.rule.impl.PathParameterInUriRule;
import com.research.analyzer.rule.impl.PluralResourceNameRule;
import com.research.analyzer.rule.impl.PostReturns201Rule;
import com.research.analyzer.rule.impl.ResourceOrientedUriRule;
import com.research.analyzer.rule.impl.SecurityDefinedRule;
import com.research.analyzer.rule.impl.SuccessResponseDefinedRule;
import com.research.analyzer.rule.impl.VersionInUriRule;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Research-validation test: locks the exact rule-id -> practice-id mapping to
 * the authoritative paper catalog (paper-notes/paper-catalog.md).
 *
 * <p>Practice ids that start with "OPENAPI-" have NO counterpart in the
 * paper's 73-practice catalog; they are OpenAPI-spec-specific checks of this
 * implementation. Practice ids starting with U-/RM-/E-/H-/O- come from the
 * paper's own tables (paper-catalog.md).
 */
class RulePracticeMappingValidationTest {

    private static final Map<String, String> EXPECTED_MAPPING = new LinkedHashMap<>();

    static {
        EXPECTED_MAPPING.put("REST-001", "U-12");          // URI: CRUD function names not in URIs
        EXPECTED_MAPPING.put("REST-002", "U-9");           // URI: plural noun for collection names
        EXPECTED_MAPPING.put("REST-003", "RM-2");          // Request methods (adapted): GET must not have a body
        EXPECTED_MAPPING.put("REST-004", "U-5");           // URI: lowercase letters
        EXPECTED_MAPPING.put("REST-005", "OPENAPI-01");    // spec-specific: operationId present
        EXPECTED_MAPPING.put("REST-006", "OPENAPI-04");    // spec-specific: at least one error response documented
        EXPECTED_MAPPING.put("REST-007", "OPENAPI-05");    // spec-specific: at least one success response documented
        EXPECTED_MAPPING.put("REST-008", "U-2");           // URI: no trailing slash
        EXPECTED_MAPPING.put("REST-009", "OPENAPI-06");    // spec-specific: info.version present
        EXPECTED_MAPPING.put("REST-010", "OPENAPI-02");    // spec-specific: path parameters declared in URI
        EXPECTED_MAPPING.put("REST-011", "U-6");           // URI: no file extension
        EXPECTED_MAPPING.put("REST-012", "U-4");           // URI: no underscore
        EXPECTED_MAPPING.put("REST-013", "E-3");           // Error: 201 for successful creation
        EXPECTED_MAPPING.put("REST-014", "E-5");           // Error (adapted): 204 when body intentionally empty
        EXPECTED_MAPPING.put("REST-015", "E-11");          // Error (adapted): 404 for not found
        EXPECTED_MAPPING.put("REST-016", "O-14");          // Other: SSL should be used (https server url)
        EXPECTED_MAPPING.put("REST-017", "U-7");           // URI: no version info in the URI path
        EXPECTED_MAPPING.put("REST-018", "O-10");          // Other (adapted): OAuth/security scheme defined
        EXPECTED_MAPPING.put("REST-019", "O-8");           // Other: JSON-based representations
        EXPECTED_MAPPING.put("REST-020", "O-13");          // Other (adapted): pagination params on collection GET
        EXPECTED_MAPPING.put("REST-021", "H-2");           // HTTP headers: JSON Content-Type on GET responses
    }

    @Test
    void allRulesAreRegisteredWithTheirRuleIds() {
        List<RestApiRule> rules = buildRules();
        assertEquals(EXPECTED_MAPPING.keySet().size(), rules.size(),
                "The rule->practice mapping test must cover every implemented rule.");
        for (RestApiRule rule : rules) {
            assertTrue(EXPECTED_MAPPING.containsKey(rule.getRuleId()),
                    "Unexpected rule id in implementation: " + rule.getRuleId());
        }
    }

    @Test
    void everyRuleReportsThePracticeIdExpectedByThePaperCatalog() {
        for (RestApiRule rule : buildRules()) {
            assertEquals(EXPECTED_MAPPING.get(rule.getRuleId()), rule.getPracticeId(),
                    "Practice id drift detected for " + rule.getRuleId()
                            + " (rule name: " + rule.getRuleName() + "). "
                            + "Expected the mapping agreed in paper-notes/paper-catalog.md.");
        }
    }

    @Test
    void practiceIdReferencesAreUniqueAcrossRules() {
        Map<String, String> practiceToRule = new LinkedHashMap<>();
        for (RestApiRule rule : buildRules()) {
            String previous = practiceToRule.putIfAbsent(rule.getPracticeId(), rule.getRuleId());
            assertEquals(null, previous,
                    "Two rules claim the same practice id: " + rule.getPracticeId()
                            + " (" + previous + " vs " + rule.getRuleId() + ")");
        }
    }

    @Test
    void noRuleStillReferencesTheReconstructedCatalogIds() {
        for (RestApiRule rule : buildRules()) {
            assertFalse(rule.getPracticeId().startsWith("P-"),
                    "Rule " + rule.getRuleId() + " still reports reconstructed-catalog practice id "
                            + rule.getPracticeId() + " instead of a paper/OPENAPI id.");
        }
    }

    @Test
    void paperMappedRulesUseOnlyKnowledgeFromThePaperCatalog() {
        for (RestApiRule rule : buildRules()) {
            String id = rule.getPracticeId();
            if (id.startsWith("OPENAPI-")) {
                continue;
            }
            assertTrue(id.matches("(U|RM|E|H|O)-\\d+"),
                    "Non-OPENAPI practice id must come from the paper tables (U-/RM-/E-/H-/O-): " + id);
        }
    }

    private List<RestApiRule> buildRules() {
        return List.of(
                new ApiVersionPresentRule(),
                new ContentTypeHeaderRule(),
                new DeleteReturns204Rule(),
                new ErrorResponseDefinedRule(),
                new Get404ForNotFoundRule(),
                new HttpMethodSemanticsRule(),
                new HttpsServerRule(),
                new JsonRepresentationRule(),
                new LowercasePathRule(),
                new NoFileExtensionRule(),
                new NoTrailingSlashRule(),
                new NoUnderscoreInUriRule(),
                new OperationIdPresentRule(),
                new PaginationRule(),
                new PathParameterInUriRule(),
                new PluralResourceNameRule(),
                new PostReturns201Rule(),
                new ResourceOrientedUriRule(),
                new SecurityDefinedRule(),
                new SuccessResponseDefinedRule(),
                new VersionInUriRule()
        );
    }
}