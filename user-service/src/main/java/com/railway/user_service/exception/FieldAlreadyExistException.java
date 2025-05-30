package com.railway.user_service.exception;

/**
 * Exception thrown when attempting to create or update
 * a field that must be unique but already exists.
 */
public class FieldAlreadyExistException extends RuntimeException {

    /**
     * Constructs a new FieldAlreadyExistException with the specified detail message.
     *
     * @param message the detail message
     */
    public FieldAlreadyExistException(String message) {
        super(message);
    }
}
