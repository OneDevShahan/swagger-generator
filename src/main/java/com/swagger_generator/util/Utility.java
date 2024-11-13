package com.swagger_generator.util;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for parsing JSON schemas into Swagger-compatible formats.
 *
 * <p>This class contains methods to transform JSON node schemas into a format
 * suitable for Swagger documentation generation, helping represent API request
 * and response bodies accurately.
 */
public class Utility {

    /**
     * Converts a JSON schema represented by a {@link JsonNode} into a Swagger-compatible
     * map structure.
     *
     * <p>This method recursively parses the JSON schema to create a Swagger-compatible
     * object structure, including nested objects and arrays.
     *
     * @param schema the {@link JsonNode} containing the JSON schema to parse
     * @return a map representing the parsed schema, compatible with Swagger's
     *         "properties" and "type" fields.
     */
    public static Map<String, Object> parseSchema(JsonNode schema) {

        // Root schema map with "type" set as "object" for Swagger compatibility
        Map<String, Object> schemaMap = new HashMap<>();
        schemaMap.put("type", "object");

        // Property definitions for fields in the schema
        Map<String, Object> properties = new HashMap<>();
        schema.fields().forEachRemaining(field -> {
            JsonNode fieldValue = field.getValue();

            // Handle nested objects recursively
            if (fieldValue.isObject()) {
                properties.put(field.getKey(), parseSchema(fieldValue));

                // Handle arrays, assuming array items are strings (can be extended for other types)
            } else if (fieldValue.isArray()) {
                properties.put(field.getKey(), Map.of(
                        "type", "array",
                        "items", Map.of("type", "string")
                ));

                // Handle primitive types: 'number' and 'string' (default)
            } else {
                properties.put(field.getKey(), Map.of(
                        "type", fieldValue.asText().equals("number") ? "number" : "string"
                ));
            }
        });

        // Add properties to the schema map
        schemaMap.put("properties", properties);
        return schemaMap;
    }
}
