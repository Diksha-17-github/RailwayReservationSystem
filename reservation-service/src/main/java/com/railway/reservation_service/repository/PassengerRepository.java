package com.railway.reservation_service.repository;

import com.railway.reservation_service.entity.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for Passenger entity.
 * Extends JpaRepository to provide CRUD operations.
 */
public interface PassengerRepository extends JpaRepository<Passenger, Integer> {

    /**
     * Finds all passengers associated with a specific reservation ID.
     *
     * @param reservationId the ID of the reservation
     * @return list of passengers belonging to the reservation
     */
    List<Passenger> findByReservation_ReservationId(int reservationId);
}
