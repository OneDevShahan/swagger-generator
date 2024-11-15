package com.swagger_generator.entity;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * Entity representing a request to generate Swagger documentation for an API endpoint.
 *
 * <p>This class captures the endpoint information, HTTP method, and request/response schemas
 * necessary to dynamically generate Swagger documentation for an API. Each field corresponds
 * to details about an endpoint, which are used by the controller to build the Swagger YAML.
 */
@Data
@Getter
@Setter
public class SwaggerSchemaRequest {

    /**
     * The endpoint path for the API, e.g., "/api/v1/resource".
     *
     * <p>This field specifies the relative path of the API endpoint for which the Swagger
     * documentation is to be generated.
     */
    private String endpoint;

    /**
     * The HTTP method for the API endpoint, e.g., "GET", "POST", "PUT", or "DELETE".
     *
     * <p>This field indicates which HTTP method should be associated with the endpoint in
     * the generated Swagger documentation.
     */
    private String httpMethod;

    /**
     * The JSON schema for the request body of the API endpoint.
     *
     * <p>This field specifies the structure of the expected request payload in JSON format,
     * allowing Swagger to define the input schema for methods that require a request body.
     * It is represented as a {@link JsonNode} to accommodate flexible JSON structures.
     */
    private JsonNode requestSchema;

    /**
     * The JSON schema for the response body of the API endpoint.
     *
     * <p>This field specifies the structure of the response payload in JSON format, allowing
     * Swagger to define the output schema for the API endpoint. It is represented as a
     * {@link JsonNode} to handle dynamic JSON structures.
     */
    private JsonNode responseSchema;

    /**
     * A list of tags associated with the API endpoint, e.g., ["user", "admin"].
     *
     * <p>This field provides a way to group and categorize API endpoints within the Swagger documentation.
     */
    private List<String> tags;

    /**
     * A list of parameters required or optional for the API endpoint.
     *
     * <p>Each parameter is represented as a map containing details like name, type, and description.
     * This field defines query, path, header, or form parameters for the API.
     */
    private List<Map<String, Object>> parameters;

    /**
     * A unique identifier for the operation, e.g., "getUserDetails".
     *
     * <p>This field allows users to reference and distinguish specific operations in the Swagger documentation.
     */
    private String operationId;

    /**
     * A brief description of the API endpoint.
     *
     * <p>This field provides additional context or details about the endpoint's functionality within
     * the Swagger documentation.
     */
    private String description;
}
