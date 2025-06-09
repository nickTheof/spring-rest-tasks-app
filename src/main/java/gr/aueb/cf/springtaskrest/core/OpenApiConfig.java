package gr.aueb.cf.springtaskrest.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;

@OpenAPIDefinition(
        info = @Info(title = "Tasks Management API", version = "1.0"),
        security = @SecurityRequirement(name = "bearerAuth"),
        tags = {
                @Tag(name = "Authentication", description = "User authentication endpoints"),
                @Tag(name = "Users", description = "User management endpoints"),
                @Tag(name = "Tasks", description = "Tasks management endpoints"),
                @Tag(name = "Admin", description = "Admin authentication endpoints"),
                @Tag(name = "CurrentUser", description = "Current authenticated user endpoints")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT"
)
public class OpenApiConfig {
}
