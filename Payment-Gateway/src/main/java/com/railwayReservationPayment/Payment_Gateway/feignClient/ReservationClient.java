package com.railwayReservationPayment.Payment_Gateway.feignClient;

import com.railway.common.dto.ReservationResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * Feign client interface for communicating with the reservation-service.
 */
@FeignClient(name = "reservation-service")
public interface ReservationClient {

    /**
     * Retrieves reservation details by PNR.
     *
     * @param pnr the PNR number
     * @return ResponseEntity containing ReservationResponseDTO
     */
    @GetMapping("/reservations/pnr/{pnr}")
    ResponseEntity<ReservationResponseDTO> getReservationByPNR(@PathVariable("pnr") String pnr);

    /**
     * Updates the reservation status by PNR.
     *
     * @param pnr the PNR number
     * @return updated ReservationResponseDTO
     */
    @PutMapping("/reservations/status/{pnr}")
    ReservationResponseDTO updateReservationStatus(@PathVariable("pnr") String pnr);
}
