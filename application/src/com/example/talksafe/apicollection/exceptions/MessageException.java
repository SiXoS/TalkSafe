package com.example.talksafe.apicollection.exceptions;

public class MessageException extends Exception {

	public MessageException(String detailMessage) {
		super(detailMessage);
	}

	public MessageException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
