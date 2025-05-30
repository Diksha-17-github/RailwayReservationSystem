package com.railway.reservation_service.feignClients;

import com.railway.common.dto.EmailRequestDTO;
import com.railway.common.dto.ResponseStructureDTO;
import com.railway.common.dto.UserResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client interface for communicating with the User Service.
 */
@FeignClient(name = "userservice")
public interface UserClient {

    /**
     * Retrieves user details by email using POST request.
     *
     * @param emailRequestDTO the email request DTO containing the user's email
     * @return response structure containing the UserResponseDTO
     */
    @PostMapping("/users/getUserByEmail")
    ResponseStructureDTO<UserResponseDTO> getUserByEmail(@RequestBody EmailRequestDTO emailRequestDTO);

    /**
     * Retrieves user details by username using GET request.
     *
     * @param username the username of the user
     * @return ResponseEntity containing the UserResponseDTO
     */
    @GetMapping("/users/username/{username}")
    ResponseEntity<UserResponseDTO> getUserByUserName(@PathVariable("username") String username);
}
