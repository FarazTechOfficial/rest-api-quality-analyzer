package com.research.analyzer.service;

import com.research.analyzer.exception.InvalidOpenApiException;
import com.research.analyzer.model.ApiEndpoint;
import com.research.analyzer.model.ApiParameter;
import com.research.analyzer.model.ApiResponseInfo;
import com.research.analyzer.model.ApiSpecification;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.ParseOptions;
import io.swagger.v3.parser.core.models.SwaggerParseResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OpenApiParserService {

    public ApiSpecification parse(String specificationContent) {
        ParseOptions options = new ParseOptions();
        options.setResolve(true);

        SwaggerParseResult parseResult = new OpenAPIV3Parser().readContents(specificationContent, null, options);

        if (parseResult.getMessages() != null && !parseResult.getMessages().isEmpty()) {
            OpenAPI openAPI = parseResult.getOpenAPI();
            if (openAPI == null) {
                throw new InvalidOpenApiException("Invalid OpenAPI specification: " + parseResult.getMessages().get(0));
            }
        }

        OpenAPI openAPI = parseResult.getOpenAPI();
        if (openAPI == null) {
            throw new InvalidOpenApiException("Invalid OpenAPI specification");
        }

        ApiSpecification spec = new ApiSpecification();

        if (openAPI.getInfo() != null) {
            spec.setTitle(openAPI.getInfo().getTitle());
            spec.setVersion(openAPI.getInfo().getVersion());
            spec.setDescription(openAPI.getInfo().getDescription());
        }

        if (openAPI.getServers() != null) {
            for (io.swagger.v3.oas.models.servers.Server server : openAPI.getServers()) {
                if (server.getUrl() != null) {
                    spec.getServerUrls().add(server.getUrl());
                }
            }
        }

        if (openAPI.getComponents() != null && openAPI.getComponents().getSecuritySchemes() != null) {
            for (String schemeName : openAPI.getComponents().getSecuritySchemes().keySet()) {
                spec.getSecuritySchemes().add(schemeName);
            }
        }

        if (openAPI.getPaths() == null) {
            return spec;
        }

        for (Map.Entry<String, PathItem> pathEntry : openAPI.getPaths().entrySet()) {
            String path = pathEntry.getKey();
            PathItem pathItem = pathEntry.getValue();

            addOperation(spec, path, "GET", pathItem.getGet());
            addOperation(spec, path, "POST", pathItem.getPost());
            addOperation(spec, path, "PUT", pathItem.getPut());
            addOperation(spec, path, "PATCH", pathItem.getPatch());
            addOperation(spec, path, "DELETE", pathItem.getDelete());
            addOperation(spec, path, "HEAD", pathItem.getHead());
            addOperation(spec, path, "OPTIONS", pathItem.getOptions());
        }

        return spec;
    }

    private void addOperation(ApiSpecification spec, String path, String method, Operation operation) {
        if (operation == null) {
            return;
        }

        ApiEndpoint endpoint = new ApiEndpoint();
        endpoint.setPath(path);
        endpoint.setMethod(method);
        endpoint.setOperationId(operation.getOperationId());
        endpoint.setSummary(operation.getSummary());
        endpoint.setDescription(operation.getDescription());
        endpoint.setHasRequestBody(operation.getRequestBody() != null);
        if (operation.getRequestBody() != null && operation.getRequestBody().getContent() != null) {
            endpoint.setRequestContentTypes(
                    new ArrayList<>(operation.getRequestBody().getContent().keySet()));
        }

        if (operation.getParameters() != null) {
            List<ApiParameter> parameters = new ArrayList<>();
            for (Parameter param : operation.getParameters()) {
                ApiParameter apiParam = new ApiParameter();
                apiParam.setName(param.getName());
                apiParam.setLocation(param.getIn());
                apiParam.setRequired(Boolean.TRUE.equals(param.getRequired()));
                apiParam.setDescription(param.getDescription());
                parameters.add(apiParam);
            }
            endpoint.setParameters(parameters);
        }

        if (operation.getResponses() != null) {
            List<ApiResponseInfo> responses = new ArrayList<>();
            for (Map.Entry<String, ApiResponse> entry : operation.getResponses().entrySet()) {
                ApiResponseInfo responseInfo = new ApiResponseInfo();
                responseInfo.setStatusCode(entry.getKey());
                if (entry.getValue() != null) {
                    responseInfo.setDescription(entry.getValue().getDescription());
                    if (entry.getValue().getContent() != null) {
                        responseInfo.setContentTypes(new ArrayList<>(entry.getValue().getContent().keySet()));
                    }
                }
                responses.add(responseInfo);
            }
            endpoint.setResponses(responses);
        }

        spec.getEndpoints().add(endpoint);
    }
}
