package com.example.talksafe.network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class Connection implements Runnable {

	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 128;
	private boolean isBusy = false;
	
	private Connection(int callPort, int listenPort) {
		try {
			callListener = new DatagramSocket(listenPort);
			sender = new DatagramSocket(callPort);
		}
		catch(SocketException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		
		byte[] payload = new byte[bufferSize];
		DatagramPacket incoming = new DatagramPacket(payload, bufferSize);	
		try {
			callListener.receive(incoming);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		isBusy = true;
		byte[] data = new byte[bufferSize];
		data = incoming.getData();
		
		try {
			InetAddress targetDevice = InetAddress.getByAddress(data);
			sender.connect(targetDevice, sender.getPort());
			sender.send(new DatagramPacket(new byte[bufferSize], bufferSize));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
	}
	
}
