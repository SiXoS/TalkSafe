package com.example.talksafe.callhandling;

import java.net.DatagramPacket;
import java.net.InetAddress;

public class Result{
	
	private String message;
	private boolean success;
	private String fatal;
	private DatagramPacket response;
	private byte[] publicKey;
	private InetAddress ip;
	private int receivePort;
	private int callPort;
	
	public Result(DatagramPacket response, boolean success){
		setResponse(response);
	}
	
	public Result(int callPort, int receivePort, InetAddress ip, byte[] publicKey){
		this.callPort = callPort;
		this.receivePort = receivePort;
		this.ip = ip;
		this.publicKey = publicKey;
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
	
	public byte[] getPublicKey() {
		return publicKey;
	}

	public void setPublicKey(byte[] publicKey) {
		this.publicKey = publicKey;
	}

	public InetAddress getIp() {
		return ip;
	}

	public void setIp(InetAddress ip) {
		this.ip = ip;
	}

	public int getReceivePort() {
		return receivePort;
	}

	public void setReceivePort(int receivePort) {
		this.receivePort = receivePort;
	}

	public int getCallPort() {
		return callPort;
	}

	public void setCallPort(int callPort) {
		this.callPort = callPort;
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
