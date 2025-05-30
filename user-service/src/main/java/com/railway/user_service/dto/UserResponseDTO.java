package com.railway.user_service.dto;

import com.railway.user_service.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "User response DTO")
public class UserResponseDTO {

    @Schema(description = "User UUID", type = "string", format = "uuid", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID userId;

    @Schema(description = "User name", example = "john_doe")
    private String userName;

    @Schema(description = "Email address", example = "john@example.com")
    private String email;

    @Schema(description = "User role", example = "USER")
    private Role role;

    @Schema(description = "Creation timestamp", type = "string", format = "date-time", example = "2025-05-15T15:30:00")
    private LocalDateTime createdAt;
}
