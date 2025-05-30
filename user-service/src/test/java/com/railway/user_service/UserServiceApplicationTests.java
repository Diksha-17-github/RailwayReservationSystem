package com.railway.user_service;

import com.railway.user_service.controller.UserController;
import com.railway.user_service.dto.EmailRequestDTO;
import com.railway.user_service.dto.ResponseStructureDTO;
import com.railway.user_service.dto.UserResponseDTO;
import com.railway.user_service.entity.Role;
import com.railway.user_service.service.IUserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class) // Tell Spring which controller to test

public class UserServiceApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private IUserService iUserService;

	private UserResponseDTO userResponseDTO;

	@BeforeEach
	void setUp() {
		userResponseDTO = new UserResponseDTO();
		userResponseDTO.setUserId(UUID.fromString("58f8fae0-2df5-41bf-9b0c-4e0f49b007c5"));
		userResponseDTO.setUserName("dikshapimple");
		userResponseDTO.setEmail("diksha@xyz.com");
		userResponseDTO.setRole(Role.USER);
		userResponseDTO.setCreatedAt(LocalDateTime.parse("2025-04-06T16:50:29"));
	}

	@Test
	public void testGetAllUsers() throws Exception {
		ResponseStructureDTO<List<UserResponseDTO>> responseStructure =
				new ResponseStructureDTO<>(LocalDateTime.now(), "Users fetched successfully!", Collections.singletonList(userResponseDTO));

		Mockito.when(iUserService.getAllUsers()).thenReturn(responseStructure);

		mockMvc.perform(get("/api/v1/users/allUsers"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("Users fetched successfully!"))
				.andExpect(jsonPath("$.data[0].userName").value("dikshapimple"));
	}

	@Test
	public void testGetUserByEmail() throws Exception {
		EmailRequestDTO emailRequest = new EmailRequestDTO();
		emailRequest.setEmail("diksha@xyz.com");

		ResponseStructureDTO<UserResponseDTO> responseStructure =
				new ResponseStructureDTO<>(LocalDateTime.now(), "User fetched successfully!", userResponseDTO);

		Mockito.when(iUserService.getUserByEmail(emailRequest.getEmail())).thenReturn(responseStructure);

		mockMvc.perform(post("/api/v1/users/getUserByEmail")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"email\":\"diksha@xyz.com\"}"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").value("User fetched successfully!"))
				.andExpect(jsonPath("$.data.userName").value("dikshapimple"));
	}
}
