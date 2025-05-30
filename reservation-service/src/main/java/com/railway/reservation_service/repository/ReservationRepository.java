package com.railway.reservation_service.repository;

import com.railway.reservation_service.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository interface for Reservation entity.
 * Extends JpaRepository to provide CRUD and custom query methods.
 */
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    /**
     * Finds a reservation by its PNR (Passenger Name Record).
     *
     * @param pnr the PNR string to search
     * @return an Optional containing the Reservation if found, or empty if not found
     */
    Optional<Reservation> findByPnr(String pnr);
}
