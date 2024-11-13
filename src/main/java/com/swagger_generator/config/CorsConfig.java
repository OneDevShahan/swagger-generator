package com.swagger_generator.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Cross-Origin Resource Sharing (CORS) settings.
 * This class customizes the CORS configuration to control cross-origin access
 * from specified origins to the backend endpoints. It implements the
 * {@link WebMvcConfigurer} interface to override the CORS mappings.
 *
 * <p>This configuration allows the frontend to access backend APIs securely,
 * ensuring that requests are limited to specified origins, methods, and headers.
 * The configuration is especially useful for Single Page Applications (SPA)
 * like React or Angular clients hosted on a different domain.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    /**
     * Configures CORS mappings for the entire application. This method defines
     * the specific origins, HTTP methods, and headers allowed when accessing
     * backend resources.
     *
     * <p><b>Allowed Origins:</b> Allows requests only from "http://localhost:3000"
     * and "https://example.com", enabling controlled access for development and
     * production environments, respectively.
     *
     * <p><b>Allowed Methods:</b> Supports "GET", "POST", "PUT", and "DELETE"
     * methods to allow CRUD operations.
     *
     * <p><b>Allowed Headers:</b> Accepts any header required by the frontend
     * client to interact with the backend.
     *
     * <p><b>Allow Credentials:</b> Enables cookies and authorization headers to
     * be sent with requests, supporting secure authentication.
     *
     * @param registry the CorsRegistry instance to apply the configuration
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Enable CORS for all endpoints
                .allowedOrigins("http://localhost:3000", "https://example.com") // Specify allowed origins
                .allowedMethods("GET", "POST", "PUT", "DELETE") // Define allowed HTTP methods
                .allowedHeaders("*") // Accept all headers
                .allowCredentials(true); // Allow credentials (cookies, authentication headers, etc.)
    }
}
