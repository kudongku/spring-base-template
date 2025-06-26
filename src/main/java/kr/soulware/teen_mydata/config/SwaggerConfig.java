package kr.soulware.teen_mydata.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.*;
import org.springframework.context.annotation.Configuration;

@OpenAPIDefinition(
    info = @Info(title = "Teen MyData API", version = "v0.1"),
    security = {
        @SecurityRequirement(name = "bearerAuth"),
        @SecurityRequirement(name = "oauth2")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT"
)
@SecurityScheme(
    name = "oauth2",
    type = SecuritySchemeType.OAUTH2,
    flows = @OAuthFlows(
        authorizationCode = @OAuthFlow(
            authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth",
            tokenUrl = "https://oauth2.googleapis.com/token",
            scopes = {
                @OAuthScope(name = "profile", description = "Google profile info"),
                @OAuthScope(name = "email", description = "Google email info")
            }
        )
    )
)
@Configuration
public class SwaggerConfig {
}
