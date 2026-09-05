package com.research.analyzer.model;

import java.util.ArrayList;
import java.util.List;

public class ApiSpecification {

    private String title;
    private String version;
    private String description;
    private List<String> serverUrls = new ArrayList<>();
    private List<String> securitySchemes = new ArrayList<>();
    private List<ApiEndpoint> endpoints = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getServerUrls() {
        return serverUrls;
    }

    public void setServerUrls(List<String> serverUrls) {
        this.serverUrls = serverUrls;
    }

    public List<String> getSecuritySchemes() {
        return securitySchemes;
    }

    public void setSecuritySchemes(List<String> securitySchemes) {
        this.securitySchemes = securitySchemes;
    }

    public List<ApiEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<ApiEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
