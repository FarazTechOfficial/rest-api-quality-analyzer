package com.research.dataset;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Parses every spec under specs/ with the same call the Analyzer Service makes
 * (OpenApiParserService.parse: OpenAPIV3Parser, resolve=true, invalid if the
 * resulting OpenAPI model is null) and writes tool/validation.json.
 *
 * Usage:  ValidateSpecs <specsRootDir> <outputJsonPath>
 *
 * Endpoint counting mirrors OpenApiParserService: only the seven methods it
 * inspects (GET, POST, PUT, PATCH, DELETE, HEAD, OPTIONS) are counted.
 */
public final class ValidateSpecs {

    private static final String[] METHODS =
            {"get", "post", "put", "patch", "delete", "head", "options"};

    public static void main(String[] args) throws IOException {
        Path specsRoot = Paths.get(args[0]);
        Path outJson = Paths.get(args[1]);
        List<Path> dirs;
        try (Stream<Path> stream = Files.list(specsRoot)) {
            dirs = new ArrayList<>(stream.filter(Files::isDirectory).toList());
        }
        dirs.sort(Comparator.comparing(p -> p.getFileName().toString()));

        StringBuilder json = new StringBuilder();
        json.append("{\n");
        json.append("  \"probe\": \"io.swagger.parser.v3:swagger-parser-v3:2.1.22 "
                + "OpenAPIV3Parser resolve=true (mirrors OpenApiParserService)\",\n");
        json.append("  \"generatedAt\": \"").append(isoNow()).append("\",\n");
        json.append("  \"results\": [");

        boolean first = true;
        for (Path dir : dirs) {
            String apiId = dir.getFileName().toString();
            Path specFile = dir.resolve("openapi.json");
            ValidateSpecs.Result r = new ValidateSpecs.Result(apiId);
            if (Files.exists(specFile)) {
                String content;
                try {
                    content = Files.readString(specFile, StandardCharsets.UTF_8);
                } catch (IOException exc) {
                    r.ok = false;
                    r.messages.add("cannot read spec: " + exc.getMessage());
                    continue;
                }
                r = validate(apiId, content);
            } else {
                r.ok = false;
                r.messages.add("no openapi.json in directory");
            }
            if (!first) {
                json.append(",");
            }
            first = false;
            json.append(r.toJson());
        }

        json.append("\n  ]\n}\n");

        Files.writeString(outJson, json.toString(), StandardCharsets.UTF_8);
        System.out.println("validated " + dirs.size() + " spec(s) -> " + outJson);
    }

    private static ValidateSpecs.Result validate(String apiId, String content) {
        ValidateSpecs.Result r = new ValidateSpecs.Result(apiId);
        ParseOptions options = new ParseOptions();
        options.setResolve(true);

        SwaggerParseResult parseResult =
                new OpenAPIV3Parser().readContents(content, null, options);
        OpenAPI openAPI = parseResult.getOpenAPI();

        if (openAPI == null) {
            r.ok = false;
            if (parseResult.getMessages() != null) {
                r.messages.addAll(parseResult.getMessages());
            }
            if (r.messages.isEmpty()) {
                r.messages.add("parser returned a null OpenAPI model");
            }
            return r;
        }

        r.ok = true;
        if (parseResult.getMessages() != null) {
            r.messages.addAll(parseResult.getMessages()); // non-fatal
        }
        r.title = openAPI.getInfo() != null ? openAPI.getInfo().getTitle() : "";
        r.openapi = openAPI.getOpenapi();
        r.endpointCount = countEndpoints(openAPI);
        return r;
    }

    private static int countEndpoints(OpenAPI openAPI) {
        if (openAPI.getPaths() == null) {
            return 0;
        }
        int total = 0;
        for (Map.Entry<String, PathItem> e : openAPI.getPaths().entrySet()) {
            PathItem item = e.getValue();
            if (item == null) {
                continue;
            }
            for (String method : METHODS) {
                Operation op = operation(item, method);
                if (op != null) {
                    total++;
                }
            }
        }
        return total;
    }

    private static Operation operation(PathItem item, String method) {
        return switch (method) {
            case "get" -> item.getGet();
            case "post" -> item.getPost();
            case "put" -> item.getPut();
            case "patch" -> item.getPatch();
            case "delete" -> item.getDelete();
            case "head" -> item.getHead();
            case "options" -> item.getOptions();
            default -> null;
        };
    }

    private static String isoNow() {
        return Instant.now().toString();
    }

    private static final class Result {
        final String apiId;
        boolean ok;
        final List<String> messages = new ArrayList<>();
        String title = "";
        String openapi = "";
        int endpointCount;

        Result(String apiId) {
            this.apiId = apiId;
        }

        String toJson() {
            StringBuilder sb = new StringBuilder();
            sb.append("\n    {\"api_id\": \"").append(jsonEscape(apiId)).append("\"");
            sb.append(", \"ok\": ").append(ok);
            sb.append(", \"title\": \"").append(jsonEscape(title)).append("\"");
            sb.append(", \"openapi\": \"").append(jsonEscape(openapi)).append("\"");
            sb.append(", \"endpoint_count\": ").append(endpointCount);
            sb.append(", \"messages\": [");
            for (int i = 0; i < messages.size(); i++) {
                if (i > 0) {
                    sb.append(", ");
                }
                sb.append("\"").append(jsonEscape(messages.get(i))).append("\"");
            }
            sb.append("]}");
            return sb.toString();
        }
    }

    private static String jsonEscape(String s) {
        StringBuilder sb = new StringBuilder(s.length() + 8);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"' -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
                }
            }
        }
        return sb.toString();
    }
}