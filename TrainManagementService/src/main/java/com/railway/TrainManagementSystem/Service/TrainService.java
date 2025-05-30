package com.railway.TrainManagementSystem.Service;

import com.railway.TrainManagementSystem.DTO.TrainClassDTO;
import com.railway.TrainManagementSystem.DTO.TrainRequestDTO;
import com.railway.TrainManagementSystem.DTO.TrainResponseDTO;

import java.util.List;

/**
 * Service interface for managing trains and their classes.
 */
public interface TrainService {

    /**
     * Retrieves all trains.
     *
     * @return list of all TrainResponseDTO
     */
    List<TrainResponseDTO> getAllTrains();

    /**
     * Retrieves train details by train ID.
     *
     * @param id the train ID
     * @return TrainResponseDTO of the specified train
     */
    TrainResponseDTO getTrainById(int id);

    /**
     * Searches trains by source and destination.
     *
     * @param source      the source location
     * @param destination the destination location
     * @return list of trains matching the source and destination
     */
    List<TrainResponseDTO> searchTrains(String source, String destination);

    /**
     * Adds a new train.
     *
     * @param trainDTO the train request DTO containing train details
     * @return the added TrainResponseDTO
     */
    TrainResponseDTO addTrain(TrainRequestDTO trainDTO);

    /**
     * Updates an existing train.
     *
     * @param id       the ID of the train to update
     * @param trainDTO the updated train details
     * @return the updated TrainResponseDTO
     */
    TrainResponseDTO updateTrain(int id, TrainRequestDTO trainDTO);

    /**
     * Deletes a train by ID.
     *
     * @param id the ID of the train to delete
     */
    void deleteTrain(int id);

    /**
     * Gets train class details by train ID and class type.
     *
     * @param trainId   the train ID
     * @param classType the class type (e.g., "Sleeper", "AC")
     * @return TrainClassDTO matching the train ID and class type
     */
    TrainClassDTO getTrainClassByTrainIdAndClassType(int trainId, String classType);

    /**
     * Gets train class details by class ID.
     *
     * @param classId the class ID
     * @return TrainClassDTO of the class
     */
    TrainClassDTO getTrainClassById(int classId);

    /**
     * Gets train details by train number.
     *
     * @param trainNumber the unique train number
     * @return TrainResponseDTO matching the train number
     */
    TrainResponseDTO getTrainByNumber(String trainNumber);

    /**
     * Reduces available seats for a particular train class.
     *
     * @param trainNumber the train number
     * @param classType   the class type
     * @param seats       the number of seats to reduce
     * @return true if seats were reduced successfully, false otherwise
     */
    boolean reduceAvailableSeats(String trainNumber, String classType, int seats);
}
