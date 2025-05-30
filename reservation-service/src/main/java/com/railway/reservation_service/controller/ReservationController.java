package com.railway.reservation_service.controller;

import com.railway.reservation_service.dto.*;
import com.railway.reservation_service.entity.Reservation;
import com.railway.reservation_service.service.IReservationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    @Autowired
    private IReservationService reservationService;

    /**
     * Endpoint to book a new reservation.
     * @param request the reservation details.
     * @return the booked reservation details.
     */
    @PostMapping
    public ReservationResponseDTO bookReservation(@RequestBody ReservationRequestDTO request) {
        logger.info("Booking reservation for user: {}", request.getUserName());
        ReservationResponseDTO response = reservationService.createReservation(request);
        logger.info("Reservation booked successfully with PNR:");
        return response;
    }

    /**
     * Endpoint to update the status of a reservation by PNR.
     * @param pnr the PNR number of the reservation.
     * @return updated reservation details.
     */
    @PutMapping("/status/{pnr}")
    public ReservationResponseDTO updateReservationStatus(@PathVariable String pnr) {
        logger.info("Updating reservation status for PNR: {}", pnr);
        ReservationResponseDTO response = reservationService.updateReservationStatus(pnr);
        logger.info("Reservation status updated for PNR: {}", pnr);
        return response;
    }

    /**
     * Endpoint to delete a reservation by ID.
     * @param id the reservation ID.
     * @return success message.
     */
    @DeleteMapping("/{id}")
    public String deleteReservation(@PathVariable int id) {
        logger.info("Deleting reservation with ID: {}", id);
        String result = reservationService.deleteReservation(id);
        logger.info("Reservation with ID {} deleted", id);
        return result;
    }

    /**
     * Endpoint to get reservation details by PNR.
     * @param pnr the PNR number.
     * @return reservation details.
     */
    @GetMapping("/pnr/{pnr}")
    public ReservationResponseDTO getByPnr(@PathVariable String pnr) {
        logger.info("Fetching reservation details for PNR: {}", pnr);
        return reservationService.getReservationByPNR(pnr);
    }

    /**
     * Endpoint to cancel a reservation by PNR.
     * @param pnr the PNR number.
     * @return updated reservation details after cancellation.
     */
    @PutMapping("/cancel/{pnr}")
    public ReservationResponseDTO cancelReservation(@PathVariable String pnr) {
        logger.info("Cancelling reservation for PNR: {}", pnr);
        ReservationResponseDTO response = reservationService.cancelReservation(pnr);
        logger.info("Reservation cancelled for PNR: {}", pnr);
        return response;
    }
}
