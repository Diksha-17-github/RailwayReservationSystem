package com.railway.user_service.service;

import com.railway.user_service.dto.LoginDTO;
import com.railway.user_service.dto.ResponseStructureDTO;
import com.railway.user_service.dto.UserRequestDTO;
import com.railway.user_service.dto.UserResponseDTO;
import com.railway.user_service.entity.User;
import com.railway.user_service.exception.FieldAlreadyExistException;
import com.railway.user_service.repository.IUserRepository;
import com.railway.user_service.utility.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j // Lombok logger
public class UserServiceImpl implements IUserService {
    private final IUserRepository userRepository;

    // Constructor with only the repository dependency
    public UserServiceImpl(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Register a new user after checking for existing email and username
     */
    @Override
    public ResponseStructureDTO<UserResponseDTO> addUser(UserRequestDTO userRequestDTO) {
        log.info("Attempting to register user with username: {}", userRequestDTO.getUserName());

        Optional<User> existingUser = userRepository.findByEmail(userRequestDTO.getEmail());
        Optional<User> existingUsername = userRepository.findByUserName(userRequestDTO.getUserName());

        if (existingUsername.isPresent()) {
            log.warn("Username {} already exists", userRequestDTO.getUserName());
            throw new FieldAlreadyExistException("User with username " + userRequestDTO.getUserName() + " already exists!");
        }

        if (existingUser.isPresent()) {
            log.warn("Email {} already exists", userRequestDTO.getEmail());
            throw new FieldAlreadyExistException("User with email " + userRequestDTO.getEmail() + " already exists!");
        }

        User user = UserMapper.mapToEntity(userRequestDTO);
        // No password encoding here, assuming plain text password
        user.setPassword(user.getPassword()); // Password stored as is, without encoding
        User addedUser = userRepository.save(user);

        log.info("User registered with ID: {}", addedUser.getUserId());
        return new ResponseStructureDTO<>(
                LocalDateTime.now(),
                "User registered successfully!",
                UserMapper.mapToDTO(addedUser)
        );
    }

    /**
     * Fetch and return all users from the database
     */
    @Override
    public ResponseStructureDTO<List<UserResponseDTO>> getAllUsers() {
        log.info("Fetching all users");
        List<User> allUsers = userRepository.findAll();
        List<UserResponseDTO> allUsersDTO = new ArrayList<>();
        for (User user : allUsers) {
            allUsersDTO.add(UserMapper.mapToDTO(user));
        }

        return new ResponseStructureDTO<>(
                LocalDateTime.now(),
                "All users fetched successfully",
                allUsersDTO
        );
    }

    /**
     * Get user by email
     */
    @Override
    public ResponseStructureDTO<UserResponseDTO> getUserByEmail(String email) {
        log.info("Fetching user by email: {}", email);
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            log.error("User with email {} not found", email);
            throw new IllegalArgumentException("User with email " + email + " does not exist!");
        }

        return new ResponseStructureDTO<>(
                LocalDateTime.now(),
                "User fetched successfully!",
                UserMapper.mapToDTO(user.get())
        );
    }

    /**
     * Get user by username
     */
    @Override
    public ResponseStructureDTO<UserResponseDTO> getUserByUserName(String userName) {
        log.info("Fetching user by username: {}", userName);
        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            log.error("User with username {} not found", userName);
            throw new IllegalArgumentException("User with username " + userName + " does not exist!");
        }

        return new ResponseStructureDTO<>(
                LocalDateTime.now(),
                "User fetched successfully!",
                UserMapper.mapToDTO(user.get())
        );
    }

    /**
     * User login with username and password
     */
    @Override
    public ResponseStructureDTO<UserResponseDTO> userLogin(LoginDTO loginDTO) {
        String userName = loginDTO.getUserName();
        String password = loginDTO.getPassword();
        log.info("Attempting login for username: {}", userName);

        Optional<User> user = userRepository.findByUserName(userName);
        if (user.isEmpty()) {
            log.error("Login failed. Username {} not found", userName);
            throw new IllegalArgumentException("User with username " + userName + " does not exist!");
        }

        User userData = user.get();
        // No password check, assuming plain text comparison
        if (!userData.getPassword().equals(password)) {
            log.error("Invalid password for username {}", userName);
            throw new IllegalArgumentException("Invalid password!");
        }

        log.info("Login successful for username: {}", userName);
        return new ResponseStructureDTO<>(
                LocalDateTime.now(),
                "Login successful!",
                UserMapper.mapToDTO(userData)
        );
    }
}
