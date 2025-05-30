package com.railway.common.dto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;
import com.railway.common.enums.Role;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private UUID userId;
    private String userName;
    private String email;
    private Role role;
    private LocalDateTime createdAt;
}