package com.example.talksafe.client;

import java.nio.ByteBuffer;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ApplicationState {
	
	private static ApplicationState instance = null;
	
	private boolean isBusy = false;
	
	private ReentrantLock outLock = new ReentrantLock();
	private ReentrantLock incLock = new ReentrantLock();
	
	private Condition incomingEncryptedHasContent = incLock.newCondition();
	private Condition outgoingEncryptedHasContent = outLock.newCondition();
	
	private ByteBuffer incomingEncrypted;
	private ByteBuffer outgoingEncrypted;
	
	private ApplicationState(){
		incomingEncrypted = ByteBuffer.allocate(1024);
		outgoingEncrypted = ByteBuffer.allocate(1024);
	}
	

	
	public void pushIncomingSound(byte[] sound){
		incLock.lock();
		
		incomingEncrypted.put(sound);
		incomingEncryptedHasContent.signal();
		
		incLock.unlock();
	}
	
	public byte[] pullIncomingSound(){
		incLock.lock();
		
		try{
			while(incomingEncrypted.position() != 0) incomingEncryptedHasContent.await();
		}catch(Exception e){}
		
		byte[] bytes = incomingEncrypted.array();
		incomingEncrypted = (ByteBuffer)incomingEncrypted.clear();
		
		incLock.unlock();
		return bytes;
	}
	
	public void pushOutgoingSound(byte[] sound){
		outLock.lock();
		
		outgoingEncrypted.put(sound);
		outgoingEncryptedHasContent.signal();
		
		outLock.unlock();
	}
	
	public byte[] pullOutgoingSound(){
		outLock.lock();
		
		try{
			while(outgoingEncrypted.position() != 0) outgoingEncryptedHasContent.await();
		}catch(Exception e){}
		
		byte[] bytes = outgoingEncrypted.array();
		outgoingEncrypted = (ByteBuffer)outgoingEncrypted.clear();
		
		outLock.unlock();
		return bytes;
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
