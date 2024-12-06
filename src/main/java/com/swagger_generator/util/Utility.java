package com.swagger_generator.util;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.*;

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
        // Parse YAML with Swagger Parser
        //...

        // Check if all paths have required security (Authorization)
        //...

        // Check security compliance with the updated structure
        //...

        // Return the list of compliance issues
        return Collections.emptyList();  // Placeholder for actual implementation
    }
}
