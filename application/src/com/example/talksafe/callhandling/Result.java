package com.example.talksafe.callhandling;

import java.net.DatagramPacket;

public class Result{
	
	private String message;
	private boolean success;
	private String fatal;
	private DatagramPacket response;
	
	public Result(DatagramPacket response, boolean success){
		setResponse(response);
	}
	
	public Result(String message, boolean success){
		setMessage(message);
		setSuccess(success);
	}

	public Result(String message, boolean success, String fatal){
		setMessage(message);
		setSuccess(success);
		setFatal(fatal);
	}
	
	public String getFatal() {
		return fatal;
	}

	public void setFatal(String fatal) {
		this.fatal = fatal;
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

	public DatagramPacket getResponse() {
		return response;
	}

	public void setResponse(DatagramPacket response) {
		this.response = response;
	}
	
}
