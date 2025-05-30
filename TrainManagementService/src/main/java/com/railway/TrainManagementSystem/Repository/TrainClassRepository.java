package com.railway.TrainManagementSystem.Repository;

import com.railway.TrainManagementSystem.Entity.TrainClass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Repository interface for TrainClass entity.
 * Provides methods to perform CRUD operations on TrainClass.
 */
public interface TrainClassRepository extends JpaRepository<TrainClass, Integer> {

    /**
     * Finds a TrainClass by the associated train's ID and the class type.
     *
     * @param trainId   the ID of the train
     * @param classType the type of the class (e.g., "Sleeper", "AC")
     * @return Optional containing the TrainClass if found, else empty
     */
    Optional<TrainClass> findByTrain_TrainIdAndClassType(int trainId, String classType);
}
