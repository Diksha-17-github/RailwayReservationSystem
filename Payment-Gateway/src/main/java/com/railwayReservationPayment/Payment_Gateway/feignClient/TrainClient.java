package com.railwayReservationPayment.Payment_Gateway.feignClient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Feign client interface for communicating with the train management service.
 */
@FeignClient(name = "trainmanagementservice")
public interface TrainClient {

    /**
     * Reduces the available seats for a given train and class type.
     *
     * @param trainNumber the train number
     * @param classType   the class type (e.g., "Sleeper", "AC")
     * @param seats       number of seats to reduce
     */
    @PutMapping("/api/trains/reduceSeats")
    void reduceAvailableSeats(@RequestParam String trainNumber,
                              @RequestParam String classType,
                              @RequestParam int seats);
}
