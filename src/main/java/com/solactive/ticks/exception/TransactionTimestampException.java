package com.solactive.ticks.exception;

import org.springframework.http.HttpStatus;

/**
 * Exception for invalid values of transaction timestamp
 */
public class TransactionTimestampException extends RuntimeException {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;

    public TransactionTimestampException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
