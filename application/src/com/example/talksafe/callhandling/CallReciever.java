package com.example.talksafe.callhandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.example.talksafe.client.ApplicationState;

public class CallReciever extends AsyncTask<Integer, Result, Void> {
	
	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 1024;

	@SuppressLint("NewApi")
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
				
					byte[] mod = Arrays.copyOfRange(incoming.getData(), 0, 8);;
					byte[] exp = Arrays.copyOfRange(incoming.getData(), 8, incoming.getData().length);
					
					Encrypter enc = new Encrypter();
					enc.init(mod, exp);
					
					Encrypter decrypt = new Encrypter();
					RSAPublicKey key = decrypt.init();
					
					ApplicationState state = ApplicationState.getInstance();
					byte[] data;
					Result result;
					if(state.isBusy()){
						data = "Busy".getBytes();
						result = new Result("", false, "User was busy.");
					}else{
						byte[] modForSender = key.getModulus().toByteArray();
						byte[] expForSender = key.getPublicExponent().toByteArray();
						ByteBuffer buf = ByteBuffer.allocate(mod.length + exp.length);
						buf.put(modForSender); buf.put(expForSender);
						data = buf.array();
						
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
