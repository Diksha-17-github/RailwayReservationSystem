package com.railwayReservationPayment.Payment_Gateway.controller;

import com.railwayReservationPayment.Payment_Gateway.Service.PaymentService;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentResponseDTO;
import com.railway.common.dto.ReservationResponseDTO;
import com.stripe.model.Event;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Scanner;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentService paymentService;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;  // Inject the webhook secret

    // Injecting PaymentService via constructor
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /**
     * Initiates Stripe checkout session for a given reservation PNR.
     * @param pnr Reservation PNR number
     * @param baseUrl Base URL for redirect after payment
     * @return PaymentResponseDTO with checkout details or error message
     */
    @PostMapping("/checkout/{pnr}")
    public ResponseEntity<?> checkoutByPNR(
            @PathVariable("pnr") String pnr,
            @RequestParam(value = "baseUrl", required = true) String baseUrl) {

        logger.info("Initiating checkout for PNR: {}, baseUrl: {}", pnr, baseUrl);

        if (baseUrl == null || baseUrl.isBlank()) {
            logger.warn("BaseUrl query parameter is missing or blank");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error: 'baseUrl' query parameter is required.");
        }

        PaymentResponseDTO stripeResponse = paymentService.initiateCheckout(pnr, baseUrl);
        logger.info("Checkout session created for PNR: {}", pnr);
        return ResponseEntity.ok(stripeResponse);
    }

    /**
     * Handles successful payment callback from Stripe.
     * @param sessionId Stripe checkout session ID
     * @return Confirmation message or error response
     */
    @GetMapping("/success")
    public ResponseEntity<?> paymentSuccess(@RequestParam("session_id") String sessionId) {
        logger.info("Payment success callback received for sessionId: {}", sessionId);
        try {
            Session session = Session.retrieve(sessionId);

            if ("paid".equals(session.getPaymentStatus())) {
                logger.info("Payment confirmed for sessionId: {}", sessionId);
                // Manually call your payment handling logic
                String result = paymentService.handlePaymentSuccess(sessionId);
                return ResponseEntity.ok("Payment successful. " + result);
            } else {
                logger.warn("Payment not completed yet for sessionId: {}", sessionId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Payment not completed yet.");
            }

        } catch (Exception e) {
            logger.error("Error processing payment success for sessionId: {}", sessionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }

    /**
     * Handles payment cancellation scenario.
     * @return Cancellation message
     */
    @GetMapping("/cancel")
    public String paymentCancelled() {
        logger.info("Payment cancelled by user");
        return "Payment was cancelled.";
    }

    /**
     * Checks the booking status of a reservation by PNR.
     * @param pnr Reservation PNR number
     * @return Reservation details or error message
     */
    @GetMapping("/reservation/status/{pnr}")
    public ResponseEntity<?> checkBookingStatus(@PathVariable("pnr") String pnr) {
        logger.info("Checking booking status for PNR: {}", pnr);
        try {
            // Call the service to get reservation details
            ReservationResponseDTO reservationResponse = paymentService.getReservationByPNR(pnr);
            if (reservationResponse == null) {
                logger.warn("Reservation not found for PNR: {}", pnr);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Reservation not found for PNR: " + pnr);
            }

            logger.info("Reservation found for PNR: {}", pnr);
            return ResponseEntity.ok(reservationResponse);
        } catch (Exception e) {
            logger.error("Error retrieving reservation status for PNR: {}", pnr, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error retrieving reservation status: " + e.getMessage());
        }
    }

    /**
     * Webhook endpoint to receive Stripe events.
     * Verifies signature and processes relevant events.
     * @param request HttpServletRequest containing webhook payload and headers
     * @return Response status and message
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(HttpServletRequest request) {
        String payload;
        try (Scanner scanner = new Scanner(request.getInputStream()).useDelimiter("\\A")) {
            payload = scanner.hasNext() ? scanner.next() : "";
        } catch (IOException e) {
            logger.error("Failed to read webhook payload", e);
            return ResponseEntity.status(400).body("Invalid payload");
        }

        Event event = null;
        try {
            event = Webhook.constructEvent(
                    payload,
                    request.getHeader("Stripe-Signature"),
                    webhookSecret
            );
        } catch (Exception e) {
            logger.error("Webhook signature verification failed", e);
            return ResponseEntity.status(400).body("Webhook error: " + e.getMessage());
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {
                Session session = (Session) event.getDataObjectDeserializer().getObject().get();
                String sessionId = session.getId();
                String pnr = session.getMetadata().get("pnr");

                logger.info("Received checkout.session.completed event for PNR: {}", pnr);

                // This might throw error internally
                paymentService.handlePaymentSuccess(sessionId);
            } catch (Exception e) {
                logger.error("Error processing checkout.session.completed webhook event", e);
                return ResponseEntity.status(500).body("Error processing payment success");
            }
        }

        logger.info("Webhook event processed successfully: {}", event.getType());
        return ResponseEntity.ok("Webhook received");
    }

}
