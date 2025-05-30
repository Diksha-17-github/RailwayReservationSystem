package com.railway.reservation_service.Exception;

/**
 * Custom exception to indicate that a requested resource was not found.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
