package com.swagger_generator.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

public class Utility {

    public static Map<String, Object> parseSchema(JsonNode schema) {

        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object");

        Map<String, Object> properties = new HashMap<>();
        schema.fields().forEachRemaining(field -> {
            JsonNode fieldValue = field.getValue();
            if (fieldValue.isObject()) {
                properties.put(field.getKey(), parseSchema(fieldValue));
            } else if (fieldValue.isArray()) {
                properties.put(field.getKey(), Map.of(
                        "type", "array",
                        "items", Map.of("type", "string")
                ));
            } else {
                properties.put(field.getKey(), Map.of("type", fieldValue.asText().equals("number") ? "number" : "string"));
            }
        });
        schemaMap.put("properties", properties);
        return schemaMap;
    }
}
