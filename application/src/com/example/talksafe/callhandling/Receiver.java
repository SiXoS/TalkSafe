package com.example.talksafe.callhandling;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import com.example.talksafe.client.ApplicationState;

import android.util.Log;

public class Receiver extends Thread {

	Encrypter decrypter;

	public Receiver(Encrypter decrypter) {
		this.decrypter = decrypter;
	}

	@Override
	public void run() {
		try {
			DatagramSocket callListener = new DatagramSocket(13337);

			byte[] payload = new byte[128];
			DatagramPacket incoming = new DatagramPacket(payload, payload.length);	

			ApplicationState state = ApplicationState.getInstance();
			while(!isInterrupted()) {

				try {
					callListener.receive(incoming);
					byte[] data = decrypter.decrypt(incoming.getData());
					state.pushIncomingSound(data);
				} catch (Exception e) {
					Log.e("Receiver error1", e.getMessage());
				}
			}
		} catch (Exception e) {
			Log.e("Receiver error2", e.getMessage());
		}
	}
}
