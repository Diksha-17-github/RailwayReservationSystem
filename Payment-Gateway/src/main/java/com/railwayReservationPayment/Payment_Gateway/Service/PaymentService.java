package com.railwayReservationPayment.Payment_Gateway.Service;

import com.railway.common.dto.ReservationResponseDTO;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentRequestDTO;
import com.railwayReservationPayment.Payment_Gateway.dto.PaymentResponseDTO;

public interface PaymentService {

    /**
     * Initiates the Stripe checkout process for a given PNR and base URL.
     * @param pnr - The PNR of the reservation
     * @param baseUrl - The base URL for redirect after payment
     * @return PaymentResponseDTO containing checkout session info
     */
    PaymentResponseDTO initiateCheckout(String pnr, String baseUrl);

    /**
     * Performs the checkout process given a payment request DTO.
     * @param paymentRequestDTO - DTO containing payment details
     * @return PaymentResponseDTO with checkout details
     */
    PaymentResponseDTO checkout(PaymentRequestDTO paymentRequestDTO);

    /**
     * Handles the logic after successful payment completion.
     * @param sessionId - Stripe session ID for the payment
     * @return A confirmation message or relevant response
     */
    String handlePaymentSuccess(String sessionId);

    /**
     * Fetches the reservation details for a given PNR.
     * @param pnr - The PNR of the reservation
     * @return ReservationResponseDTO containing reservation info
     */
    ReservationResponseDTO getReservationByPNR(String pnr);
}
