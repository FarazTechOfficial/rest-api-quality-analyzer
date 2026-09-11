package com.research.analyzer.services;

import com.research.analyzer.model.ApiSpecification;

public interface OpenApiParserService {

    ApiSpecification parse(String specificationContent);
}