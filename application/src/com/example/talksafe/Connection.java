package com.example.talksafe;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import android.util.Log;

public class Connection implements Runnable {

	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 128;
	private boolean isBusy = false;
	
	public Connection() {
		try {
			callListener = new DatagramSocket(25565);
		}
		catch(SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		//set up incoming packet
		byte[] payload = new byte[bufferSize];
		DatagramPacket incoming = new DatagramPacket(payload, bufferSize);	
		try {
			callListener.receive(incoming);
			callListener.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//Call recieved, set user busy
		isBusy = true;
		byte[] data = new byte[bufferSize];
		data = incoming.getData();
		
		
		Log.d("recieved", new String(data));
		try {
			sender = new DatagramSocket();
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			sender.send(new DatagramPacket(data,0, data.length,incoming.getAddress(), 25566));
			sender.close();
			Global.callConnected =true;
			Log.d("Address", incoming.getAddress()+"");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
