package com.swagger_generator.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.*;

import org.yaml.snakeyaml.Yaml;

public class Utility {

    public static Map<String, Object> parseSchema(JsonNode schema) {
        if (schema == null) return Collections.emptyMap();

        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        schema.fields().forEachRemaining(field -> {
            JsonNode fieldValue = field.getValue();
            if (fieldValue.isObject()) {
                properties.put(field.getKey(), parseSchema(fieldValue));
            } else if (fieldValue.isArray()) {
                JsonNode arrayElement = fieldValue.elements().hasNext() ? fieldValue.elements().next() : null;
                properties.put(field.getKey(), Map.of(
                        "type", "array",
                        "items", parseSchema(arrayElement)
                ));
            } else {
                String fieldType = fieldValue.asText();
                properties.put(field.getKey(), Map.of(
                        "type", "number".equalsIgnoreCase(fieldType) ? "number" : "string"
                ));
            }
        });

        schemaMap.put("properties", properties);
        return schemaMap;
    }

    // New method for response schema parsing to handle updated structure
    public static Map<String, Object> parseResponseSchema(JsonNode schema) {
        if (schema == null) return Collections.emptyMap();

        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        schema.fields().forEachRemaining(field -> {
            JsonNode fieldValue = field.getValue();
            if (fieldValue.isObject()) {
                properties.put(field.getKey(), parseResponseSchema(fieldValue));
            } else if (fieldValue.isArray()) {
                JsonNode arrayElement = fieldValue.elements().hasNext() ? fieldValue.elements().next() : null;
                properties.put(field.getKey(), Map.of(
                        "type", "array",
                        "items", parseResponseSchema(arrayElement)
                ));
            } else {
                String fieldType = fieldValue.isNumber() ? "number" : "string";
                if (field.getKey().equals("amount")) {
                    fieldType = "number";  // Ensure 'amount' is a number
                }
                properties.put(field.getKey(), Map.of(
                        "type", fieldType
                ));
            }
        });

        schemaMap.put("properties", properties);
        return schemaMap;
    }

    public static List<String> checkCompliance(String swaggerYamlContent) {
        List<String> issues = new ArrayList<>();

        try {
            // Parse YAML into a structured object
            Yaml yaml = new Yaml();
            Map<String, Object> parsedYaml = yaml.load(swaggerYamlContent);

            // Check for security definitions
            Map<String, Object> components = (Map<String, Object>) parsedYaml.get("components");
            if (components == null || !components.containsKey("securitySchemes")) {
                issues.add("Security schemes are missing from components.");
            }

            // Check if paths have `security` definitions
            Map<String, Object> paths = (Map<String, Object>) parsedYaml.get("paths");
            if (paths != null) {
                for (Map.Entry<String, Object> pathEntry : paths.entrySet()) {
                    String endpoint = pathEntry.getKey();
                    Map<String, Object> methods = (Map<String, Object>) pathEntry.getValue();

                    for (Map.Entry<String, Object> methodEntry : methods.entrySet()) {
                        String method = methodEntry.getKey();
                        Map<String, Object> methodDetails = (Map<String, Object>) methodEntry.getValue();

                        // Check for security definitions
                        if (!methodDetails.containsKey("security")) {
                            issues.add("Missing security definitions for " + method.toUpperCase() + " " + endpoint);
                        }

                        // Check if Authorization is in the correct place
                        List<Map<String, Object>> parameters = (List<Map<String, Object>>) methodDetails.get("parameters");
                        if (parameters != null) {
                            for (Map<String, Object> param : parameters) {
                                if ("Authorization".equals(param.get("name")) && !"header".equals(param.get("in"))) {
                                    issues.add("Authorization parameter should be in the header for " + method.toUpperCase() + " " + endpoint);
                                }
                            }
                        }

                        // Check for response schemas
                        Map<String, Object> responses = (Map<String, Object>) methodDetails.get("responses");
                        if (responses == null || !responses.containsKey("400")) {
                            issues.add("Error response (e.g., 400) is missing for " + method.toUpperCase() + " " + endpoint);
                        }
                    }
                }
            }
        } catch (Exception e) {
            issues.add("Error parsing or analyzing the Swagger YAML content: " + e.getMessage());
        }

        return issues.isEmpty() ? null : issues;
    }

}
