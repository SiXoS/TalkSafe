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
		Log.d("hej", "tjo");
		DatagramSocket sender;
		try {
			sender = new DatagramSocket();

			ApplicationState state = ApplicationState.getInstance();
			while(!isInterrupted()) {
				byte[] data = state.pullOutgoingSound();			
				Log.d("Jävla mor", (data==null)  + "");
				DatagramPacket msg = new DatagramPacket(data,0, data.length,ip,13337);

				try {
					sender.send(msg);
					Log.d("Sender", "sending sound");
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
