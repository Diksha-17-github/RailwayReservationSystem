package com.railway.reservation_service.service;

import com.railway.common.dto.TrainClassDTO;
import com.railway.common.dto.TrainResponseDTO;
import com.railway.common.dto.UserResponseDTO;
import com.railway.reservation_service.Exception.ResourceNotFoundException;
import com.railway.reservation_service.dto.PassengerDTO;
import com.railway.reservation_service.dto.ReservationRequestDTO;
import com.railway.reservation_service.dto.ReservationResponseDTO;
import com.railway.reservation_service.entity.BookingStatus;
import com.railway.reservation_service.entity.Passenger;
import com.railway.reservation_service.entity.Reservation;
import com.railway.reservation_service.feignClients.TrainClient;
import com.railway.reservation_service.feignClients.UserClient;
import com.railway.reservation_service.repository.ReservationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements IReservationService {

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceImpl.class);

    private final ReservationRepository reservationRepository;
    private final UserClient userClient;
    private final TrainClient trainClient;
    private final EmailService emailService;

    public ReservationServiceImpl(ReservationRepository reservationRepository,
                                  UserClient userClient,
                                  TrainClient trainClient,
                                  EmailService emailService) {
        this.reservationRepository = reservationRepository;
        this.userClient = userClient;
        this.trainClient = trainClient;
        this.emailService = emailService;
    }

    @Override
    public ReservationResponseDTO createReservation(ReservationRequestDTO request) {
        logger.info("Creating reservation for user: {} on train: {}", request.getUserName(), request.getTrainNumber());

        // 1. Get user by userName
        UserResponseDTO userDTO = userClient.getUserByUserName(request.getUserName()).getBody();
        if (userDTO == null || !userDTO.getUserName().equals(request.getUserName())) {
            logger.error("User not found with username: {}", request.getUserName());
            throw new ResourceNotFoundException("User not found with username: " + request.getUserName());
        }
        logger.debug("User found: {}", userDTO.getUserName());

        // 2. Get train by Number
        TrainResponseDTO trainDTO = trainClient.getTrainByNumber(request.getTrainNumber()).getBody();
        if (trainDTO == null || !trainDTO.getTrainNumber().equals(request.getTrainNumber())) {
            logger.error("Train not found with number: {}", request.getTrainNumber());
            throw new ResourceNotFoundException("Train not found with number: " + request.getTrainNumber());
        }
        logger.debug("Train found: {} - {}", trainDTO.getTrainNumber(), trainDTO.getTrainName());

        // 3. Get class info
        TrainClassDTO classDTO = trainClient
                .getTrainClassByTrainIdAndClassType(trainDTO.getTrainId(), request.getClassType())
                .getBody();

        if (classDTO == null || !classDTO.getClassType().equalsIgnoreCase(request.getClassType())) {
            logger.error("Class type {} not available for train {}", request.getClassType(), request.getTrainNumber());
            throw new ResourceNotFoundException("Class type " + request.getClassType() +
                    " not available for train " + request.getTrainNumber());
        }
        logger.debug("Class type found: {}", classDTO.getClassType());

        // 4. Check seat availability
        int requestedSeats = request.getPassengers().size();
        if (classDTO.getAvailableSeats() < requestedSeats) {
            logger.error("Insufficient seats: requested {}, available {}", requestedSeats, classDTO.getAvailableSeats());
            throw new IllegalStateException("Only " + classDTO.getAvailableSeats() + " seats available.");
        }
        logger.info("Seats available: {}. Proceeding with booking.", classDTO.getAvailableSeats());

        // 5. Generate PNR
        String pnr = generatePNR();
        logger.info("Generated PNR: {}", pnr);

        // 6. Create reservation entity
        Reservation reservation = Reservation.builder()
                .userName(userDTO.getUserName())
                .trainId(trainDTO.getTrainId())
                .classId(classDTO.getClassId())
                .journeyDate(request.getJourneyDate())
                .seatCount(requestedSeats)
                .bookingStatus(BookingStatus.PENDING)
                .pnr(pnr)
                .passengers(
                        request.getPassengers().stream()
                                .map(p -> Passenger.builder()
                                        .name(p.getName())
                                        .age(p.getAge())
                                        .gender(p.getGender())
                                        .address(p.getAddress())
                                        .reservation(null) // will set below
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();

        // Link passengers with reservation
        reservation.getPassengers().forEach(p -> p.setReservation(reservation));

        // 7. Save to database
        Reservation saved = reservationRepository.save(reservation);
        logger.info("Reservation saved with ID: {}", saved.getReservationId());

        // ---- Send booking confirmation email here ----
        emailService.sendBookingConfirmation(
                userDTO.getEmail(),
                userDTO.getUserName(),
                saved.getPnr()
        );

        // 8. Build and return response DTO
        return ReservationResponseDTO.builder()
                .reservationId(saved.getReservationId())
                .pnrNumber(saved.getPnr())
                .trainNumber(trainDTO.getTrainNumber())
                .trainName(trainDTO.getTrainName())
                .classType(classDTO.getClassType())
                .username(userDTO.getUserName())
                .journeyDate(saved.getJourneyDate())
                .numberOfSeats(saved.getSeatCount())
                .totalFare(getPriceByQuota(classDTO.getQuota()) * saved.getSeatCount())
                .reservationStatus(saved.getBookingStatus().toString())
                .reservationTime(saved.getCreatedAt())
                .passengers(
                        saved.getPassengers().stream()
                                .map(p -> PassengerDTO.builder()
                                        .name(p.getName())
                                        .age(p.getAge())
                                        .gender(p.getGender())
                                        .address(p.getAddress())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public ReservationResponseDTO getReservationByPNR(String pnr) {
        logger.info("Fetching reservation details for PNR: {}", pnr);

        Reservation reservation = reservationRepository.findByPnr(pnr)
                .orElseThrow(() -> {
                    logger.error("Reservation not found for PNR: {}", pnr);
                    return new ResourceNotFoundException("Reservation not found with PNR: " + pnr);
                });

        TrainResponseDTO trainDTO = trainClient.getTrainById(reservation.getTrainId()).getBody();
        TrainClassDTO classDTO = trainClient.getClassById(reservation.getClassId()).getBody();

        // Fetch user email
        UserResponseDTO userDTO = userClient.getUserByUserName(reservation.getUserName()).getBody();

        return ReservationResponseDTO.builder()
                .reservationId(reservation.getReservationId())
                .pnrNumber(reservation.getPnr())
                .trainNumber(trainDTO.getTrainNumber())
                .trainName(trainDTO.getTrainName())
                .classType(classDTO.getClassType())
                .username(reservation.getUserName())
                .email(userDTO != null ? userDTO.getEmail() : "")  // Include email
                .journeyDate(reservation.getJourneyDate())
                .numberOfSeats(reservation.getSeatCount())
                .totalFare(classDTO.getPrice() * reservation.getSeatCount())
                .reservationStatus(reservation.getBookingStatus().toString())
                .reservationTime(reservation.getCreatedAt())
                .passengers(
                        reservation.getPassengers().stream()
                                .map(p -> PassengerDTO.builder()
                                        .name(p.getName())
                                        .age(p.getAge())
                                        .gender(p.getGender())
                                        .address(p.getAddress())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public String deleteReservation(int reservationId) {
        logger.info("Deleting reservation with ID: {}", reservationId);
        reservationRepository.deleteById(reservationId);
        logger.info("Reservation deleted with ID: {}", reservationId);
        return "Reservation with ID " + reservationId + " deleted successfully.";
    }

    private double getPriceByQuota(String quota) {
        if ("Ladies".equalsIgnoreCase(quota)) {
            return 800.0;
        } else {
            return 1000.0;
        }
    }

    @Override
    public ReservationResponseDTO updateReservationStatus(String pnr) {
        logger.info("Updating reservation status to CONFIRMED for PNR: {}", pnr);

        Reservation reservation = reservationRepository.findByPnr(pnr)
                .orElseThrow(() -> {
                    logger.error("PNR not found: {}", pnr);
                    return new RuntimeException("PNR not found");
                });

        reservation.setBookingStatus(BookingStatus.CONFIRMED);
        reservationRepository.save(reservation);

        TrainResponseDTO trainDTO = trainClient.getTrainById(reservation.getTrainId()).getBody();
        TrainClassDTO classDTO = trainClient.getClassById(reservation.getClassId()).getBody();

        return ReservationResponseDTO.builder()
                .reservationId(reservation.getReservationId())
                .pnrNumber(reservation.getPnr())
                .trainNumber(trainDTO.getTrainNumber())
                .trainName(trainDTO.getTrainName())
                .classType(classDTO.getClassType())
                .username(reservation.getUserName())
                .journeyDate(reservation.getJourneyDate())
                .numberOfSeats(reservation.getSeatCount())
                .totalFare(classDTO.getPrice() * reservation.getSeatCount())
                .reservationStatus(reservation.getBookingStatus().toString())
                .reservationTime(reservation.getCreatedAt())
                .passengers(
                        reservation.getPassengers().stream()
                                .map(p -> PassengerDTO.builder()
                                        .name(p.getName())
                                        .age(p.getAge())
                                        .gender(p.getGender())
                                        .address(p.getAddress())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }

    @Override
    public void updateStatusByPNR(String pnr) {
        logger.info("Updating status to CONFIRMED for reservation with PNR: {}", pnr);

        Reservation reservation = reservationRepository.findByPnr(pnr)
                .orElseThrow(() -> {
                    logger.error("Reservation not found with PNR: {}", pnr);
                    return new ResourceNotFoundException("Reservation not found with PNR: " + pnr);
                });

        reservation.setBookingStatus(BookingStatus.CONFIRMED);
        reservationRepository.save(reservation);

        logger.info("Status updated to CONFIRMED for PNR: {}", pnr);
    }

    // Utility method to generate a random 6 character alphanumeric PNR
    private String generatePNR() {
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = (int) (Math.random() * alphanumeric.length());
            sb.append(alphanumeric.charAt(index));
        }
        return sb.toString();
    }

    @Override
    public ReservationResponseDTO cancelReservation(String pnr) {
        logger.warn("Cancel reservation method called but not implemented yet.");
        throw new UnsupportedOperationException("Cancel reservation not implemented yet.");
    }

    @Override
    public void sendBookingConfirmation(String toEmail, String userName, String pnr) {

        logger.info("Sending booking confirmation email to: {}", toEmail);
        logger.info("Hello {}, your booking is confirmed. Your PNR is {}.", userName, pnr);
    }
}
