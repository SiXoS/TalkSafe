package com.example.talksafe.apicollection.exceptions;

public class FatalException extends Exception {

	public FatalException(String message) {
		super(message);
	}

	public FatalException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
