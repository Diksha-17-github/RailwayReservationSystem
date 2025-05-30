package com.railway.TrainManagementSystem.DTO;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainClassDTO {
    private int classId;
    private int trainId;
    private String classType;
    private int totalSeats;
    private int availableSeats;
    private double price;
    private String quota;
}
