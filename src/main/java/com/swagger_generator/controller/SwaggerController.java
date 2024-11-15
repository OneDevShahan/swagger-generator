package com.swagger_generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swagger_generator.entity.SwaggerSchemaRequest;
import com.swagger_generator.util.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST controller for handling requests related to dynamic Swagger documentation generation.
 * This controller includes endpoints for health checks and generating Swagger YAML for multiple
 * schema requests dynamically.
 */
@RestController
@RequestMapping("/api/swagger")
public class SwaggerController {

    // ObjectMapper instance for JSON serialization and deserialization
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Health check endpoint to verify service availability.
     *
     * <p>Returns a "healthy" status when the service is running correctly.
     *
     * @return ResponseEntity with "healthy" message and HTTP status 200 (OK)
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthStatus() {
        return new ResponseEntity<>("healthy", HttpStatus.OK);
    }

    /**
     * Endpoint for generating Swagger YAML documentation for multiple schema requests.
     *
     * <p>This method accepts a list of {@link SwaggerSchemaRequest} objects in the request body,
     * processes each schema to dynamically generate Swagger documentation with appropriate
     * paths, methods, and responses.
     *
     * <p>If the request list is empty, it returns a 400 (Bad Request) status with an error message.
     * Otherwise, it generates the Swagger YAML and returns it with a 200 (OK) status.
     *
     * @param swaggerSchemaRequests a list of schema requests containing endpoint, HTTP method,
     *                              request, and response schema details
     * @return ResponseEntity with generated Swagger YAML documentation and HTTP status
     */
    @PostMapping("/generate")
    public ResponseEntity<String> generateSwaggerMulti(@RequestBody List<SwaggerSchemaRequest> swaggerSchemaRequests) {

        // Check if request list is empty and return error response if so
        if (swaggerSchemaRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Request data is missing or null.");
        }

        // Generate and return Swagger YAML content
        String swaggerYamlContent = generateSwaggerYaml(swaggerSchemaRequests);
        return new ResponseEntity<>(swaggerYamlContent, HttpStatus.OK);
    }

    /**
     * Generates a Swagger YAML representation for multiple schema requests.
     *
     * <p>This method initializes the basic structure of a Swagger document and iterates through
     * each {@link SwaggerSchemaRequest} to dynamically build out paths, HTTP methods, request
     * bodies, and response definitions based on the schema details provided.
     *
     * <p>The resulting YAML includes OpenAPI version, info, paths, and definitions.
     * The method converts the resulting map structure into YAML format using SnakeYAML library.
     *
     * @param swaggerSchemaRequests a list of schema requests with endpoint and schema information
     * @return a String containing the generated Swagger YAML documentation
     */
    private String generateSwaggerYaml(List<SwaggerSchemaRequest> swaggerSchemaRequests) {
        // Create the root structure for the Swagger YAML
        Map<String, Object> swaggerRoot = new LinkedHashMap<>();
        swaggerRoot.put("openapi", "3.0.0");
        swaggerRoot.put("info", Map.of(
                "title", "Dynamic API",
                "version", "1.0.0",
                "description", "Dynamically generated API documentation"
        ));

        Map<String, Object> paths = new HashMap<>();
        for (SwaggerSchemaRequest schemaEnhanced : swaggerSchemaRequests) {
            // Extract endpoint and HTTP method, ensuring method names are lowercase
            String endpoint = schemaEnhanced.getEndpoint();
            String httpMethod = schemaEnhanced.getHttpMethod().toLowerCase();
            paths.putIfAbsent(endpoint, new HashMap<>());

            Map<String, Object> methodMap = new HashMap<>();
            methodMap.put("summary", schemaEnhanced.getDescription() != null ? schemaEnhanced.getDescription() : "Generated endpoint description");
            if (schemaEnhanced.getOperationId() != null) {
                methodMap.put("operationId", schemaEnhanced.getOperationId());
            }

            // Include request body schema for non-GET and non-DELETE methods
            if (!httpMethod.equals("get") && !httpMethod.equals("delete")) {
                methodMap.put("requestBody", Map.of(
                        "required", true,
                        "content", Map.of("application/json", Map.of("schema", Utility.parseSchema(schemaEnhanced.getRequestSchema())))
                ));
            }

            // Add response schema
            methodMap.put("responses", Map.of("200", Map.of(
                    "description", "Successful response",
                    "content", Map.of("application/json", Map.of("schema", Utility.parseSchema(schemaEnhanced.getResponseSchema())))
            )));

            // Add parameters, excluding 'Authorization'
            if (schemaEnhanced.getParameters() != null) {
                List<Map<String, Object>> filteredParameters = schemaEnhanced.getParameters().stream()
                        .filter(param -> !"Authorization".equals(param.get("name")))
                        .collect(Collectors.toList());
                if (!filteredParameters.isEmpty()) {
                    methodMap.put("parameters", filteredParameters);
                }
            }

            // Apply method-level security
            methodMap.put("security", List.of(Map.of("bearerAuth", List.of())));

            // Add tags
            if (schemaEnhanced.getTags() != null) {
                methodMap.put("tags", schemaEnhanced.getTags());
            }

            // Update paths with the constructed method map
            ((Map<String, Object>) paths.get(endpoint)).put(httpMethod, methodMap);
        }

        // Add paths to the root structure
        swaggerRoot.put("paths", paths);

        // Define security schemes under components
        swaggerRoot.put("components", Map.of("securitySchemes", Map.of(
                "bearerAuth", Map.of(
                        "type", "http",
                        "scheme", "bearer",
                        "bearerFormat", "JWT"
                )
        )));

        // Add global security requirement
        swaggerRoot.put("security", List.of(Map.of("bearerAuth", List.of())));

        // Convert the Swagger root map to YAML format
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(swaggerRoot);
    }
}
