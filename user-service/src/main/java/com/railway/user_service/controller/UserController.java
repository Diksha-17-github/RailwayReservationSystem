package com.railway.user_service.controller;

import com.railway.user_service.dto.*;
import com.railway.user_service.service.IUserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController //combination of controller and response body
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final IUserService iUserService;

    public UserController(IUserService userService) {
        this.iUserService = userService;
    }

    /**
     * Registers a new user.
     *
     * @param userRequestDTO the user data to register
     * @return created user response wrapped in ResponseStructureDTO
     */
    @PostMapping("/register")
    public ResponseEntity<ResponseStructureDTO<UserResponseDTO>> addUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        logger.info("Received request to register user with username: {}", userRequestDTO.getUserName());
        ResponseStructureDTO<UserResponseDTO> response = iUserService.addUser(userRequestDTO);
        logger.info("User registered successfully with username: {}", userRequestDTO.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Retrieves a list of all users.
     *
     * @return list of users wrapped in ResponseStructureDTO
     */
    @GetMapping("/allUsers")
    public ResponseEntity<ResponseStructureDTO<List<UserResponseDTO>>> getAllUsers() {
        logger.info("Received request to get all users");
        ResponseStructureDTO<List<UserResponseDTO>> allUsersList = iUserService.getAllUsers();
        logger.info("Returning {} users", allUsersList.getData().size());
        return ResponseEntity.status(HttpStatus.OK).body(allUsersList);
    }

    /**
     * Retrieves a user by their email.
     *
     * @param emailRequest email wrapped in EmailRequestDTO
     * @return user data wrapped in ResponseStructureDTO
     */
    @PostMapping("/getUserByEmail")
    public ResponseEntity<ResponseStructureDTO<UserResponseDTO>> getUserByEmail(@RequestBody EmailRequestDTO emailRequest) {
        logger.info("Received request to get user by email: {}", emailRequest.getEmail());
        ResponseStructureDTO<UserResponseDTO> response = iUserService.getUserByEmail(emailRequest.getEmail());
        logger.info("User fetched by email: {}", emailRequest.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Retrieves a user by their username.
     *
     * @param userName username as a plain string in request body
     * @return user data wrapped in ResponseStructureDTO
     */
    @PostMapping("/getUserByUserName")
    public ResponseEntity<ResponseStructureDTO<UserResponseDTO>> getUserByUserName(@RequestBody String userName) {
        logger.info("Received request to get user by username: {}", userName);
        ResponseStructureDTO<UserResponseDTO> response = iUserService.getUserByUserName(userName);
        logger.info("User fetched by username: {}", userName);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Authenticates a user using login credentials.
     *
     * @param loginDTO login credentials
     * @return user data wrapped in ResponseStructureDTO
     */
    @PostMapping("/userLogin")
    public ResponseEntity<ResponseStructureDTO<UserResponseDTO>> userLogin(@Valid @RequestBody LoginDTO loginDTO) {
        logger.info("User login attempt for username: {}", loginDTO.getUserName());
        ResponseStructureDTO<UserResponseDTO> response = iUserService.userLogin(loginDTO);
        logger.info("User login successful for username: {}", loginDTO.getUserName());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    /**
     * Retrieves a user by username via path variable.
     *
     * @param username username in the URL path
     * @return user data (only the data part) for Feign client consumption
     */
    @GetMapping("/username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUserNameViaPath(@PathVariable String username) {
        logger.info("Received request to get user by username via path: {}", username);
        ResponseStructureDTO<UserResponseDTO> response = iUserService.getUserByUserName(username);
        logger.info("Returning user data for username: {}", username);
        return ResponseEntity.ok(response.getData()); // Return only the data part for Feign to easily consume
    }
}
