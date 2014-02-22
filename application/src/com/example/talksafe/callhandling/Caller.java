package com.example.talksafe.callhandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

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

	@Override
	protected Result doInBackground(Void... params) {

		ApplicationState state = ApplicationState.getInstance();
		state.setBusy(true);
		publishProgress("Looking up user...");
		
		UserHandler handler = new UserHandler();
		try {
			
			Member toCall = handler.get(Member.phoneNumberToHash(phone));
			
			try {
				DatagramSocket callListener = new DatagramSocket(25566);
				DatagramSocket sender = new DatagramSocket();
				
				byte[] data = "hej".getBytes();
				InetAddress targetDevice = null;

				try {
					
					targetDevice = InetAddress.getByName(toCall.getIPAdress());
					
					Log.d("Connect", "target ip= " +targetDevice.toString());
					//sender.connect(targetDevice, 25565);

					DatagramPacket msg = new DatagramPacket(data,0, data.length,targetDevice,toCall.getPortNumber());

					

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
							publishProgress("Calling user...");
							callListener.close();
							return new Result(incoming, true);

						}catch(SocketTimeoutException e){
							
							e.printStackTrace();
							Log.d("timeout", "TIMEOUt");
							callListener.close();
							return new Result("The device did not respond", false);
							
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
