package com.railway.TrainManagementSystem.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainSearchDTO {
    private String source;
    private String destination;
    private String trainType;
}
