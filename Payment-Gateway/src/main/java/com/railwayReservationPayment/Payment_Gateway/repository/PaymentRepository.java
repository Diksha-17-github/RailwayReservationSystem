package com.railwayReservationPayment.Payment_Gateway.repository;

import com.railwayReservationPayment.Payment_Gateway.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for Payment entity to handle database operations.
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
