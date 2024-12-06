package com.swagger_generator.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SecurityComplianceChecker {

    // Define a list to hold compliance issues
    private List<String> complianceIssues = new ArrayList<>();

    /**
     * This method checks the generated Swagger YAML against security standards.
     *
     * @param swaggerRoot the root map structure of Swagger documentation.
     * @return List of security compliance issues, empty if compliant.
     */
    public List<String> checkSecurityCompliance(Map<String, Object> swaggerRoot) {
        // Check if all paths have required security (Authorization)
        Map<String, Object> paths = (Map<String, Object>) swaggerRoot.get("paths");
        if (paths != null) {
            paths.forEach((endpoint, methods) -> {
                Map<String, Object> methodMap = (Map<String, Object>) methods;
                methodMap.forEach((httpMethod, details) -> {
                    if (details instanceof Map) {
                        Map<String, Object> detailsMap = (Map<String, Object>) details;
                        if (!detailsMap.containsKey("security")) {
                            complianceIssues.add("Missing 'security' definition for " + httpMethod + " on endpoint " + endpoint);
                        }
                    }
                });
            });
        }

        // Check for HTTPs (optional, depends on your requirements)
        if (swaggerRoot.containsKey("schemes") && !((List<String>) swaggerRoot.get("schemes")).contains("https")) {
            complianceIssues.add("API should support HTTPS (missing 'https' in schemes).");
        }

        // Example: If CORS headers are missing in responses (for cross-origin requests)
        paths.forEach((endpoint, methods) -> {
            Map<String, Object> methodMap = (Map<String, Object>) methods;
            methodMap.forEach((httpMethod, details) -> {
                if (details instanceof Map) {
                    Map<String, Object> detailsMap = (Map<String, Object>) details;
                    if (detailsMap.containsKey("responses")) {
                        Map<String, Object> responses = (Map<String, Object>) detailsMap.get("responses");
                        if (!responses.containsKey("Access-Control-Allow-Origin")) {
                            complianceIssues.add("CORS headers missing for " + httpMethod + " on endpoint " + endpoint);
                        }
                    }
                }
            });
        });
        return complianceIssues;
    }
}
