package com.railway.reservation_service;

import com.railway.common.dto.TrainClassDTO;
import com.railway.common.dto.TrainResponseDTO;
import com.railway.common.dto.UserResponseDTO;
import com.railway.reservation_service.dto.PassengerDTO;
import com.railway.reservation_service.dto.ReservationRequestDTO;
import com.railway.reservation_service.dto.ReservationResponseDTO;
import com.railway.reservation_service.entity.BookingStatus;
import com.railway.reservation_service.entity.Passenger;
import com.railway.reservation_service.entity.Reservation;
import com.railway.reservation_service.repository.ReservationRepository;
import com.railway.reservation_service.feignClients.TrainClient;
import com.railway.reservation_service.feignClients.UserClient;
import com.railway.reservation_service.service.EmailService;
import com.railway.reservation_service.service.ReservationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ReservationServiceImplTests {

	private ReservationRepository reservationRepository;
	private TrainClient trainClient;
	private UserClient userClient;
	private EmailService emailService;

	private ReservationServiceImpl reservationService;

	@BeforeEach
	void setUp() {
		reservationRepository = mock(ReservationRepository.class);
		trainClient = mock(TrainClient.class);
		userClient = mock(UserClient.class);
		emailService = mock(EmailService.class);

		reservationService = new ReservationServiceImpl(reservationRepository, userClient, trainClient, emailService);
	}

	@Test
	void testCreateReservation_Success() {
		// Prepare data
		PassengerDTO passenger = PassengerDTO.builder()
				.name("John Doe")
				.age(30)
				.gender("Male")
				.address("123 Street")
				.build();

		ReservationRequestDTO request = ReservationRequestDTO.builder()
				.userName("john")
				.trainNumber("12345")
				.classType("Sleeper")
				.journeyDate(LocalDate.now().plusDays(1))
				.passengers(List.of(passenger))
				.build();

		UserResponseDTO userDTO = UserResponseDTO.builder()
				.userName("john")
				.email("john@example.com")
				.build();

		TrainResponseDTO trainDTO = TrainResponseDTO.builder()
				.trainId(1)
				.trainNumber("12345")
				.trainName("Express")
				.build();

		TrainClassDTO classDTO = TrainClassDTO.builder()
				.classId(1)
				.trainId(1)
				.classType("Sleeper")
				.totalSeats(100)
				.availableSeats(50)
				.price(150.0)
				.quota("General")
				.build();

		when(userClient.getUserByUserName("john")).thenReturn(ResponseEntity.ok(userDTO));
		when(trainClient.getTrainByNumber("12345")).thenReturn(ResponseEntity.ok(trainDTO));
		when(trainClient.getTrainClassByTrainIdAndClassType(1, "Sleeper")).thenReturn(ResponseEntity.ok(classDTO));
		when(reservationRepository.save(any(Reservation.class))).thenAnswer(invocation -> {
			Reservation res = invocation.getArgument(0);
			res.setReservationId(1);
			res.setCreatedAt(LocalDateTime.now());
			return res;
		});

		ReservationResponseDTO response = reservationService.createReservation(request);

		assertNotNull(response);
		assertEquals("Sleeper", response.getClassType());
		assertEquals(BookingStatus.CONFIRMED.name(), response.getReservationStatus());
		assertEquals(150.0 * request.getPassengers().size(), response.getTotalFare());
		assertEquals("john", response.getUsername());
		assertEquals(1, response.getNumberOfSeats());
		assertEquals("12345", response.getTrainNumber());

		verify(emailService, times(1)).sendBookingConfirmation(
				eq(userDTO.getEmail()),
				eq(userDTO.getUserName()),
				anyString()
		);
	}

	@Test
	void testCreateReservation_UserNotFound() {
		ReservationRequestDTO request = ReservationRequestDTO.builder()
				.userName("nonexistent")
				.trainNumber("12345")
				.classType("Sleeper")
				.journeyDate(LocalDate.now().plusDays(1))
				.passengers(List.of())
				.build();

		when(userClient.getUserByUserName("nonexistent")).thenReturn(ResponseEntity.ok(null));

		Exception ex = assertThrows(RuntimeException.class, () -> reservationService.createReservation(request));
		assertTrue(ex.getMessage().contains("User not found"));
	}

	@Test
	void testCreateReservation_TrainNotFound() {
		UserResponseDTO userDTO = UserResponseDTO.builder()
				.userName("john")
				.email("john@example.com")
				.build();

		ReservationRequestDTO request = ReservationRequestDTO.builder()
				.userName("john")
				.trainNumber("00000")
				.classType("Sleeper")
				.journeyDate(LocalDate.now().plusDays(1))
				.passengers(List.of())
				.build();

		when(userClient.getUserByUserName("john")).thenReturn(ResponseEntity.ok(userDTO));
		when(trainClient.getTrainByNumber("00000")).thenReturn(ResponseEntity.ok(null));

		Exception ex = assertThrows(RuntimeException.class, () -> reservationService.createReservation(request));
		assertTrue(ex.getMessage().contains("Train not found"));
	}

	@Test
	void testCreateReservation_ClassTypeNotFound() {
		UserResponseDTO userDTO = UserResponseDTO.builder()
				.userName("john")
				.email("john@example.com")
				.build();

		TrainResponseDTO trainDTO = TrainResponseDTO.builder()
				.trainId(1)
				.trainNumber("12345")
				.trainName("Express")
				.build();

		ReservationRequestDTO request = ReservationRequestDTO.builder()
				.userName("john")
				.trainNumber("12345")
				.classType("AC") // Not available
				.journeyDate(LocalDate.now().plusDays(1))
				.passengers(List.of(PassengerDTO.builder().name("John Doe").age(30).gender("Male").address("123 Street").build()))
				.build();

		when(userClient.getUserByUserName("john")).thenReturn(ResponseEntity.ok(userDTO));
		when(trainClient.getTrainByNumber("12345")).thenReturn(ResponseEntity.ok(trainDTO));
		when(trainClient.getTrainClassByTrainIdAndClassType(1, "AC")).thenReturn(ResponseEntity.ok(null));

		Exception ex = assertThrows(RuntimeException.class, () -> reservationService.createReservation(request));
		assertTrue(ex.getMessage().contains("Class type AC not available"));
	}

	@Test
	void testCreateReservation_NotEnoughSeats() {
		UserResponseDTO userDTO = UserResponseDTO.builder()
				.userName("john")
				.email("john@example.com")
				.build();

		TrainResponseDTO trainDTO = TrainResponseDTO.builder()
				.trainId(1)
				.trainNumber("12345")
				.trainName("Express")
				.build();

		TrainClassDTO classDTO = TrainClassDTO.builder()
				.classId(1)
				.trainId(1)
				.classType("Sleeper")
				.totalSeats(100)
				.availableSeats(1) // Only 1 seat available
				.price(150.0)
				.quota("General")
				.build();

		PassengerDTO p1 = PassengerDTO.builder().name("John Doe").age(30).gender("Male").address("123 Street").build();
		PassengerDTO p2 = PassengerDTO.builder().name("Jane Doe").age(28).gender("Female").address("456 Avenue").build();

		ReservationRequestDTO request = ReservationRequestDTO.builder()
				.userName("john")
				.trainNumber("12345")
				.classType("Sleeper")
				.journeyDate(LocalDate.now().plusDays(1))
				.passengers(List.of(p1, p2)) // 2 passengers but only 1 seat available
				.build();

		when(userClient.getUserByUserName("john")).thenReturn(ResponseEntity.ok(userDTO));
		when(trainClient.getTrainByNumber("12345")).thenReturn(ResponseEntity.ok(trainDTO));
		when(trainClient.getTrainClassByTrainIdAndClassType(1, "Sleeper")).thenReturn(ResponseEntity.ok(classDTO));

		IllegalStateException ex = assertThrows(IllegalStateException.class, () -> reservationService.createReservation(request));
		assertTrue(ex.getMessage().contains("Only 1 seats available"));
	}
}
