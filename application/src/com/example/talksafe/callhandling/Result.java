package com.example.talksafe.callhandling;

public class Result{
	
	private String message;
	private boolean success;
	
	public Result(String message, boolean success){
		setMessage(message);
		setSuccess(success);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}
	
}
