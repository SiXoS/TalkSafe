package apicollection;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.util.Log;

public class Member {
	
	private String phone;
	private String IPAdress;
	private int portNumber;
	
	/**
	 * 
	 * Creates a new object with the specified details. The phone number is not hashed, but should be so before leaving this unit.
	 * 
	 * @param phone
	 * @param IPAdress
	 * @param portNumber
	 */
	public Member(String phone, String IPAdress, int portNumber){
		this.phone = phone;
		this.IPAdress = IPAdress;
		this.portNumber = portNumber;
	}
	
	public Member clone(){
		return new Member(phone,IPAdress, portNumber);
	}
	
	@Override
	public boolean equals(Object other){
		
		if(other == null)
			return false;
		
		if(!(other instanceof Member))
			return false;
		
		Member mem = (Member)other;
		if(mem.getPhone().equals(getPhone()) && mem.getIPAdress().equals(getIPAdress()) && mem.getPortNumber() == getPortNumber())
			return true;
		
		return false;
		
	}

	
	/**
	 * 
	 * Encrypts the phone number using MD5 hashing, this is irreversible. 
	 * 
	 * <b>NOTE!! Use the non-static function for encrypting an objects phone number, this is vital.</b>
	 * 
	 * @param phone The phone number to hash
	 * @return A string with the encrypted String
	 * @throws Exception If an error occurred during hashing.
	 */
	public static String phoneNumberToHash(String phone) throws Exception{
		
		try{
			MessageDigest digester = MessageDigest.getInstance("MD5");
			byte[] bytes = digester.digest(phone.getBytes());
			return new String(bytes);
		}catch(NoSuchAlgorithmException e){
			Log.e("Member - phoneNumberToHash", "No such algorithm raised.");
			throw new Exception("The encryption failed");
		}
	}
	
	/**
	 * 
	 * Encrypts the phone number using MD5 hashing. shortcut for the same static function, but not the same, always use this when encrypting an objects phoneNumber.
	 * 
	 * @return A string with the encrypted String
	 * @throws Exception If an error occurred during hashing.
	 */
	public String phoneNumberToHash() throws Exception{
		return phoneNumberToHash(phone);
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getIPAdress() {
		return IPAdress;
	}

	public void setIPAdress(String iPAdress) {
		IPAdress = iPAdress;
	}

	public int getPortNumber() {
		return portNumber;
	}

	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}

}
