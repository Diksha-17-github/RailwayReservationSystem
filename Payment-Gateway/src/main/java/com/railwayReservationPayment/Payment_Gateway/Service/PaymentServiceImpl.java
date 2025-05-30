package com.railwayReservationPayment.Payment_Gateway.Service;

import com.railwayReservationPayment.Payment_Gateway.dto.PaymentRequestDTO;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentResponseDTO;
import com.railwayReservationPayment.Payment_Gateway.repository.PaymentRepository;
import com.railway.common.dto.ReservationResponseDTO;
import com.railwayReservationPayment.Payment_Gateway.entity.Payment;
import com.railwayReservationPayment.Payment_Gateway.entity.PaymentStatus;
import com.railwayReservationPayment.Payment_Gateway.feignClient.TrainClient;
import com.railwayReservationPayment.Payment_Gateway.feignClient.ReservationClient;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final ReservationClient reservationClient;
    private final TrainClient trainClient;
    private final EmailService emailService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              ReservationClient reservationClient,
                              TrainClient trainClient,
                              EmailService emailService) {
        this.paymentRepository = paymentRepository;
        this.reservationClient = reservationClient;
        this.trainClient = trainClient;
        this.emailService = emailService;
    }

    @Setter
    @Value("${stripe.secretKey}")
    private String secretKey;

    @Override
    public PaymentResponseDTO initiateCheckout(String pnr, String baseUrl) {
        logger.info("Initiating checkout for PNR: {}, baseUrl: {}", pnr, baseUrl);

        ResponseEntity<ReservationResponseDTO> response = reservationClient.getReservationByPNR(pnr);
        ReservationResponseDTO reservation = response.getBody();

        if (reservation == null) {
            String errMsg = "Reservation not found for PNR: " + pnr;
            logger.error(errMsg);
            throw new RuntimeException(errMsg);
        }

        PaymentRequestDTO paymentRequest = PaymentRequestDTO.builder()
                .pnr(pnr)
                .ticketName("Train: " + reservation.getTrainName() + " | Class: " + reservation.getClassType())
                .currency("INR")
                .amount((long) (reservation.getTotalFare() * 100)) // converting to paise (minor units)
                .quantity(1L)
                .successBaseUrl(baseUrl)
                .build();

        logger.debug("Created PaymentRequestDTO: {}", paymentRequest);

        return checkout(paymentRequest);
    }

    @Override
    @Transactional
    public PaymentResponseDTO checkout(PaymentRequestDTO paymentRequestDTO) {
        logger.info("Processing checkout for PNR: {}", paymentRequestDTO.getPnr());

        Stripe.apiKey = secretKey;

        // Build product and pricing data for Stripe
        SessionCreateParams.LineItem.PriceData.ProductData ticketData =
                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(paymentRequestDTO.getTicketName())
                        .build();

        SessionCreateParams.LineItem.PriceData priceData =
                SessionCreateParams.LineItem.PriceData.builder()
                        .setCurrency(paymentRequestDTO.getCurrency())
                        .setUnitAmount(paymentRequestDTO.getAmount())
                        .setProductData(ticketData)
                        .build();

        SessionCreateParams.LineItem lineItem =
                SessionCreateParams.LineItem.builder()
                        .setQuantity(paymentRequestDTO.getQuantity())
                        .setPriceData(priceData)
                        .build();

        String baseUrl = paymentRequestDTO.getSuccessBaseUrl() != null
                ? paymentRequestDTO.getSuccessBaseUrl()
                : "http://localhost:8083"; // fallback URL

        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(baseUrl + "/payment/success?session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(baseUrl + "/payment/cancel")
                .putMetadata("pnr", paymentRequestDTO.getPnr())
                .addLineItem(lineItem)
                .build();

        try {
            Session session = Session.create(params);
            logger.info("Stripe checkout session created successfully for PNR: {}", paymentRequestDTO.getPnr());

            return PaymentResponseDTO.builder()
                    .status("Success")
                    .message("Payment Session Created!")
                    .sessionId(session.getId())
                    .sessionUrl(session.getUrl())
                    .build();
        } catch (StripeException e) {
            logger.error("Stripe session creation failed for PNR: {}", paymentRequestDTO.getPnr(), e);
            throw new RuntimeException("Stripe session creation failed", e);
        }
    }

    @Override
    public String handlePaymentSuccess(String sessionId) {
        logger.info("Handling payment success for sessionId: {}", sessionId);
        try {
            // 1. Retrieve session details from Stripe
            Session session = Session.retrieve(sessionId);

            // 2. Extract PNR from session metadata
            String pnr = session.getMetadata().get("pnr");
            logger.debug("Payment session metadata PNR: {}", pnr);

            // 3. Get reservation details for the PNR
            ReservationResponseDTO reservation = reservationClient.getReservationByPNR(pnr).getBody();
            if (reservation == null) {
                String errMsg = "Reservation not found for PNR: " + pnr;
                logger.error(errMsg);
                throw new RuntimeException(errMsg);
            }

            // 4. Save payment record in DB
            Payment payment = Payment.builder()
                    .reservationId(reservation.getReservationId())
                    .amount(reservation.getTotalFare())
                    .paymentMethod("CARD")
                    .paymentStatus(PaymentStatus.SUCCESS)
                    .build();
            paymentRepository.save(payment);
            logger.info("Payment saved successfully for reservationId: {}", reservation.getReservationId());

            try {
                emailService.sendPaymentSuccessEmail(
                        reservation.getEmail(),
                        reservation.getUsername(),
                        reservation.getPnrNumber(),
                        reservation.getTotalFare()
                );
                logger.info("Payment confirmation email sent to {}", reservation.getEmail());
            } catch (Exception e) {
                logger.error("Failed to send payment email to {}: {}", reservation.getEmail(), e.getMessage());
            }

            // 5. Update reservation status to CONFIRMED
            ReservationResponseDTO updatedReservation = reservationClient.updateReservationStatus(pnr);
            if (updatedReservation != null && "CONFIRMED".equals(updatedReservation.getReservationStatus())) {
                logger.info("Reservation confirmed successfully for PNR: {}", pnr);
            } else {
                logger.warn("Failed to confirm reservation for PNR: {}", pnr);
            }

            // 6. Reduce available seats in train
            trainClient.reduceAvailableSeats(reservation.getTrainNumber(), reservation.getClassType(), reservation.getNumberOfSeats());
            logger.info("Reduced {} seats for train: {} class: {}", reservation.getNumberOfSeats(), reservation.getTrainNumber(), reservation.getClassType());

            return "Payment successful and recorded!";

        } catch (Exception e) {
            logger.error("Error processing payment success for sessionId: {}", sessionId, e);
            return "Payment success processing failed!";
        }
    }

    @Override
    public ReservationResponseDTO getReservationByPNR(String pnr) {
        logger.info("Fetching reservation details for PNR: {}", pnr);
        return reservationClient.getReservationByPNR(pnr).getBody();
    }
}
