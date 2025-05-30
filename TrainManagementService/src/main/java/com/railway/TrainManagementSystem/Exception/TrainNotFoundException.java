package com.railway.TrainManagementSystem.Exception;

/**
 * Custom exception to indicate that a requested Train was not found.
 */
public class TrainNotFoundException extends RuntimeException {
    public TrainNotFoundException(String message) {
        super(message);
    }
}
