package com.railway.reservation_service.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReservationRequestDTO {
    @NotBlank(message = "User name is required")
    private String userName;

    @NotBlank(message = "Train number is required")
    private String trainNumber;

    @NotBlank(message = "Train type is required")
    @Pattern(regexp = "General|Ladies", message = "Train type must be either 'General' or 'Ladies'")
    private String trainType;

    @NotBlank(message = "Class type is required")
    @Pattern(regexp = "AC|Sleeper", message = "Class type must be either 'AC' or 'Sleeper'")
    private String classType;

    @NotNull(message = "Journey date is required")
    private LocalDate journeyDate;

    @Min(value = 1, message = "At least one seat must be booked")
    @Max(value = 6, message = "You cannot book more than 6 seats at a time")
    private Integer numberOfSeats;

    @NotEmpty(message = "Passenger list cannot be empty")
    @Size(min = 1, max = 6, message = "You must provide details for 1 to 6 passengers")
    private List<PassengerDTO> passengers;
}
