package com.railway.common.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainClassDTO {
    private int classId;
    private int trainId;
    private String classType; // e.g., "Sleeper", "AC", etc.
    private int totalSeats;
    private int availableSeats;
    private double price; // Previously 'price'
    private String quota; // Add this if quota-based booking is supported
}