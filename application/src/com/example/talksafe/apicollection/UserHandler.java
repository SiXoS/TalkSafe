package com.example.talksafe.apicollection;

import java.net.ConnectException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.talksafe.apicollection.exceptions.MessageException;

import android.util.Log;

/**
 * 
 * 
 * 
 * @author Simon Lindhén
 * @version 2014-02-12 (YYYY-MM-DD)
 *
 */
public class UserHandler extends APIHandler {
	
	private final String URL = "http://taksejf.appspot.com/api";
	
	public UserHandler(){
		setURL(URL);
	}
	
	/**
	 * 
	 * Gets the member with the specified hashed phone number.
	 * 
	 * @param hashedPhoneNumber The phone number hashed with Member.phoneNumberToHash()
	 * @return A Member object containing the hashed phone number and contact information. May be null on error.
	 * @throws MessageException Contains a user friendly error from the database
	 */
	public Member get(String hashedPhoneNumber) throws MessageException{
		bindParam("get",hashedPhoneNumber);
		
		try {
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler - get")){
				if(json.has("item")){
					JSONObject jsonMember = json.getJSONObject("item");
					Member member = new Member(jsonMember.getString("phone"), jsonMember.getString("IP"), jsonMember.getInt("port"));
					return member;
				}else
					return null;
				
			}else{
				Log.d("UserHandler - get",getError());
				return null;
			}
			
		}catch(MessageException e){
			throw e;
		}catch (Exception e) {
			Log.e("UserHandler - get : ", e.getMessage());
			return null;
		}
	}
	
	/**
	 * 
	 * Signals the database that the user is still online. Should be called once a minute.
	 * 
	 * @param hashedPhoneNumber
	 * @return
	 * @throws MessageException Contains a user friendly error from the database
	 */
	public boolean update(String hashedPhoneNumber) throws MessageException{
		bindParam("update", hashedPhoneNumber);
		
		try{
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler - update")){
				return true;
			}else
				return false;
		}catch(MessageException e){
			throw e;
		}catch(Exception e){
			Log.d("UserHandler - update", e.getMessage());
			return false;
		}
	}
	
	/**
	 * 
	 * Adds the member to the database, the phone number must have been hashed.
	 * 
	 * @param member
	 * @return true if the member was added, else false
	 * @throws MessageException Contains a user friendly error from the database
	 */
	public boolean add(Member member) throws MessageException{
		
		bindParam("add","yes");
		bindParam("phone",member.getPhone());
		bindParam("IP", member.getIPAdress());
		bindParam("port", member.getPortNumber() + "");
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json,"UserHandler - add"))
				return true;
			else
				return false;
			
		}catch(MessageException e){
			throw e;
		}catch(Exception e){
			Log.e("UserHandler - add : ", e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * 
	 * Changes the members IP-address and port number corresponding to its phone number.
	 * 
	 * @param member 
	 * @return
	 * @throws MessageException Contains a user friendly error from the database
	 */
	public boolean change(Member member) throws MessageException{
		
		bindParam("editIP",member.getPhone());
		bindParam("IP", member.getIPAdress());
		bindParam("port", member.getPortNumber() + "");
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler change"))
				return true;
			else
				return false;
			
		}catch(MessageException e){
			throw e;
		}catch(Exception e){
			Log.e("UserHandler - change : ", e.getMessage());
			return false;
		}
		
	}
	
	/**
	 * 
	 * Deletes the specified user.
	 * 
	 * @param phone hashed phone number to be deleted
	 * @return true on success, else false
	 * @throws MessageException Contains a user friendly error from the database
	 */
	public boolean delete(String phone) throws MessageException{
		
		bindParam("delete", phone);
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler - change : ")){
				return true;
			}
				return false;
			
		}catch(MessageException e){
			throw e;
		}catch(Exception e){
			Log.e("UserHandler - delete : ", e.getMessage());
			return false;
		}
		
	}

}
