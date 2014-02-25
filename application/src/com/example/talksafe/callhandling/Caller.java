package com.example.talksafe.callhandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.example.talksafe.apicollection.Member;
import com.example.talksafe.apicollection.UserHandler;
import com.example.talksafe.apicollection.exceptions.MessageException;
import com.example.talksafe.client.ApplicationState;
import com.example.talksafe.client.CallView;

public class Caller extends AsyncTask<Void, String, Result> {
	
	private String phone;
	private TextView status;
	private CallView activity;
	
	public Caller(String phone, TextView status, CallView activity){
		
		this.activity = activity;
		this.status = status;
		this.phone = phone;
		
	}

	@SuppressLint("NewApi")
	@Override
	protected Result doInBackground(Void... params) {

		ApplicationState state = ApplicationState.getInstance();
		state.setBusy(true);
		Encrypter enc = new Encrypter();
		RSAPublicKey key = enc.init();
		publishProgress("Looking up user...");
		
		UserHandler handler = new UserHandler();
		try {
			
			Member toCall = handler.get(Member.phoneNumberToHash(phone));
			
			try {
				DatagramSocket callListener = new DatagramSocket(25566);
				DatagramSocket sender = new DatagramSocket();
				
				byte[] mod = key.getModulus().toByteArray();
				byte[] exp = key.getPublicExponent().toByteArray();
				ByteBuffer buf = ByteBuffer.allocate(mod.length + exp.length);
				buf.put(mod); buf.put(exp);
				byte[] data = buf.array();
				
				InetAddress targetDevice = null;

				try {
					
					targetDevice = InetAddress.getByName(toCall.getIPAdress());
					
					Log.d("Connect", "target ip= " +targetDevice.toString());
					//sender.connect(targetDevice, 25565);

					DatagramPacket msg = new DatagramPacket(data,0, data.length,targetDevice,25565);

					

					try {
						
						Log.d("Sender", (sender==null) +"");
						publishProgress("Establishing connection...");
						sender.send(msg);
						sender.close();
						
						byte[] payload = new byte[data.length];
						DatagramPacket incoming = new DatagramPacket(payload, data.length);	

						try {
							callListener.setSoTimeout(2000);
							callListener.receive(incoming);
							
							byte[] modFromReceiver = Arrays.copyOfRange(incoming.getData(), 0, 8);;
							byte[] expFromReceiver = Arrays.copyOfRange(incoming.getData(), 8, incoming.getData().length);
							
							Encrypter dec = new Encrypter();
							dec.init(modFromReceiver, expFromReceiver);
							
							publishProgress("Calling user...");
							callListener.close();
							return new Result(enc, dec,incoming.getAddress());

						}catch(SocketTimeoutException e){
							
							e.printStackTrace();
							Log.d("timeout", "TIMEOUT");
							callListener.close();
							return new Result("The device did not respond", false, e.getMessage());
							
						} catch (IOException e) {
							
							e.printStackTrace();
							callListener.close();
							return new Result("", false, e.getMessage());
							
						}

					} catch (IOException e) {
						
						e.printStackTrace();
						callListener.close();
						sender.close();
						return new Result("", false, e.getMessage());
						
					}
					
				} catch (UnknownHostException e1) {

					e1.printStackTrace();
					callListener.close();
					sender.close();
					return new Result("", false, e1.getMessage());
					
				}
			
			}catch(SocketException e) {
				e.printStackTrace();
				return new Result("", false, e.getMessage());
			}
			
			
		} catch (MessageException e) {
			return new Result(e.getMessage(), false);
		} catch (Exception e) {

			e.printStackTrace();
			return new Result("", false, e.getMessage());
			
		}
		
	}
	
	@Override
	protected void onProgressUpdate(String... update){
		
		String statusText = update[0];
		status.setText(statusText);
		
	}
	
	@Override
	protected void onPostExecute(Result result){
		Log.d("Result Caller", result.toString());
		if(result.isSuccess()){
			status.setText("Success!");
		}else{
			Log.d("Caller failed", result.getFatal());
			status.setText(result.getMessage());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			activity.finish();
		}
	}

}
