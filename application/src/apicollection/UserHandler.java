package apicollection;

import java.net.ConnectException;

import org.json.JSONException;
import org.json.JSONObject;

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
	 * @return A Member object containing the hashed phone number and contact information.
	 * @throws ConnectException
	 * @throws JSONException
	 */
	public Member get(String hashedPhoneNumber) throws ConnectException, JSONException{
		bindParam("get",hashedPhoneNumber);
		
		try {
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler - get","Could not fetch the member")){
				if(json.has("member")){
					JSONObject jsonMember = json.getJSONObject("member");
					Member member = new Member(jsonMember.getString("phone"), jsonMember.getString("IP"), jsonMember.getInt("port"));
					return member;
				}else
					return null;
				
			}else
				return null;
			
		} catch (ConnectException e) {
			Log.e("UserHandler - get : ", e.getMessage());
			throw e;
		}catch(JSONException e){
			Log.e("UserHandler - get : ", e.getMessage());
			throw e;
		}
	}
	
	/**
	 * 
	 * Adds the member to the database, the phone number must have been hashed.
	 * 
	 * @param member
	 * @return true if the member was added, else false
	 * @throws ConnectException If there was on error connecting to the server
	 */
	public boolean add(Member member) throws ConnectException{
		
		bindParam("add","yes");
		bindParam("phone",member.getPhone());
		bindParam("IP", member.getIPAdress());
		bindParam("port", member.getPortNumber() + "");
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json,"UserHandler - add", "Could not add the member."))
				return true;
			else
				return false;
			
		}catch(ConnectException e){
			Log.e("UserHandler - add : ", e.getMessage());
			throw e;
		}
		
	}
	
	/**
	 * 
	 * Changes the members IP-address and port number corresponding to its phone number.
	 * 
	 * @param member 
	 * @return
	 * @throws ConnectException If there was on error connecting to the server
	 */
	public boolean change(Member member) throws ConnectException{
		
		bindParam("editIP",member.getPhone());
		bindParam("IP", member.getIPAdress());
		bindParam("port", member.getPortNumber() + "");
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler change", "Could not edit the member"))
				return true;
			else
				return false;
			
		}catch(ConnectException e){
			Log.e("UserHandler - change : ", e.getMessage());
			throw e;
		}
		
	}
	
	/**
	 * 
	 * Deletes the specified user.
	 * 
	 * @param phone hashed phone number to be deleted
	 * @return true on success, else false
	 * @throws ConnectException If there was on error connecting to the server
	 */
	public boolean delete(String phone) throws ConnectException{
		
		bindParam("delete", phone);
		
		try{
			
			JSONObject json = execute();
			if(jsonSuccess(json, "UserHandler - change : ", "Could not delete the member")){
				return true;
			}
				return false;
			
		}catch(ConnectException e){
			Log.e("UserHandler - delete : ", e.getMessage());
			throw e;
		}
		
	}

}
