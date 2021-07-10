package com.solactive.ticks.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.solactive.ticks.entity.ExceptionResponse;
import com.solactive.ticks.exception.InvalidInstrumentException;
import com.solactive.ticks.exception.TransactionTimestampException;

@RestControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(TransactionTimestampException.class)
	public ResponseEntity<ExceptionResponse> mapException(TransactionTimestampException exception) {
		ExceptionResponse error = new ExceptionResponse(exception.getHttpStatus().value(),"Transaction is older then 60 seconds");
		return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(InvalidInstrumentException.class)
	public ResponseEntity<ExceptionResponse> mapException(InvalidInstrumentException ex) {
		ExceptionResponse error = new ExceptionResponse(ex.getErrorCode(), "There is no such instrument");
		return new ResponseEntity<ExceptionResponse>(error, HttpStatus.NOT_FOUND);
	}

}