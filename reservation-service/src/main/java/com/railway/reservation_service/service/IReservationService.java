package com.railway.reservation_service.service;

import com.railway.reservation_service.dto.ReservationRequestDTO;
import com.railway.reservation_service.dto.ReservationResponseDTO;

/**
 * Service interface for handling reservation operations.
 */
public interface IReservationService {

    /**
     * Create a new reservation based on the request DTO.
     * @param request ReservationRequestDTO containing reservation details.
     * @return ReservationResponseDTO with reservation details.
     */
    ReservationResponseDTO createReservation(ReservationRequestDTO request);

    /**
     * Update the status of a reservation by PNR.
     * @param pnr Passenger Name Record identifier.
     */
    void updateStatusByPNR(String pnr);

    /**
     * Cancel a reservation by PNR.
     * @param pnr Passenger Name Record identifier.
     * @return ReservationResponseDTO after cancellation.
     */
    ReservationResponseDTO cancelReservation(String pnr);

    /**
     * Retrieve a reservation by its PNR.
     * @param pnr Passenger Name Record identifier.
     * @return ReservationResponseDTO for the given PNR.
     */
    ReservationResponseDTO getReservationByPNR(String pnr);

    /**
     * Delete a reservation by its ID.
     * @param reservationId The reservation ID.
     * @return A confirmation message.
     */
    String deleteReservation(int reservationId);

    /**
     * Update reservation status by PNR and return updated reservation details.
     * @param pnr Passenger Name Record identifier.
     * @return Updated ReservationResponseDTO.
     */
    ReservationResponseDTO updateReservationStatus(String pnr);

    /**
     * Send booking confirmation to user regarding the successful booking reservation created
     * @return Updated ReservationResponseDTO.
     */
    void sendBookingConfirmation(String toEmail, String userName, String pnr);
}
