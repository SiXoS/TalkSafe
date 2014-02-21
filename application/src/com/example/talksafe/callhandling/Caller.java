package com.example.talksafe.callhandling;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import android.os.AsyncTask;
import android.provider.Settings.Global;
import android.util.Log;
import android.widget.TextView;

import com.example.talksafe.apicollection.Member;
import com.example.talksafe.apicollection.UserHandler;
import com.example.talksafe.apicollection.exceptions.MessageException;
import com.example.talksafe.client.ApplicationState;

public class Caller extends AsyncTask<Void, String, Result> {
	
	private String phone;
	private TextView status;
	
	public Caller(String phone, TextView status){
		
		this.status = status;
		this.phone = phone;
		
	}

	@Override
	protected Result doInBackground(Void... params) {

		ApplicationState state = ApplicationState.getInstance();
		state.setBusy(true);
		
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

					//byte[] buf,int offset, int length ,InetAddress address, int port

					try {
						
						Log.d("Sender", (sender==null) +"");
						sender.send(msg);
						sender.close();
						
						byte[] payload = new byte[data.length];
						DatagramPacket incoming = new DatagramPacket(payload, data.length);	

						try {
							callListener.setSoTimeout(2000);
							callListener.receive(incoming);
							callListener.close();

						}catch(SocketTimeoutException e){
							
							e.printStackTrace();
							Log.d("timeout", "TIMEOUt");
							callListener.close();
							return new Result("The device did not respond", false);
							
						} catch (IOException e) {
							
							e.printStackTrace();
							callListener.close();
							
						}

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}catch(SocketException e) {
				e.printStackTrace();
			}
			
			
		} catch (MessageException e) {
			return new Result(e.getMessage(), false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	private void standardCatch(){
		ApplicationState state = ApplicationState.getInstance();
		state.setBusy(false);
	}

}
