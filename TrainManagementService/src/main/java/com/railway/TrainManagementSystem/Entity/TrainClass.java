package com.railway.TrainManagementSystem.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "train_class")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrainClass {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false, updatable = false, unique = true)
    private Integer classId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @Column(nullable = false)
    private String classType; // e.g., "Sleeper", "AC", etc.

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer availableSeats;

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    private String quota; // e.g., "General", "Ladies", etc.
}
