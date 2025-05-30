package com.railwayReservationPayment.Payment_Gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder

public class PaymentResponseDTO {
    public String status;
    public String message;
    public String sessionId;
    public String sessionUrl;
}