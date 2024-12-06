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
        List<String> complianceIssues = new ArrayList<>();

        // Parse the YAML content into a Map structure
        Yaml yaml = new Yaml();
        Map<String, Object> swaggerData = yaml.load(swaggerYamlContent);

        // Example compliance check: Ensure security section exists
        if (!swaggerData.containsKey("security")) {
            complianceIssues.add("Missing security section.");
        }

        // Check if all paths have required security (Authorization)
        Map<String, Object> paths = (Map<String, Object>) swaggerData.get("paths");
        if (paths != null) {
            for (Object pathObj : paths.values()) {
                Map<String, Object> path = (Map<String, Object>) pathObj;
                for (Object methodObj : path.values()) {
                    Map<String, Object> method = (Map<String, Object>) methodObj;
                    if (!method.containsKey("security")) {
                        complianceIssues.add("Missing security definition for one or more methods.");
                    }
                }
            }
        }

        // Add more checks for compliance (e.g., required fields, valid types, etc.)
        // Example: Check if required fields are defined for the paths, parameters, etc.

        return complianceIssues;
    }
}
