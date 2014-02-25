package com.example.talksafe.callhandling;

import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.*;

import android.util.Log;

public class Encrypter{
	
	public PrivateKey privKey = null;
	public PublicKey pubKey = null;
	Cipher cipher;
	
	public RSAPublicKey init(){
		KeyPairGenerator kpg;
		try {
			
			kpg = KeyPairGenerator.getInstance("RSA");

			kpg.initialize(1024);
	
			KeyPair keyPair = kpg.generateKeyPair();
	
			privKey = keyPair.getPrivate();
			Log.d("Private key:" , "Modulus:{" + ((RSAPrivateKey)privKey).getModulus() + "}exp:{" + ((RSAPrivateKey)privKey).getPrivateExponent() + "}");
			pubKey = null;
			initDecrypt();
			return (RSAPublicKey)keyPair.getPublic();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void init(byte[] mod, byte[] exp){
		BigInteger modulus = new BigInteger(mod);
		BigInteger exponent = new BigInteger(exp);
		
		RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
		pubKey = null;
		privKey = null;
		try {
			
			KeyFactory factory = KeyFactory.getInstance("RSA");
			pubKey = factory.generatePublic(spec);
			Log.d("Public key:" , "Modulus:{" + ((RSAPublicKey)pubKey).getModulus() + "}exp:{" + ((RSAPublicKey)pubKey).getPublicExponent() + "}");
			initEncrypt();
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
	}
	
	private void initEncrypt(){
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
	
	public byte[] encrypt(byte [] b){
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

	private void initDecrypt(){
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
