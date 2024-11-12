package com.swagger_generator.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SwaggerSchemaRequest {

    private String endpoint;
    private String httpMethod;
    private JsonNode requestSchema;
    private JsonNode responseSchema;
}
