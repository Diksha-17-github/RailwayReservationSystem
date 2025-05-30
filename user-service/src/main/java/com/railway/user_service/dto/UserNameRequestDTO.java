package com.railway.user_service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Username request DTO")
public class UserNameRequestDTO {

    @Schema(description = "Username", example = "john_doe")
    private String userName;
}
