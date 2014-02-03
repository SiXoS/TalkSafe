import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;

import javax.crypto.Cipher;



public class Main {

	public static void main(String[] args) {
		
		KeyPairGenerator gen = null;
		try{
			gen = KeyPairGenerator.getInstance("RSA");
		}catch(Exception e){ System.err.println("Felakig algoritm");}

		KeyPair keyPair = gen.generateKeyPair();
		RSAPrivateCrtKey priv = (RSAPrivateCrtKey)keyPair.getPrivate();
		RSAPublicKey pub = (RSAPublicKey)keyPair.getPublic();
		
		System.out.println("Modulus: " + priv.getModulus());
		System.out.println("#### Privata ####");
		System.out.println("privata exponenten: " + priv.getPrivateExponent());
		System.out.println("Primtal Q: " + priv.getPrimeQ());
		System.out.println("Primtal P: " + priv.getPrimeP());
		System.out.println("#### publika ####");
		System.out.println("publika exponenten: " + pub.getPublicExponent());
		
		//krypterar med publika nyckeln
		byte[] message = "Hej Simon.".getBytes();
		BigInteger mess = new BigInteger(message);
		System.out.println("okrypterad BigInt: " + mess); 
		mess = mess.modPow(pub.getPublicExponent(), pub.getModulus());
		System.out.println("Krypterad BigInt: " + mess);
		
		//Avkrypterar med privata nyckeln
		mess = mess.modPow(priv.getPrivateExponent(), priv.getModulus());
		System.out.println("Avkrypterat meddelande: " + new String(mess.toByteArray()));
		System.out.println();
		
		try{
			
			Cipher cipher = Cipher.getInstance("RSA");
			
			File sound = new File("F://java/Projekt kryptering/ljudtest.wma");
			FileReader fr = new FileReader(sound);
			int fragSize = 117;
			ArrayList<byte[]> encryptedFragments = new ArrayList<byte[]>((int)(sound.length()/fragSize)); //create an aproximation on number of BigIntegers required
			byte byt;
			byte[] buf = new byte[fragSize];
			int bufPos = 0;
			while((byt = (byte)fr.read()) != -1){
				buf[bufPos++] = byt;
				if(bufPos == fragSize){
					System.out.print("unencrypted bytes: ");
					for(byte b : buf) System.out.print(b);
					System.out.println();
					cipher.init(Cipher.ENCRYPT_MODE, pub);
					encryptedFragments.add(cipher.doFinal(buf));
					bufPos = 0;
				}
			}
			fr.close();
			
			cipher = Cipher.getInstance("RSA");
			
			File decryptedSound = new File("F://java/Projekt kryptering/ljudtest2.wma");
			decryptedSound.createNewFile();
			FileWriter fw = new FileWriter(decryptedSound);
			for(byte[] frag : encryptedFragments){
				cipher.init(Cipher.DECRYPT_MODE, priv);
				byte[] decrypted = cipher.doFinal();
				System.out.println("decrypted frag: ");
				for(byte b : decrypted) System.out.print(b);
				System.out.println();
				for(byte fragg : decrypted)
					fw.write(fragg);
			}
			fw.close();
			
		}catch(Exception e){
			System.err.println(e.getMessage());
		}
		
	}

}
