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
import com.example.talksafe.callhandling.util.Encrypter;
import com.example.talksafe.callhandling.util.Result;
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
		Log.d("inne i call", "japp");
		ApplicationState state = ApplicationState.getInstance();
		//state.setBusy(true);
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
				//byte[] exp = key.getPublicExponent().toByteArray();
				ByteBuffer buf = ByteBuffer.allocate(mod.length);
				buf.put(mod);
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
							callListener.setSoTimeout(5000);
							callListener.receive(incoming);
							
							byte[] modFromReceiver = incoming.getData();
							byte[] expFromReceiver = key.getPublicExponent().toByteArray();
							ByteBuffer b = ByteBuffer.allocate(modFromReceiver.length);
							b.put(modFromReceiver);
							Log.d("Modulus p� t�g", b.toString());
							
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
			CallView.startCall(result.getEnc(), result.getDec(), result.getIp());
		}else{
			Log.d("Caller failed", result.getFatal() + " ");
			status.setText(result.getMessage());
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {}
			activity.finish();
		}
	}

}
