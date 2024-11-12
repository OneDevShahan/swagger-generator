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
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/swagger")
@CrossOrigin("http://localhost:3000")
public class SwaggerController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/health/status")
    public ResponseEntity<String> healthStatus() {
        return new ResponseEntity<>("healthy", HttpStatus.OK);
    }

    @PostMapping("/generate/multi")
    public ResponseEntity<String> generateSwaggerMulti(@RequestBody List<SwaggerSchemaRequest> swaggerSchemaRequests) {

        // Log the incoming request for debugging
        System.out.println("Received Swagger Schema Requests: " + swaggerSchemaRequests);

        if (swaggerSchemaRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Request data is missing or null.");
        }
        String swaggerYamlContent = generateMultiSwaggerYaml(swaggerSchemaRequests);
        return new ResponseEntity<>(swaggerYamlContent, HttpStatus.OK);
    }

    private String generateMultiSwaggerYaml(List<SwaggerSchemaRequest> swaggerSchemaRequests) {
        // Initialize the root structure for Swagger
        Map<String, Object> swaggerRoot = new HashMap<>();
        swaggerRoot.put("openapi", "3.0.0");
        swaggerRoot.put("info", Map.of(
                "title", "Dynamic API",
                "version", "1.0.0",
                "description", "Dynamically generated API documentation"
        ));

        Map<String, Object> paths = new HashMap<>();
        for (SwaggerSchemaRequest schemaRequest : swaggerSchemaRequests) {
            String endpoint = schemaRequest.getEndpoint();
            String httpMethod = schemaRequest.getHttpMethod().toLowerCase();

            paths.putIfAbsent(endpoint, new HashMap<>());

            Map<String, Object> methodMap = new HashMap<>();
            methodMap.put("summary", "Dynamically generated endpoint");
            methodMap.put("description", "Generated endpoint description");

            // Only include the requestBody for methods that allow it
            if (!httpMethod.equals("get") && !httpMethod.equals("delete")) {
                methodMap.put("requestBody", Map.of(
                        "required", true,
                        "content", Map.of("application/json", Map.of("schema", Utility.parseSchema(schemaRequest.getRequestSchema())))
                ));
            }

            methodMap.put("responses", Map.of("200", Map.of(
                    "description", "Successful response",
                    "content", Map.of("application/json", Map.of("schema", Utility.parseSchema(schemaRequest.getResponseSchema())))
            )));

            ((Map<String, Object>) paths.get(endpoint)).put(httpMethod, methodMap);
            swaggerRoot.put("paths", paths);
        }

        // Convert to YAML
        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setPrettyFlow(true);
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(swaggerRoot);
    }
}
