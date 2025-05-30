package com.railway.TrainManagementSystem.Controller;

import com.railway.TrainManagementSystem.DTO.TrainClassDTO;
import com.railway.TrainManagementSystem.DTO.TrainRequestDTO;
import com.railway.TrainManagementSystem.DTO.TrainResponseDTO;
import com.railway.TrainManagementSystem.Service.TrainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/trains")
@RequiredArgsConstructor
public class TrainController {

    private final TrainService trainService;

    private static final Logger logger = LoggerFactory.getLogger(TrainController.class);

    /**
     * Get the list of all trains.
     *
     * @return list of TrainResponseDTO
     */
    @GetMapping
    public List<TrainResponseDTO> getAllTrains() {
        logger.info("Fetching all trains");
        return trainService.getAllTrains();
    }

    /**
     * Get train details by its ID.
     *
     * @param id train ID
     * @return TrainResponseDTO object
     */
    @GetMapping("/{id}")
    public TrainResponseDTO getTrainById(@PathVariable int id) {
        logger.info("Fetching train by id: {}", id);
        return trainService.getTrainById(id);
    }

    /**
     * Get train details by its number.
     *
     * @param trainNumber train number
     * @return TrainResponseDTO object
     */
    @GetMapping("/number/{trainNumber}")
    public TrainResponseDTO getTrainByNumber(@PathVariable String trainNumber) {
        logger.info("Fetching train by number: {}", trainNumber);
        return trainService.getTrainByNumber(trainNumber);
    }

    /**
     * Add a new train.
     *
     * @param dto TrainRequestDTO containing train details
     * @return created TrainResponseDTO object
     */
    @PostMapping
    public TrainResponseDTO addTrain(@RequestBody TrainRequestDTO dto) {
        logger.info("Adding new train: {}", dto);
        return trainService.addTrain(dto);
    }

    /**
     * Update existing train details by ID.
     *
     * @param id train ID to update
     * @param dto TrainRequestDTO containing updated details
     * @return updated TrainResponseDTO object
     */
    @PutMapping("/{id}")
    public TrainResponseDTO updateTrain(@PathVariable int id, @RequestBody TrainRequestDTO dto) {
        logger.info("Updating train with id: {}", id);
        return trainService.updateTrain(id, dto);
    }

    /**
     * Reduce available seats for a train class.
     *
     * @param trainNumber the train number
     * @param classType the class type (e.g., Sleeper, AC)
     * @param seats number of seats to reduce
     * @return ResponseEntity with success or failure message
     */
    @PutMapping("/reduceSeats")
    public ResponseEntity<String> reduceSeats(
            @RequestParam String trainNumber,
            @RequestParam String classType,
            @RequestParam int seats) {
        logger.info("Reducing {} seats for train {} and class {}", seats, trainNumber, classType);
        boolean success = trainService.reduceAvailableSeats(trainNumber, classType, seats);
        if (success) {
            logger.info("Seats reduced successfully");
            return ResponseEntity.ok("Seats reduced successfully.");
        } else {
            logger.warn("Failed to reduce seats");
            return ResponseEntity.badRequest().body("Failed to reduce seats.");
        }
    }

    /**
     * Delete a train by its ID.
     *
     * @param id train ID
     */
    @DeleteMapping("/{id}")
    public void deleteTrain(@PathVariable int id) {
        logger.info("Deleting train with id: {}", id);
        trainService.deleteTrain(id);
    }

    /**
     * Get train class details by class ID.
     *
     * @param classId class ID
     * @return TrainClassDTO object
     */
    @GetMapping("/id/{classId}")
    public TrainClassDTO getClassById(@PathVariable int classId) {
        logger.info("Fetching train class by id: {}", classId);
        return trainService.getTrainClassById(classId);
    }

    /**
     * Get train class by train ID and class type.
     *
     * @param trainId train ID
     * @param classType class type string
     * @return TrainClassDTO object
     */
    @GetMapping("/train/{trainId}/class-type/{classType}")
    public TrainClassDTO getTrainClassByTrainIdAndClassType(@PathVariable int trainId, @PathVariable String classType) {
        logger.info("Fetching train class for trainId: {} and classType: {}", trainId, classType);
        return trainService.getTrainClassByTrainIdAndClassType(trainId, classType);
    }
}
