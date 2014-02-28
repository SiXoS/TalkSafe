package com.example.talksafe.callhandling;

import java.io.IOException;
import java.nio.ByteBuffer;

import com.example.talksafe.callhandling.util.Encrypter;
import com.example.talksafe.client.ApplicationState;

import android.media.AudioRecord;
import android.util.Log;

public class SoundPusher extends Thread {
	
	private Encrypter encrypter;
	private AudioRecord recorder;
	private int bufferSize;

	public SoundPusher(Encrypter encrypter, AudioRecord recorder, int bufferSize){

		this.encrypter = encrypter;
		this.recorder = recorder;
		this.bufferSize = bufferSize;

	}

	@Override
	public void run() {
		
		ApplicationState state = ApplicationState.getInstance();
		
		while(!interrupted()){
		
			byte[] data = new byte[115];
			recorder.read(data, 0, 115);
			System.out.println("Short flirting to file" + data.toString());
	
			// // writes the data to file from buffer
			// // stores the voice buffer
			byte[] tmp = new byte[128];
	
			Log.d("enc", "Encrypting in Main");
			Log.d("enc null", encrypter==null ? "den e null" : "inte null");

			//tmp = encrypter.encrypt(data);
			state.pushOutgoingSound(data);
			
		}

	}

}
