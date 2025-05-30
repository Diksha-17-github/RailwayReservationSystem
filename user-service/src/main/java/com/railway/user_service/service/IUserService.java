package com.railway.user_service.service;

import com.railway.user_service.dto.LoginDTO;
import com.railway.user_service.dto.ResponseStructureDTO;
import com.railway.user_service.dto.UserRequestDTO;
import com.railway.user_service.dto.UserResponseDTO;

import java.util.List;

/**
 * Service interface for User operations such as registration,
 * retrieval, and authentication.
 */
public interface IUserService {

    /**
     * Adds a new user based on the provided request DTO.
     *
     * @param userRequestDTO user registration data
     * @return response structure containing the registered user details
     */
    ResponseStructureDTO<UserResponseDTO> addUser(UserRequestDTO userRequestDTO);

    /**
     * Retrieves all users in the system.
     *
     * @return response structure containing a list of user details
     */
    ResponseStructureDTO<List<UserResponseDTO>> getAllUsers();

    /**
     * Retrieves a user by email address.
     *
     * @param email user's email
     * @return response structure containing user details
     */
    ResponseStructureDTO<UserResponseDTO> getUserByEmail(String email);

    /**
     * Retrieves a user by username.
     *
     * @param userName user's username
     * @return response structure containing user details
     */
    ResponseStructureDTO<UserResponseDTO> getUserByUserName(String userName);

    /**
     * Authenticates a user with login credentials.
     *
     * @param loginDTO login data containing username and password
     * @return response structure containing authenticated user details
     */
    ResponseStructureDTO<UserResponseDTO> userLogin(LoginDTO loginDTO);
}
