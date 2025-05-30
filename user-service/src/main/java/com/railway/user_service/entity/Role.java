package com.railway.user_service.entity;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "User role")
public enum Role {
    USER,
    ADMIN
}
