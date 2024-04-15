package com.ust.pharmaAssistant.exception;

import lombok.Getter;

/**
 * Custom exception class representing business-related errors in the PharmaAssistant application.
 * This exception is used to handle various business logic exceptions and errors that occur during application execution.
 * It extends the RuntimeException class for unchecked exception handling.
 */
@Getter
public class PharmaBusinessException extends RuntimeException {

    /** Error code associated with the exception. */
    private final int errorCode;

    /** Error message associated with the exception. */
    private final String errorMessage;

    /**
     * Constructs a new PharmaBusinessException with the specified error code and error message.
     *
     * @param errorCode    The error code associated with the exception.
     * @param errorMessage The error message associated with the exception.
     */
    public PharmaBusinessException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
