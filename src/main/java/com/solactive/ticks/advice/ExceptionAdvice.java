package com.solactive.ticks.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.solactive.ticks.entity.ExceptionResponse;
import com.solactive.ticks.exception.InvalidInstrumentException;
import com.solactive.ticks.exception.InvalidTickException;
import com.solactive.ticks.exception.TransactionTimestampException;
import com.solactive.ticks.utils.TicksConstant;

@RestControllerAdvice
public class ExceptionAdvice {

	@ExceptionHandler(TransactionTimestampException.class)
	public ResponseEntity<ExceptionResponse> mapException(TransactionTimestampException exception) {
		ExceptionResponse error = new ExceptionResponse(exception.getHttpStatus().value(),TicksConstant.OLDER_TXN_EXCEPTION);
		return new ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(InvalidTickException.class)
	public ResponseEntity<ExceptionResponse> mapException(InvalidTickException exception) {
		ExceptionResponse error = new ExceptionResponse(exception.getHttpStatus().value(),TicksConstant.PRICE_LESS_THAN_ZERO_MESSAGE);
		return new ResponseEntity(error, HttpStatus.NO_CONTENT);
	}

	@ExceptionHandler(InvalidInstrumentException.class)
	public ResponseEntity<ExceptionResponse> mapException(InvalidInstrumentException ex) {
		ExceptionResponse error = new ExceptionResponse(ex.getErrorCode(), TicksConstant.INSTRUMENT_NOT_FOUND);
		return new ResponseEntity<ExceptionResponse>(error, HttpStatus.NOT_FOUND);
	}

}