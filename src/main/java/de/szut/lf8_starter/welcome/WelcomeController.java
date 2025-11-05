package de.szut.lf8_starter.welcome;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "System", description = "System health and utility endpoints")
public class WelcomeController {

    @GetMapping("/welcome")
    @Operation(
        summary = "Welcome endpoint",
        description = "Simple health check endpoint to verify the API is running."
    )
    @ApiResponse(
        responseCode = "200",
        description = "API is running",
        content = @Content(
            mediaType = "text/plain",
            examples = @ExampleObject(value = "welcome to lf8_starter")
        )
    )
    public String welcome() {
        return "welcome to lf8_starter";
    }

    @GetMapping("/roles")
    @Hidden  // Hide from Swagger UI as it's primarily for debugging
    public ResponseEntity<?> getRoles(Authentication authentication) {
        return ResponseEntity.ok(authentication.getAuthorities());
    }


}
