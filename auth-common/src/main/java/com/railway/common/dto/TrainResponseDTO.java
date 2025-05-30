package com.railway.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Time;
import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainResponseDTO {
    private int trainId;
    private String trainNumber;
    private String trainName;
    private String source;
    private String destination;
    private Time departureTime;
    private Time arrivalTime;
    private int runningDays;
    private boolean availability;
    private String trainType;
    private Date createdAt;
    private Date updatedAt;
}
