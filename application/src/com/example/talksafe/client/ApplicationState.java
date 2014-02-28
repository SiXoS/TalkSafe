package com.example.talksafe.client;

import java.nio.ByteBuffer;
import java.util.LinkedList;
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
	
	private LinkedList<byte[]> incomingEncrypted;
	private LinkedList<byte[]> outgoingEncrypted;
	
	private ApplicationState(){
		incomingEncrypted = new LinkedList<byte[]>();
		outgoingEncrypted = new LinkedList<byte[]>();
	}
	

	
	public void pushIncomingSound(byte[] sound){
		incLock.lock();
		
		incomingEncrypted.add(sound);
		incomingEncryptedHasContent.signal();
		
		incLock.unlock();
	}
	
	public byte[] pullIncomingSound(){
		incLock.lock();
		
		try{
			while(incomingEncrypted.isEmpty()) incomingEncryptedHasContent.await();
		}catch(Exception e){}
		
		byte[] bytes = incomingEncrypted.poll();
		
		incLock.unlock();
		return bytes;
	}
	
	public void pushOutgoingSound(byte[] sound){
		outLock.lock();
		
		outgoingEncrypted.add(sound);
		outgoingEncryptedHasContent.signal();
		
		outLock.unlock();
	}
	
	public byte[] pullOutgoingSound(){
		outLock.lock();
		
		try{
			while(!outgoingEncrypted.isEmpty()) outgoingEncryptedHasContent.await();
		}catch(Exception e){
			e.printStackTrace();
		}
		
		byte[] bytes = outgoingEncrypted.poll();
		
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
