package com.railway.TrainManagementSystem.Repository;

import com.railway.TrainManagementSystem.Entity.Train;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Train entity.
 * Provides methods to perform CRUD operations and custom queries on Train.
 */
public interface TrainRepository extends JpaRepository<Train, Integer> {

    /**
     * Finds a list of trains by source and destination.
     *
     * @param source      the starting location of the train
     * @param destination the destination location of the train
     * @return list of trains matching the source and destination
     */
    List<Train> findBySourceAndDestination(String source, String destination);

    /**
     * Finds a train by its unique train number.
     *
     * @param trainNumber the unique train number
     * @return optional containing the train if found, else empty
     */
    Optional<Train> findByTrainNumber(String trainNumber);
}
