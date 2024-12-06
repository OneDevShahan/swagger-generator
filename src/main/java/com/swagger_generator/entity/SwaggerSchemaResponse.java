package com.swagger_generator.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class SwaggerSchemaResponse {

    private String yamlContent;
    private List<String> complianceIssues;
}
