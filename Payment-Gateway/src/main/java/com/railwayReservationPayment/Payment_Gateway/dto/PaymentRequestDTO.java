package com.railwayReservationPayment.Payment_Gateway.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PaymentRequestDTO {
    private String pnr;
    private String ticketName;
    private String currency;
    private long amount;
    private long quantity;
    private String successBaseUrl;
}

