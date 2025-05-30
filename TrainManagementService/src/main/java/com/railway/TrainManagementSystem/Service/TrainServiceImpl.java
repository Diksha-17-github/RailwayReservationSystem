package com.railway.TrainManagementSystem.Service;

import com.railway.TrainManagementSystem.DTO.TrainClassDTO;
import com.railway.TrainManagementSystem.DTO.TrainRequestDTO;
import com.railway.TrainManagementSystem.DTO.TrainResponseDTO;
import com.railway.TrainManagementSystem.Entity.Train;
import com.railway.TrainManagementSystem.Entity.TrainClass;
import com.railway.TrainManagementSystem.Exception.TrainNotFoundException;
import com.railway.TrainManagementSystem.Repository.TrainRepository;
import com.railway.TrainManagementSystem.Repository.TrainClassRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrainServiceImpl implements TrainService {

    private static final Logger logger = LoggerFactory.getLogger(TrainServiceImpl.class);

    private final TrainRepository trainRepository;
    private final TrainClassRepository trainClassRepository;

    @Override
    public List<TrainResponseDTO> getAllTrains() {
        logger.info("Fetching all trains");
        return trainRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainResponseDTO getTrainById(int id) {
        logger.info("Fetching train with ID {}", id);
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Train with ID {} not found", id);
                    return new TrainNotFoundException("Train with ID " + id + " not found.");
                });
        return mapToResponseDTO(train);
    }

    @Override
    public List<TrainResponseDTO> searchTrains(String source, String destination) {
        logger.info("Searching trains from {} to {}", source, destination);
        return trainRepository.findBySourceAndDestination(source, destination)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TrainResponseDTO addTrain(TrainRequestDTO trainDTO) {
        logger.info("Adding new train: {}", trainDTO.getTrainNumber());
        Train train = mapToEntity(trainDTO);
        train.setCreated_At(LocalDateTime.now());
        train.setUpdated_At(LocalDateTime.now());
        Train savedTrain = trainRepository.save(train);
        logger.info("Train {} added successfully with ID {}", savedTrain.getTrainNumber(), savedTrain.getTrainId());
        return mapToResponseDTO(savedTrain);
    }

    @Override
    public TrainResponseDTO updateTrain(int id, TrainRequestDTO trainDTO) {
        logger.info("Updating train with ID {}", id);
        Train existingTrain = trainRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Train with ID {} not found for update", id);
                    return new TrainNotFoundException("Train with ID " + id + " not found.");
                });

        existingTrain.setTrainNumber(trainDTO.getTrainNumber());
        existingTrain.setTrainName(trainDTO.getTrainName());
        existingTrain.setSource(trainDTO.getSource());
        existingTrain.setDestination(trainDTO.getDestination());
        existingTrain.setDepartureTime(trainDTO.getDepartureTime().toLocalTime().atDate(LocalDate.now()));
        existingTrain.setArrivalTime(trainDTO.getArrivalTime().toLocalTime().atDate(LocalDate.now()));
        existingTrain.setRunningDays(trainDTO.getRunningDays());
        existingTrain.setAvailability(trainDTO.isAvailability());
        existingTrain.setTrainType(trainDTO.getTrainType());
        existingTrain.setUpdated_At(LocalDateTime.now());

        Train updatedTrain = trainRepository.save(existingTrain);
        logger.info("Train with ID {} updated successfully", id);
        return mapToResponseDTO(updatedTrain);
    }

    @Override
    public void deleteTrain(int id) {
        logger.info("Deleting train with ID {}", id);
        trainRepository.deleteById(id);
        logger.info("Train with ID {} deleted", id);
    }

    @Override
    public TrainClassDTO getTrainClassByTrainIdAndClassType(int trainId, String classType) {
        logger.info("Fetching train class for train ID {} and class type {}", trainId, classType);
        return trainClassRepository.findByTrain_TrainIdAndClassType(trainId, classType)
                .map(this::mapToTrainClassDTO)
                .orElseThrow(() -> {
                    logger.error("Train class not found for train ID {} and class type {}", trainId, classType);
                    return new RuntimeException("Train class not found for train ID " + trainId + " and class type " + classType);
                });
    }

    @Override
    public TrainClassDTO getTrainClassById(int classId) {
        logger.info("Fetching train class with ID {}", classId);
        return trainClassRepository.findById(classId)
                .map(this::mapToTrainClassDTO)
                .orElseThrow(() -> {
                    logger.error("Train class with ID {} not found", classId);
                    return new RuntimeException("Train class with ID " + classId + " not found.");
                });
    }

    @Override
    public TrainResponseDTO getTrainByNumber(String trainNumber) {
        logger.info("Fetching train by number {}", trainNumber);
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> {
                    logger.error("Train with number {} not found", trainNumber);
                    return new TrainNotFoundException("Train with number " + trainNumber + " not found.");
                });
        return mapToResponseDTO(train);
    }

    @Override
    public boolean reduceAvailableSeats(String trainNumber, String classType, int seatsToReduce) {
        logger.info("Reducing {} seats from train {}, class {}", seatsToReduce, trainNumber, classType);
        Train train = trainRepository.findByTrainNumber(trainNumber)
                .orElseThrow(() -> {
                    logger.error("Train not found: {}", trainNumber);
                    return new IllegalArgumentException("Train not found");
                });

        TrainClass trainClass = trainClassRepository.findByTrain_TrainIdAndClassType(train.getTrainId(), classType)
                .orElseThrow(() -> {
                    logger.error("Train class not found for type: {}", classType);
                    return new IllegalArgumentException("Train class not found for type: " + classType);
                });

        if (trainClass.getAvailableSeats() < seatsToReduce) {
            logger.error("Not enough seats available: requested {}, available {}", seatsToReduce, trainClass.getAvailableSeats());
            throw new IllegalArgumentException("Not enough seats available.");
        }

        trainClass.setAvailableSeats(trainClass.getAvailableSeats() - seatsToReduce);
        trainClassRepository.save(trainClass);
        logger.info("Seats reduced successfully. Remaining seats: {}", trainClass.getAvailableSeats());
        return true;
    }

    // Mapping helper for TrainClass entity to DTO
    private TrainClassDTO mapToTrainClassDTO(TrainClass trainClass) {
        return TrainClassDTO.builder()
                .classId(trainClass.getClassId())
                .classType(trainClass.getClassType())
                .price(trainClass.getPrice())
                .availableSeats(trainClass.getAvailableSeats())
                .quota(trainClass.getQuota())
                .trainId(trainClass.getTrain().getTrainId())
                .build();
    }

    // Mapping helper for Train entity to response DTO
    private TrainResponseDTO mapToResponseDTO(Train train) {
        return TrainResponseDTO.builder()
                .trainId(train.getTrainId())
                .trainNumber(train.getTrainNumber())
                .trainName(train.getTrainName())
                .source(train.getSource())
                .destination(train.getDestination())
                .departureTime(Time.valueOf(train.getDepartureTime().toLocalTime()))
                .arrivalTime(Time.valueOf(train.getArrivalTime().toLocalTime()))
                .runningDays(train.getRunningDays())
                .availability(train.isAvailability())
                .trainType(train.getTrainType())
                .createdAt(java.sql.Date.valueOf(train.getCreated_At().toLocalDate()))
                .updatedAt(java.sql.Date.valueOf(train.getUpdated_At().toLocalDate()))
                .build();
    }

    // Mapping helper for TrainRequestDTO to Train entity
    private Train mapToEntity(TrainRequestDTO dto) {
        return Train.builder()
                .trainNumber(dto.getTrainNumber())
                .trainName(dto.getTrainName())
                .source(dto.getSource())
                .destination(dto.getDestination())
                .departureTime(dto.getDepartureTime().toLocalTime().atDate(LocalDate.now()))
                .arrivalTime(dto.getArrivalTime().toLocalTime().atDate(LocalDate.now()))
                .runningDays(dto.getRunningDays())
                .availability(dto.isAvailability())
                .trainType(dto.getTrainType())
                .build();
    }
}
