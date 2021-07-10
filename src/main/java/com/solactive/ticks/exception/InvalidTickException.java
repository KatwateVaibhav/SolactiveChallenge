package com.solactive.ticks.exception;

import org.springframework.http.HttpStatus;


public class InvalidTickException extends RuntimeException {


	private static final long serialVersionUID = 1L;
	private final HttpStatus httpStatus;

    public InvalidTickException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

}
