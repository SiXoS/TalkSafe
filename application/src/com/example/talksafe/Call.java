package com.example.talksafe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeoutException;

import android.util.Log;

public class Call implements Runnable {

	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 128;
	private boolean isBusy = false;
	
//	public Call() {
//		try {
//			callListener = new DatagramSocket(25565);
//			sender = new DatagramSocket();
//		}
//		catch(SocketException e) {
//			e.printStackTrace();
//		}
//	}
	
	@Override
	public void run() {
		try {
			callListener = new DatagramSocket(25566);
			sender = new DatagramSocket();
		}
		catch(SocketException e) {
			e.printStackTrace();
		}
		isBusy = true;
		byte[] data = new byte[bufferSize];
		data = "hej".getBytes();
		InetAddress targetDevice = null;
		
		try {
			targetDevice = InetAddress.getByName("192.168.1.10");
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Log.d("Connect", "target ip= " +targetDevice.toString());
		//sender.connect(targetDevice, 25565);
		
		DatagramPacket msg = new DatagramPacket(data,0, 3,targetDevice,25565);
		
		//byte[] buf,int offset, int length ,InetAddress address, int port
		
		try {
			Log.d("Sender", (sender==null) +"");
			sender.send(msg);
			sender.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		byte[] payload = new byte[bufferSize];
		DatagramPacket incoming = new DatagramPacket(payload, bufferSize);	
		
		try {
			callListener.setSoTimeout(1000);
			callListener.receive(incoming);
			callListener.close();
			
			//sets call true
			Global.callConnected =true;
		}catch(SocketTimeoutException e){
			e.printStackTrace();
			Log.d("timeout", "TIMEOUt");
			callListener.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			callListener.close();
		}
		Log.d("callListener", "Is bound: "+callListener.isBound()
				+"\nIs Closed: " + callListener.isClosed() 
				+"\nIs Connected: "+callListener.isConnected());
		
	}
	
}