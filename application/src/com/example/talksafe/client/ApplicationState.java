package com.example.talksafe.client;

import java.util.concurrent.Semaphore;

public class ApplicationState {
	
	private static ApplicationState instance = null;
	
	private boolean isBusy = false;
	
	private Semaphore incomingEncryptedHasContent = new Semaphore(0);
	private Semaphore outgoingEncryptedHasContent = new Semaphore(0);
	
	private byte[] incomingEncrypted;
	private byte[] outgoingEncrypted;
	
	private ApplicationState(){
		
	}
	
	public static synchronized ApplicationState getInstance(){
		if(instance != null)
			return instance;
		
		instance = new ApplicationState();
		return instance;
	}

	public boolean isBusy() {
		return isBusy;
	}

	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}

}
