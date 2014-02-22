package com.example.talksafe.callhandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import com.example.talksafe.client.ApplicationState;

import android.os.AsyncTask;

public class CallReciever extends AsyncTask<Integer, Result, Void> {
	
	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 1024;

	@Override
	protected Void doInBackground(Integer... params) {
		
		while(!isCancelled()){
			try{
				callListener = new DatagramSocket(params[0]);
				sender = new DatagramSocket();
				
				byte[] payload = new byte[bufferSize];
				DatagramPacket incoming = new DatagramPacket(payload, bufferSize);	
				
				try {
					callListener.receive(incoming);
					
					if(isCancelled()){
						callListener.close();
						return null;
					}
					
					ApplicationState state = ApplicationState.getInstance();
					byte[] data;
					Result result;
					if(state.isBusy()){
						data = "Busy".getBytes();
						result = new Result("", false, "User was busy.");
					}else{
						data = incoming.getData();
						result = new Result(params[0], incoming.getPort(), incoming.getAddress(), incoming.getData());
					}
					
					try {
						InetAddress targetDevice = incoming.getAddress();
						DatagramPacket toSend = new DatagramPacket(data,data.length, targetDevice,incoming.getPort());
						sender.send(toSend);
					} catch (UnknownHostException e) {
						e.printStackTrace();
						publishProgress(new Result("", false, e.getMessage()));
					} catch (IOException e) {
						e.printStackTrace();
						publishProgress(new Result("", false, e.getMessage()));
					}	
					
				} catch (IOException e) {
					e.printStackTrace();
					publishProgress(new Result("", false, e.getMessage()));
				}		
				
			}catch(SocketException e){
				e.printStackTrace();
				publishProgress(new Result("", false, e.getMessage()));
			}
		}
		return null;
		
	}
	
	@Override
	protected void onProgressUpdate(Result... results){
		
	}

}
