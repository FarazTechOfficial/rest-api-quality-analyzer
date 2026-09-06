package com.research.analyzer.model;

import java.util.ArrayList;
import java.util.List;

public class ApiEndpoint {

    private String path;
    private String method;
    private String operationId;
    private String summary;
    private String description;
    private boolean hasRequestBody;
    private List<String> requestContentTypes = new ArrayList<>();
    private List<ApiParameter> parameters = new ArrayList<>();
    private List<ApiResponseInfo> responses = new ArrayList<>();

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getOperationId() {
        return operationId;
    }

    public void setOperationId(String operationId) {
        this.operationId = operationId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isHasRequestBody() {
        return hasRequestBody;
    }

    public void setHasRequestBody(boolean hasRequestBody) {
        this.hasRequestBody = hasRequestBody;
    }

    public List<String> getRequestContentTypes() {
        return requestContentTypes;
    }

    public void setRequestContentTypes(List<String> requestContentTypes) {
        this.requestContentTypes = requestContentTypes;
    }

    public List<ApiParameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<ApiParameter> parameters) {
        this.parameters = parameters;
    }

    public List<ApiResponseInfo> getResponses() {
        return responses;
    }

    public void setResponses(List<ApiResponseInfo> responses) {
        this.responses = responses;
    }
}
