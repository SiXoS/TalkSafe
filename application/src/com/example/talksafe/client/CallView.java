package com.example.talksafe.client;

import java.net.InetAddress;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

import com.example.talksafe.R;
import com.example.talksafe.callhandling.Caller;
import com.example.talksafe.callhandling.Receiver;
import com.example.talksafe.callhandling.Sender;
import com.example.talksafe.callhandling.SoundFetcher;
import com.example.talksafe.callhandling.SoundPusher;
import com.example.talksafe.callhandling.util.Encrypter;

public class CallView extends Activity {
	
	public static String PHONE_KEY = "phone";
	public static String NAME_KEY = "name";
	
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_call_view);
		
		Intent intent = getIntent();
		String phone = intent.getStringExtra(PHONE_KEY);
		String name = intent.getStringExtra(NAME_KEY);
		
		((TextView) findViewById(R.id.name)).setText(name);
		
		((TextView) findViewById(R.id.phone)).setText(phone);
		
		((TextView) findViewById(R.id.status)).setText("Looking up user");
		Log.d("Innan call", "innan");
		Caller call = new Caller(phone,((TextView) findViewById(R.id.status)),this);
		call.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.call_view, menu);
		return true;
	}
	
	public static void startCall(Encrypter encrypter, Encrypter decrypter, InetAddress ip) {
		
		Sender sender = new Sender(ip);
		Receiver receiver = new Receiver();
		
		int intSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT); 
		AudioTrack player = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM);
		int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING);
		AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, bufferSize);
		
		SoundFetcher sf = new SoundFetcher(player, decrypter);
		SoundPusher sp = new SoundPusher(encrypter, recorder, bufferSize);
		
		sf.start();
		sp.start();
		sender.start();
		receiver.start();
	}

}
