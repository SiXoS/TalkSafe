package com.example.talksafe;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Button callButton = (Button) findViewById(R.id.call);
		
		callButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Thread hej = new Thread(new Call());
				hej.start();
				showCall();
			}
		});
		Button recieveButton = (Button) findViewById(R.id.recieveButton);
		recieveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				(new Thread(new Connection())).start();
				
			}
		});
//		while(!Global.callConnected);
//		
//		if(Global.callConnected){
//			callButton.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
//		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void showCall(){
		while(!Global.callConnected){
		}
		Log.d("Call","Call connected");
	}

}
