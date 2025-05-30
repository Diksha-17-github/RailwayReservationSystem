package com.railway.TrainManagementService;

import com.railway.TrainManagementSystem.Controller.TrainController;
import com.railway.TrainManagementSystem.DTO.TrainResponseDTO;
import com.railway.TrainManagementSystem.Service.TrainService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.sql.Time;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class TrainManagementServiceApplicationTests {

	private MockMvc mockMvc;

	@Mock
	private TrainService trainService;

	@InjectMocks
	private TrainController trainController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(trainController).build();
	}

	@Test
	void testGetAllTrains() throws Exception {
		TrainResponseDTO train1 = TrainResponseDTO.builder()
				.trainId(1)
				.trainName("Express")
				.source("CityA")
				.destination("CityB")
				.departureTime(Time.valueOf("10:00:00"))
				.arrivalTime(Time.valueOf("15:00:00"))
				.runningDays(127)
				.availability(true)
				.trainType("Sleeper")
				.createdAt(Date.valueOf("2025-04-01"))
				.updatedAt(Date.valueOf("2025-04-02"))
				.build();

		TrainResponseDTO train2 = TrainResponseDTO.builder()
				.trainId(2)
				.trainName("Local")
				.source("CityC")
				.destination("CityD")
				.departureTime(Time.valueOf("08:00:00"))
				.arrivalTime(Time.valueOf("11:00:00"))
				.runningDays(62)
				.availability(true)
				.trainType("Seater")
				.createdAt(Date.valueOf("2025-04-01"))
				.updatedAt(Date.valueOf("2025-04-02"))
				.build();

		List<TrainResponseDTO> trains = Arrays.asList(train1, train2);

		when(trainService.getAllTrains()).thenReturn(trains);

		mockMvc.perform(get("/api/trains")
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.size()").value(trains.size()))
				.andExpect(jsonPath("$[0].trainName").value(train1.getTrainName()))
				.andExpect(jsonPath("$[1].trainName").value(train2.getTrainName()));
	}
}
