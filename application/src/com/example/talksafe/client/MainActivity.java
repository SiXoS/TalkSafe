package com.example.talksafe.client;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.interfaces.RSAPublicKey;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;

import com.example.talksafe.R;
import com.example.talksafe.apicollection.Member;
import com.example.talksafe.apicollection.UserHandler;
import com.example.talksafe.apicollection.exceptions.MessageException;
import com.example.talksafe.callhandling.CallReciever;
import com.example.talksafe.callhandling.util.Encrypter;

public class MainActivity extends FragmentActivity {

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		(new CallReciever()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
		if(firstTime()) {
			AlertDialog.Builder alert = new AlertDialog.Builder(this);

			alert.setTitle("Phone number");
			alert.setMessage("Type your phone number to register to the app.");

			// Set an EditText view to get user input 
			final EditText input = new EditText(this);
			alert.setView(input);

			alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					String value = input.getText().toString();
					File folder = getFilesDir();
					File file = new File(folder,"registered.txt");
					try{
						file.createNewFile();
						FileWriter fw = new FileWriter(file);
						fw.write(value);
						fw.close();
					}catch(IOException e){
						e.printStackTrace();
					}
					(new Thread(new Registerer(value))).start();
				}
			});

			/*alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// Canceled.
				}
			});*/

			alert.show();
			// see http://androidsnippets.com/prompt-user-input-with-an-alertdialog
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private boolean firstTime() {
		File folder = getFilesDir();
		String[] files = folder.list();
		boolean found = false;
		for(String name : files)
			if(name.equals("registered.txt"))
				found = true;
		return !found;
	}

	
	private class Registerer implements Runnable{
		
		private String phone;
		
		public Registerer(String phone){ this.phone = phone;}
		
		@Override
		public void run(){
			String IpAddr = ""; 
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
							.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							IpAddr = inetAddress.getHostAddress().toString();
							// Filter out IPv6 addresses

							if (InetAddressUtils.isIPv6Address(IpAddr)) {
								IpAddr = null;
							}
						}
					}
				}

			} catch (SocketException se) {
				Log.e("GuC", "Error getting ip address");
			}
			Member member = new Member(phone, IpAddr, 25566);
			try {
				member.phoneNumberToHash();
			} catch (Exception e1) {
				Log.e("MEMBER fel", e1.getMessage());
			}
			UserHandler handler = new UserHandler();
			try {
				handler.add(member);
			} catch (MessageException e) {
				// TODO Auto-generated catch block
				Log.e("First time", e.getMessage());
			}
		}

	}
	
}
