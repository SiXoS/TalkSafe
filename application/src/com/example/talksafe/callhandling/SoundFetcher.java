package com.example.talksafe.callhandling;

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
		byte[] temp = new byte[128], temp2 = new byte[115];
		ApplicationState state = ApplicationState.getInstance();
		while(!isInterrupted()) {
			temp = state.pullIncomingSound();
			temp2 = decrypter.decrypt(temp);
			player.write(temp2, 0, temp2.length);
		}
		player.stop();
		player.release();
	}

}
