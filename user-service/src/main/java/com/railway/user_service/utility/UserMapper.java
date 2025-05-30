package com.railway.user_service.utility;

import com.railway.user_service.dto.UserRequestDTO;
import com.railway.user_service.dto.UserResponseDTO;
import com.railway.user_service.entity.User;

/**
 * Utility class for mapping between User entity and DTOs.
 * Prevents code duplication and promotes cleaner service layers.
 */
public class UserMapper {

    // Private constructor to prevent instantiation
    private UserMapper() {}

    /**
     * Maps a UserRequestDTO to a User entity.
     *
     * @param userRequestDTO the incoming user request DTO
     * @return a User entity populated with data from the DTO
     */
    public static User mapToEntity(UserRequestDTO userRequestDTO) {
        return User.builder()
                .userName(userRequestDTO.getUserName())
                .email(userRequestDTO.getEmail())
                .password(userRequestDTO.getPassword())
                .build();
    }

    /**
     * Maps a User entity to a UserResponseDTO.
     *
     * @param user the User entity from the database
     * @return a UserResponseDTO to be sent as a response
     */
    public static UserResponseDTO mapToDTO(User user) {
        return UserResponseDTO.builder()
                .userId(user.getUserId())
                .userName(user.getUserName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
