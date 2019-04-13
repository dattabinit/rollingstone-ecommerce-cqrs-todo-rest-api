package com.rollingstone.exception;

public class RestAPIExceptionInfo {

	private final String message;
	private final String cuase;
	
	public RestAPIExceptionInfo(String message, String cuase) {
		this.message = message;
		this.cuase = cuase;
	}
	
	public RestAPIExceptionInfo() {
		this.message = null;
		this.cuase = null;
	}

	public String getMessage() {
		return message;
	}

	public String getCuase() {
		return cuase;
	}
	
	
	
}
