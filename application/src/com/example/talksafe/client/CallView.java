package com.example.talksafe.client;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

import com.example.talksafe.R;

public class CallView extends Activity {
	
	public static String PHONE_KEY = "phone";
	public static String NAME_KEY = "name";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_view);
		
		Intent intent = getIntent();
		String phone = intent.getStringExtra(PHONE_KEY);
		String name = intent.getStringExtra(NAME_KEY);
		
		((TextView) findViewById(R.id.name)).setText(name);
		
		((TextView) findViewById(R.id.phone)).setText(phone);
		
		((TextView) findViewById(R.id.status)).setText("Looking up user...");
		
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_view, menu);
		return true;
	}

}
