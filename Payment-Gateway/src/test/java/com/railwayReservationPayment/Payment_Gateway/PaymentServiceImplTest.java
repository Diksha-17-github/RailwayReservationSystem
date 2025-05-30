package com.railwayReservationPayment.Payment_Gateway;

import com.railwayReservationPayment.Payment_Gateway.Service.EmailService;
import com.railwayReservationPayment.Payment_Gateway.Service.PaymentServiceImpl;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentRequestDTO;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentResponseDTO;
import com.railwayReservationPayment.Payment_Gateway.feignClient.ReservationClient;
import com.railwayReservationPayment.Payment_Gateway.feignClient.TrainClient;
import com.railwayReservationPayment.Payment_Gateway.repository.PaymentRepository;
import com.railway.common.dto.ReservationResponseDTO;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class PaymentServiceImplTest {

	private PaymentRepository paymentRepository;
	private ReservationClient reservationClient;
	private TrainClient trainClient;
	private PaymentServiceImpl paymentService;
	private EmailService emailService;

	@BeforeEach
	void setUp() {
		paymentRepository = mock(PaymentRepository.class);
		reservationClient = mock(ReservationClient.class);
		trainClient = mock(TrainClient.class);
		emailService = mock(EmailService.class); // added
		paymentService = new PaymentServiceImpl(paymentRepository, reservationClient, trainClient, emailService);
		paymentService.setSecretKey("sk_test_123");
	}


	@Test
	void testInitiateCheckout_ReturnsPaymentResponse() throws Exception {
		String pnr = "PNR123";
		ReservationResponseDTO reservationDTO = new ReservationResponseDTO();
		reservationDTO.setReservationId(1);
		reservationDTO.setTrainName("Express");
		reservationDTO.setClassType("SLEEPER");
		reservationDTO.setTotalFare(150.0);
		reservationDTO.setTrainNumber("12345");
		reservationDTO.setNumberOfSeats(2);

		when(reservationClient.getReservationByPNR(pnr)).thenReturn(ResponseEntity.ok(reservationDTO));

		// Mock Stripe static Session.create
		Session mockSession = mock(Session.class);
		when(mockSession.getId()).thenReturn("session123");
		when(mockSession.getUrl()).thenReturn("https://mock-checkout-url.com");

		try (MockedStatic<Session> mockedSession = mockStatic(Session.class)) {
			mockedSession.when(() -> Session.create(any(SessionCreateParams.class))).thenReturn(mockSession);

			PaymentRequestDTO dto = PaymentRequestDTO.builder()
					.pnr(pnr)
					.ticketName("Train: Express | Class: SLEEPER")
					.currency("INR")
					.amount(15000)
					.quantity(1L)
					.successBaseUrl("http://localhost:8765")
					.build();

			PaymentResponseDTO response = paymentService.checkout(dto);

			assertNotNull(response);
			assertEquals("Success", response.getStatus());
			assertEquals("session123", response.getSessionId());
		}
	}
}
