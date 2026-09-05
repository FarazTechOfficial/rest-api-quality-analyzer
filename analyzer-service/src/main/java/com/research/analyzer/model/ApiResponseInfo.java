package com.research.analyzer.model;

import java.util.ArrayList;
import java.util.List;

public class ApiResponseInfo {

    private String statusCode;
    private String description;
    private List<String> contentTypes = new ArrayList<>();

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getContentTypes() {
        return contentTypes;
    }

    public void setContentTypes(List<String> contentTypes) {
        this.contentTypes = contentTypes;
    }

    public boolean isSuccess() {
        return statusCode != null && statusCode.startsWith("2");
    }

    public boolean isClientError() {
        return statusCode != null && statusCode.startsWith("4");
    }

    public boolean isServerError() {
        return statusCode != null && statusCode.startsWith("5");
    }
}
