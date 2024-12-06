package com.swagger_generator.util;

import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.parser.OpenAPIV3Parser;
import io.swagger.v3.parser.core.models.SwaggerParseResult;

import java.lang.reflect.Method;
import java.util.*;

/**
 * Utility class for parsing JSON schemas into Swagger-compatible formats and checking compliance.
 */
public class Utility {

    /**
     * Parses a JSON schema into a Swagger-compatible map structure.
     *
     * @param schema the JSON schema as a {@link JsonNode}.
     * @return a map representing the parsed schema.
     */
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

    /**
     * Checks the compliance of a Swagger YAML content.
     *
     * @param swaggerYamlContent the Swagger YAML content as a string.
     * @return a list of compliance issues, if any.
     */
    public static List<String> checkCompliance(String swaggerYamlContent) {
        List<String> complianceIssues = new ArrayList<>();

        SwaggerParseResult result = new OpenAPIV3Parser().readContents(swaggerYamlContent, null, null);
        if (result.getOpenAPI() == null) {
            complianceIssues.add("Failed to parse Swagger YAML.");
            if (result.getMessages() != null) complianceIssues.addAll(result.getMessages());
            return complianceIssues;
        }

        OpenAPI openAPI = result.getOpenAPI();
        if (openAPI == null) {
            complianceIssues.add("OpenAPI object is null.");
            return complianceIssues;
        }

        Paths paths = openAPI.getPaths();
        if (paths == null || paths.isEmpty()) {
            complianceIssues.add("No paths defined in the Swagger documentation.");
            return complianceIssues;
        }

        for (Map.Entry<String, PathItem> entry : paths.entrySet()) {
            String path = entry.getKey();
            PathItem pathItem = entry.getValue();

            if (pathItem != null) {
                for (String method : List.of("get", "post", "put", "delete", "patch")) {
                    try {
                        Method methodGetter = PathItem.class.getMethod(method);
                        Operation operation = (Operation) methodGetter.invoke(pathItem);
                        if (operation == null) continue;

                        if (operation.getSecurity() == null || operation.getSecurity().isEmpty()) {
                            complianceIssues.add("Missing security definition for " + method.toUpperCase() + " on path: " + path);
                        }

                        List<Parameter> parameters = operation.getParameters();
                        if (parameters != null) {
                            boolean hasAuthorizationHeader = parameters.stream()
                                    .anyMatch(param -> "Authorization".equalsIgnoreCase(param.getName()));
                            if (!hasAuthorizationHeader) {
                                complianceIssues.add("Missing Authorization header for " + method.toUpperCase() + " on path: " + path);
                            }
                        }

                    } catch (ReflectiveOperationException e) {
                        // Log reflection issues or unsupported HTTP methods
                        System.err.println("Reflection error for " + method.toUpperCase() + " method on path: " + path);
                    }
                }
            }
        }

        if (result.getMessages() != null && !result.getMessages().isEmpty()) {
            complianceIssues.addAll(result.getMessages());
        }

        return complianceIssues;
    }
}
