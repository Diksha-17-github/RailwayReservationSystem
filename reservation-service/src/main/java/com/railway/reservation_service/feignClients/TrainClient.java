package com.railway.reservation_service.feignClients;

import com.railway.common.dto.TrainClassDTO;
import com.railway.common.dto.TrainResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Feign client interface for communicating with the Train Management Service.
 */
@FeignClient(name = "trainmanagementservice")
public interface TrainClient {

    /**
     * Get train details by train ID.
     */
    @GetMapping("/api/trains/{id}")
    ResponseEntity<TrainResponseDTO> getTrainById(@PathVariable("id") int trainId);

    /**
     * Get train details by train number.
     */
    @GetMapping("/api/trains/number/{trainNumber}")
    ResponseEntity<TrainResponseDTO> getTrainByNumber(@PathVariable("trainNumber") String trainNumber);

    /**
     * Get train class details by class ID.
     */
    @GetMapping("/api/trains/id/{classId}")
    ResponseEntity<TrainClassDTO> getClassById(@PathVariable("classId") int classId);

    /**
     * Get train class details by train ID and class type.
     */
    @GetMapping("/api/trains/train/{trainId}/class-type/{classType}")
    ResponseEntity<TrainClassDTO> getTrainClassByTrainIdAndClassType(
            @PathVariable("trainId") int trainId,
            @PathVariable("classType") String classType
    );
}
