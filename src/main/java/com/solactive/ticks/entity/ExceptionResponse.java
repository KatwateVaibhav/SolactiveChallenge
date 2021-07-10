package com.solactive.ticks.entity;

public class ExceptionResponse {
	private int errorCode;
	private String errorMessage;
	public ExceptionResponse(int errorCode, String errorMessage) {
		super();
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	public int getErrorCode() {
		return errorCode;
	}
	
	public String getErrorMessage() {
		return errorMessage;
	}
	

}
