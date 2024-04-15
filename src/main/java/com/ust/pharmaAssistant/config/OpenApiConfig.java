package com.ust.pharmaAssistant.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springdoc.core.GroupedOpenApi;
import org.springdoc.core.customizers.OpenApiCustomiser;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up OpenAPI documentation customization for PharmaAssistant API.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Configures a grouped OpenAPI for PharmaAssistant API with paths matching "/api/**".
     *
     * @return GroupedOpenApi instance representing the configured API group.
     */
    @Bean
    public GroupedOpenApi customOpenAPI() {
        return GroupedOpenApi.builder()
                .group("PharmaAssistant API")
                .pathsToMatch("/api/**")
                .build();
    }

    /**
     * Customizes error responses and adds them to the OpenAPI specification.
     *
     * @return OpenApiCustomiser instance for customizing the OpenAPI specification.
     */
    @Bean
    public OpenApiCustomiser customizeErrorStatus() {
        return openApi -> {
            Components components = openApi.getComponents();

            // Define custom error responses for different error codes
            ApiResponse error500Response = new ApiResponse()
                    .description("General system Error");
            ApiResponse error510Response = new ApiResponse()
                    .description("Medicine code is required");
            ApiResponse error511Response = new ApiResponse()
                    .description("Batch Code already exists");
            ApiResponse error512Response = new ApiResponse()
                    .description("Batch Weight should be greater than 100");
            ApiResponse error513Response = new ApiResponse()
                    .description("Batch format wrong. It should be in the format 'BTC-1234'");

            // Add the custom responses to the Components section
            components.addResponses("500", error500Response);
            components.addResponses("510", error510Response);
            components.addResponses("511", error511Response);
            components.addResponses("512", error512Response);
            components.addResponses("513", error513Response);

            // Update all paths to include the custom responses for respective error codes
            openApi.getPaths().forEach((path, pathItem) ->
                    pathItem.readOperations().forEach(operation -> {
                        updateResponse(operation, "500", error500Response);
                        updateResponse(operation, "510", error510Response);
                        updateResponse(operation, "511", error511Response);
                        updateResponse(operation, "512", error512Response);
                        updateResponse(operation, "513", error513Response);
                    }));
        };
    }

    /**
     * Helper method to update responses of an operation with custom error responses.
     *
     * @param operation   Operation to update responses for.
     * @param statusCode  Status code of the response.
     * @param response    Custom ApiResponse to add to the operation.
     */
    private void updateResponse(Operation operation, String statusCode, ApiResponse response) {
        operation.responses(operation.getResponses()
                .addApiResponse(statusCode, response));
    }
}
