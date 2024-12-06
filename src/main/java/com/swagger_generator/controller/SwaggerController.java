package com.swagger_generator.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swagger_generator.entity.SwaggerSchemaRequest;
import com.swagger_generator.entity.SwaggerSchemaResponse;
import com.swagger_generator.util.Utility;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/swagger")
public class SwaggerController {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/health")
    public ResponseEntity<String> healthStatus() {
        return new ResponseEntity<>("healthy", HttpStatus.OK);
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateSwaggerMulti(@RequestBody List<SwaggerSchemaRequest> swaggerSchemaRequests) {
        if (swaggerSchemaRequests == null || swaggerSchemaRequests.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Request data is missing or null.");
        }
        String swaggerYamlContent = generateSwaggerYaml(swaggerSchemaRequests);
        return ResponseEntity.ok(swaggerYamlContent);
    }

    // Modify the endpoint to accept a YAML input
    @PostMapping("/generate-with-compliance")
    public ResponseEntity<SwaggerSchemaResponse> generateSwaggerWithCompliance(@RequestBody String swaggerYamlContent) {
        if (swaggerYamlContent == null || swaggerYamlContent.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        // Call the utility method to check compliance
        List<String> complianceIssues = Utility.checkCompliance(swaggerYamlContent);

        // Return the Swagger content along with compliance issues
        SwaggerSchemaResponse response = new SwaggerSchemaResponse(
                swaggerYamlContent,
                complianceIssues.isEmpty() ? null : complianceIssues
        );
        return ResponseEntity.ok(response);
    }

    private String generateSwaggerYaml(List<SwaggerSchemaRequest> swaggerSchemaRequests) {
        Map<String, Object> swaggerRoot = new LinkedHashMap<>();
        swaggerRoot.put("openapi", "3.0.0");
        swaggerRoot.put("info", Map.of(
                "title", "Dynamic API",
                "version", "1.0.0",
                "description", "Dynamically generated API documentation"
        ));

        Map<String, Object> paths = new LinkedHashMap<>();
        for (SwaggerSchemaRequest schemaRequest : swaggerSchemaRequests) {
            String endpoint = schemaRequest.getEndpoint();
            String httpMethod = schemaRequest.getHttpMethod().toLowerCase();
            paths.putIfAbsent(endpoint, new LinkedHashMap<>());

            Map<String, Object> methodMap = new LinkedHashMap<>();
            methodMap.put("summary", Optional.ofNullable(schemaRequest.getDescription()).orElse("Generated endpoint description"));
            methodMap.put("operationId", schemaRequest.getOperationId());

            if (!httpMethod.equals("get") && !httpMethod.equals("delete")) {
                methodMap.put("requestBody", Map.of(
                        "required", true,
                        "content", Map.of(
                                "application/json", Map.of(
                                        "schema", Utility.parseSchema(schemaRequest.getRequestSchema())
                                )
                        )
                ));
            }

            methodMap.put("responses", Map.of(
                    "200", Map.of(
                            "description", "Successful response",
                            "content", Map.of(
                                    "application/json", Map.of(
                                            "schema", Utility.parseResponseSchema(schemaRequest.getResponseSchema())
                                    )
                            )
                    )
            ));

            if (schemaRequest.getParameters() != null) {
                List<Map<String, Object>> parameters = schemaRequest.getParameters().stream()
                        .filter(param -> !"Authorization".equals(param.get("name")))
                        .collect(Collectors.toList());
                if (!parameters.isEmpty()) {
                    methodMap.put("parameters", parameters);
                }
            }

            methodMap.put("security", List.of(Map.of("bearerAuth", List.of())));

            if (schemaRequest.getTags() != null) {
                methodMap.put("tags", schemaRequest.getTags());
            }

            ((Map<String, Object>) paths.get(endpoint)).put(httpMethod, methodMap);
        }

        swaggerRoot.put("paths", paths);
        swaggerRoot.put("components", Map.of(
                "securitySchemes", Map.of(
                        "bearerAuth", Map.of(
                                "type", "http",
                                "scheme", "bearer",
                                "bearerFormat", "JWT"
                        )
                )
        ));
        swaggerRoot.put("security", List.of(Map.of("bearerAuth", List.of())));

        DumperOptions dumperOptions = new DumperOptions();
        dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        LoaderOptions loaderOptions = new LoaderOptions();
        Yaml yaml = new Yaml(dumperOptions);
        return yaml.dump(swaggerRoot);
    }
}
