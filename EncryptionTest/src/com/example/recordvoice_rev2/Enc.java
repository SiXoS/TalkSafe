package com.example.recordvoice_rev2;

import java.security.*;

import javax.crypto.*;

import android.util.Log;

public class Enc{
	public PrivateKey privKey;
	public PublicKey pubKey;
	Cipher cipher;
	public void init()throws Exception{
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
		
		kpg.initialize(1024);
		
		KeyPair keyPair = kpg.generateKeyPair();
		
		privKey = keyPair.getPrivate();
		pubKey = keyPair.getPublic();
	}
	public void initEncrypt(){
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	public byte[] encrpt(byte [] b){
		byte[] encrypted;
		try {
			Log.d("enc", "Encrypting in Enc");
			Log.d("tjo1",b.length + "");
			encrypted = cipher.doFinal(b);
			Log.d("tjo", encrypted.length + "");
			return encrypted;
		} catch (Exception e) {
			Log.d("enc", e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public void initDecrypt(){
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			cipher.init( Cipher.DECRYPT_MODE, privKey);
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		}
	}
	public byte[] decrypt(byte[] b){
		byte[] decrypted = null;
		try {
			Log.d("decr", "In dercrypt");
			decrypted = cipher.doFinal(b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return decrypted;
	}
}