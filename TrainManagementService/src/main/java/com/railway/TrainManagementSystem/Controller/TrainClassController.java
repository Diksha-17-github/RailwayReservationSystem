package com.railway.TrainManagementSystem.Controller;

import com.railway.TrainManagementSystem.DTO.TrainClassDTO;
import com.railway.TrainManagementSystem.Service.TrainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/trainClasses")
@RequiredArgsConstructor
public class TrainClassController {

    private final TrainService trainService;

    private static final Logger logger = LoggerFactory.getLogger(TrainClassController.class);

    /**
     * Get train class details by class ID.
     *
     * @param classId the ID of the train class
     * @return the TrainClassDTO object
     */
    @GetMapping("/id/{classId}")
    public TrainClassDTO getClassById(@PathVariable int classId) {
        logger.info("Fetching train class by id: {}", classId);
        return trainService.getTrainClassById(classId);
    }

    /**
     * Get train class details by train ID and class type.
     *
     * @param trainId the train ID
     * @param classType the class type (e.g., "Sleeper", "AC")
     * @return the TrainClassDTO object
     */
    @GetMapping("/train/{trainId}/class-type/{classType}")
    public TrainClassDTO getTrainClassByTrainIdAndClassType(@PathVariable int trainId, @PathVariable String classType) {
        logger.info("Fetching train class for trainId: {} and classType: {}", trainId, classType);
        return trainService.getTrainClassByTrainIdAndClassType(trainId, classType);
    }
}
