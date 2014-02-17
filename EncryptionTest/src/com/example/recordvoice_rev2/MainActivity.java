package com.example.recordvoice_rev2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	private AudioRecord recorder = null;
	private Thread recordingThread = null;
	private boolean isRecording = false;
	private Enc encrypter;
	private AudioTrack player = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setButtonHandlers();
		enableButtons(false);
		encrypter = new Enc();
		try {
			encrypter.init();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
				RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING); 
		
		int intSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT); 
		player = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STATIC); 
		
	}

	private void setButtonHandlers() {
		((Button) findViewById(R.id.btnStart)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnStop)).setOnClickListener(btnClick);
		((Button) findViewById(R.id.btnplay)).setOnClickListener(btnClick);
	}

	private void enableButton(int id, boolean isEnable) {
		((Button) findViewById(id)).setEnabled(isEnable);
	}

	private void enableButtons(boolean isRecording) {
		enableButton(R.id.btnStart, !isRecording);
		enableButton(R.id.btnStop, isRecording);
	}

	int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
	int BytesPerElement = 2; // 2 bytes in 16bit format

	private void startRecording() {

		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

		recorder.startRecording();
		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	//convert short to byte
	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];
		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;

	}
	
	private short[] byteToShort(byte[] data){
		short[] shorts = new short[data.length/2];
		for(int i = 0 ; i<data.length ; i+=2){
			shorts[i/2] = (short) ((((short)data[i]) << 8) | ((short) data[i+1]));
		}
		return shorts;
	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte

		String filePath = "/sdcard/voice8K16bitmono.pcm";
		short sData[] = new short[BufferElements2Rec];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		encrypter.initEncrypt();

		while (isRecording) {
			// gets the voice output from microphone to byte format

			recorder.read(sData, 0, BufferElements2Rec);
			System.out.println("Short wirting to file" + sData.toString());
			try {
				// // writes the data to file from buffer
				// // stores the voice buffer
				byte bData[] = short2byte(sData);
				float part = bData.length/(float)115;
				Log.d("part", part + "");
				float part2 = part*128;
				Log.d("part2", part2 + "");
				byte [] finalData = new byte[((int)Math.ceil(part2))*2];
				byte[] tmp = new byte[115];
				byte[] tmp2;
				int n=0,q=0;
				
				for(byte i : bData){
					tmp [n++]=i;
					if(n == 115){
						Log.d("enc", "Encrypting in Main");
						Log.d("enc null", encrypter==null ? "den e null" : "inte null");
						
						tmp2 = encrypter.encrpt(tmp);
						n=0;
						for(byte x : tmp2){
							finalData[q] = x;
							q++;
						}
					}
					
				}
				if(n>0){
					Log.d("enc", "Encrypting outside loop");
					tmp = encrypter.encrpt(tmp);
					n=0;
					for(byte x : tmp){
						finalData[q] = x;
						q++;
					}
				}
				Log.d("enc", "Before write");
				os.write(finalData, 0, BufferElements2Rec * BytesPerElement);
				Log.d("enc", "After write");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecording() {
		// stops the recording activity
		if (null != recorder) {
			isRecording = false;
			recorder.stop();
			recorder.release();
			recorder = null;
			recordingThread = null;
		}
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btnStart: {
				enableButtons(true);
				startRecording();
				break;
			}
			case R.id.btnStop: {
				enableButtons(false);
				stopRecording();
				break;
			}
			case R.id.btnplay:{
				try {
					playTrack();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void playTrack() throws IOException{
		String encrypted = "/sdcard/voice8K16bitmono.pcm";
		String decrypted = "/sdcard/voice8K16bitmonodec.pcm";
		
		File encr = new File(encrypted);
		FileInputStream reader = new FileInputStream(encr);
		byte[] contents = new byte[(int) encr.length()];
		reader.read(contents);
		reader.close();
		
		encrypter.initDecrypt();
		int counter = 100000;
		boolean notWorking = true;
		while(notWorking){
			try{
				player.play();
				notWorking=false;
			}catch(IllegalStateException e){
				if(counter-- == 0){
					IllegalStateException er = new IllegalStateException(e.getMessage() + " Counter: " + counter,e.getCause());
					er.setStackTrace(e.getStackTrace());
					throw er;
				}
			}
		}
		Log.d("Testing play function", "times: " + counter);
		
		int n = 0, m=0;
		byte[] newContents = new byte[contents.length];
		byte[] temp = new byte[128], temp2 = new byte[115];
		for(byte b : contents){
			temp[n++] = b;
			if(n==128){
				temp2 = encrypter.decrypt(temp);
				player.write(byteToShort(temp2), 0, temp2.length);
				for(byte x : temp2){
					newContents[m++] = x;
				}
				n=0;
			}
		}
		player.stop();
		player.release();
		/*File decr = new File(decrypted);
		if(!decr.exists())
			decr.createNewFile();
		FileOutputStream out = new FileOutputStream(decr);
		out.write(newContents);
		out.close();
		PlayShortAudioFileViaAudioTrack(decrypted);*/
	}
	
	private void PlayShortAudioFileViaAudioTrack(String filePath) throws IOException
	{
		// We keep temporarily filePath globally as we have only two sample sounds now..
		if (filePath==null)
			return;

		//Reading the file..
		byte[] byteData = null; 
		File file = null; 
		file = new File(filePath); // for ex. path= "/sdcard/samplesound.pcm" or "/sdcard/samplesound.wav"
		byteData = new byte[(int) file.length()];
		FileInputStream in = null;
		try {
			in = new FileInputStream( file );
			in.read( byteData );
			in.close(); 

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Set and push to audio track..
		int intSize = android.media.AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT); 
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO,
				AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STATIC); 
		if (at!=null) { 
			at.play();
			// Write the byte array to the track
			at.write(byteData, 0, byteData.length); 
			at.stop();
			at.release();
		}
		else
			Log.d("TCAudio", "audio track is not initialised ");

	}
}