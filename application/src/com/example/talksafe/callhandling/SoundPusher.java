package com.example.talksafe.callhandling;

import java.io.IOException;
import java.nio.ByteBuffer;

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
		
			byte[] data = new byte[bufferSize];
			recorder.read(data, 0, bufferSize);
			System.out.println("Short wirting to file" + data.toString());
	
			// // writes the data to file from buffer
			// // stores the voice buffer
			float part = data.length/(float)115;
			Log.d("part", part + "");
			float part2 = part*128;
			Log.d("part2", part2 + "");
			byte[] tmp = new byte[115];
			byte[] tmp2;
			int n=0,q=0;
	
			for(byte i : data){
				tmp [n++]=i;
				if(n == 115){
					Log.d("enc", "Encrypting in Main");
					Log.d("enc null", encrypter==null ? "den e null" : "inte null");
	
					tmp2 = encrypter.encrypt(tmp);
					n=0;
					state.pushOutgoingSound(tmp2);
				}
	
			}
			if(n>0){
				Log.d("enc", "Encrypting outside loop");
				tmp2 = encrypter.encrypt(tmp);
				n=0;
				state.pushOutgoingSound(tmp2);
			}
			
		}

	}

}
