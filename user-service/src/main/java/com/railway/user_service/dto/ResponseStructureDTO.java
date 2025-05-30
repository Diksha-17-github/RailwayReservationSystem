package com.railway.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "Standard response wrapper")
public class ResponseStructureDTO<T> {

    @Schema(description = "Response timestamp", example = "2025-05-15T15:30:00")
    @NotNull
    private LocalDateTime timestamp;

    @Schema(description = "Status message", example = "Success")
    private String message;

    @Schema(description = "Response data")
    private T data;

}
