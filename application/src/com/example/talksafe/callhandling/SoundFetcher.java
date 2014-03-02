package com.example.talksafe.callhandling;

import java.nio.ByteBuffer;

import com.example.talksafe.callhandling.util.Encrypter;
import com.example.talksafe.client.ApplicationState;

import android.media.AudioTrack;
import android.util.Log;

public class SoundFetcher extends Thread {

	private AudioTrack player;
	private Encrypter decrypter;
	
	public SoundFetcher(AudioTrack player, Encrypter decrypter) {
		this.player = player;
		this.decrypter = decrypter;
	}
	
	@Override
	public void run(){
		Log.d("hej", "tjo");
		
		int counter = 100000;
		boolean notWorking = true;
		
		while(notWorking){
			try{
				player.play();
				notWorking=false;
			}catch(IllegalStateException e){
				if(counter-- == 0){
					IllegalStateException er = new IllegalStateException(e.getMessage() + " Counter: " + counter,e.getCause());
					er.setStackTrace(e.getStackTrace());
					throw er;
				}
			}
		}
		Log.d("Testing play function", "times: " + counter);
		byte[] temp = new byte[115], temp2 = new byte[115];
		ByteBuffer buf = ByteBuffer.allocate(115*20);
		int n=0;
		ApplicationState state = ApplicationState.getInstance();
		while(!isInterrupted()) {
			temp = state.pullIncomingSound();
			buf.put(temp);
			//temp2 = decrypter.decrypt(temp);
			Log.d("SoundFetcher", "playing sound");
			n++;
			if(n == 9){
				Log.d("SoundFetcher", "playing sound");
				player.write(buf.array(), 0, buf.array().length);
				buf = (ByteBuffer)buf.clear();
				n=0;
			}
			try {
				sleep(1);
			} catch (InterruptedException e) {			}
		}
		player.stop();
		player.release();
	}

}
