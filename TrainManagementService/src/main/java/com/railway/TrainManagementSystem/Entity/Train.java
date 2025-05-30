package com.railway.TrainManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "trains")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int trainId;

    @Column(nullable = false)
    private String trainName;

    @Column(unique = true, nullable = false)
    private String trainNumber;

    @Column(nullable = false)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private LocalDateTime departureTime;

    @Column(nullable = false)
    private LocalDateTime arrivalTime;

    @Column(nullable = false)
    private int runningDays;

    @Column(nullable = false)
    private boolean availability;

    private String trainType;

    @CreationTimestamp
    private LocalDateTime created_At;

    @UpdateTimestamp
    private LocalDateTime updated_At;

    @ToString.Exclude
    @OneToMany(mappedBy = "train", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<TrainClass> trainClasses = new ArrayList<>();
}
