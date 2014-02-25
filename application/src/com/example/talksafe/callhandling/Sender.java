package com.example.talksafe.callhandling;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;

import com.example.talksafe.client.ApplicationState;

import android.util.Log;

public class Sender extends Thread {

	private InetAddress ip;

	public Sender(InetAddress ip) {
		this.ip = ip;
	}

	@Override
	public void run() {
		DatagramSocket sender;
		try {
			sender = new DatagramSocket();
			Log.d("Connect", "target ip= " +ip.toString());

			ApplicationState state = ApplicationState.getInstance();
			while(!isInterrupted()) {
				byte[] data = state.pullOutgoingSound();				
				DatagramPacket msg = new DatagramPacket(data,0, data.length,ip,13337);

				try {
					Log.d("Sender", (sender==null) +"");
					sender.send(msg);
					
				} catch(Exception e) {
					Log.e("Sender SendException", e.getMessage());
				}
			}
			sender.close();
		} catch (SocketException e1) {			
			Log.e("Sender SocketException", e1.getMessage());
		}		
	}
}
