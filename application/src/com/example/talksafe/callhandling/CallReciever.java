package com.example.talksafe.callhandling;

import java.io.IOException;
import java.math.BigInteger;
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
import android.util.Log;

import com.example.talksafe.callhandling.util.Encrypter;
import com.example.talksafe.callhandling.util.Result;
import com.example.talksafe.client.ApplicationState;
import com.example.talksafe.client.CallView;

public class CallReciever extends AsyncTask<Integer, Result, Void> {
	
	private DatagramSocket callListener;
	private DatagramSocket sender;
	
	private final int bufferSize = 128;

	@SuppressLint("NewApi")
	@Override
	protected Void doInBackground(Integer... params) {
		
		while(!isCancelled()){
			Log.d("Lyssnar", "jajjemen");
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				return null;
			}
			try{
				callListener = new DatagramSocket(25565);
				sender = new DatagramSocket();
				
				byte[] payload = new byte[bufferSize];
				DatagramPacket incoming = new DatagramPacket(payload, bufferSize);	
				
				try {
					callListener.receive(incoming);
					
					if(isCancelled()){
						callListener.close();
						return null;
					}
				
					Encrypter decrypt = new Encrypter();
					RSAPublicKey key = decrypt.init();
					
					byte[] mod = incoming.getData();
					byte[] exp = key.getPublicExponent().toByteArray();
					
					BigInteger shit = new BigInteger(exp);
					Log.d("BigInteger", shit.toString());
					
					Encrypter enc = new Encrypter();
					enc.init(mod, exp);
					
					ApplicationState state = ApplicationState.getInstance();
					byte[] data;
					Result result;
					if(state.isBusy()){
						data = "Busy".getBytes();
						result = new Result("", false, "User was busy.");
						Log.d("Busy", "wtf");
					}else{
						byte[] modForSender = key.getModulus().toByteArray();
						byte[] expForSender = key.getPublicExponent().toByteArray();
						ByteBuffer buf = ByteBuffer.allocate(modForSender.length);
						buf.put(modForSender);
						data = buf.array();
						Log.d("Buffer", buf.toString());
						result = new Result(enc, decrypt, incoming.getAddress());
					}
					
					try {
						InetAddress targetDevice = incoming.getAddress();
						Log.d("SKICKa TILLBAKA", "target="+targetDevice);
						DatagramPacket toSend = new DatagramPacket(data,data.length, targetDevice,25566);
						sender.send(toSend);
						callListener.close();
						publishProgress(result);
					} catch (UnknownHostException e) {
						e.printStackTrace();
						callListener.close();
						publishProgress(new Result("", false, e.getMessage()));
						callListener.close();
					} catch (IOException e) {
						e.printStackTrace();
						publishProgress(new Result("", false, e.getMessage()));
						callListener.close();
					}	
					
					
				} catch (IOException e) {
					callListener.close();
					e.printStackTrace();
					publishProgress(new Result("", false, e.getMessage()));
				}		
				
			}catch(SocketException e){
				callListener.close();
				e.printStackTrace();
				publishProgress(new Result("", false, e.getMessage()));
			}
		}
		return null;
		
	}
	
	@Override
	protected void onProgressUpdate(Result... results){
		Result result = results[0];
		if(result.isSuccess()){
			CallView.startCall(result.getEnc(), result.getDec(), result.getIp());
		}
	}

}
