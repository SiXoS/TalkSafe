package apicollection;

import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



/**
 * APIHandler.java
 *
 * @author Jimmy Svensson
 * 
 */
public class APIHandler {

	private List<NameValuePair> params = new ArrayList<NameValuePair>();
	private String URL;
	// JSON Response node names
	private String KEY_SUCCESS = "success";
	private String KEY_ERROR_MESSAGE = "error";

	/**
	 * Sets the URL address
	 * @param URLadr The URL address to the APIHandler
	 */
	public void setURL(String URLadr){
		URL=URLadr;
	}

	/**
	 * Binds the parameters to an ArrayList of NameValuePair
	 * @param key A description of what the value refer to
	 * @param value The value which will linked to the key
	 */
	public void bindParam(String key, String value){
		params.add(new BasicNameValuePair(key, value));
	}

	/**
	 * Executes the connection
	 * @return Returns a JSONObject containing information from the process.
	 * @throws ConnectException In case of communication errors with server
	 */
	public JSONObject execute() throws ConnectException{
		JSONParser jsonParser = new JSONParser();
		JSONObject json = jsonParser.getJSONFromUrl(URL, params);

		params = new ArrayList<NameValuePair>();
		if(json == null)
		{
			throw new ConnectException("Error connecting to server");
		}
		return json;
	}	
	
	/**
	 * Checks if the JSONObject was succeeded
	 * @param json A JSONObject containing information about the process
	 * @param whichAPIHandler Tells which APIHandler that called function, only used when not succeeded
	 * @param theProblem Tells the Problem with the APIHanlder, only used when not succeeded
	 * @return Returns true if the JSONObject was succeeded, false otherwise
	 */
	public boolean jsonSuccess(JSONObject json,String whichAPIHandler) {
		
		try {
			// If JSON object contains success key 
			if(json.getString(KEY_SUCCESS) != null){
				// If the value of success is 1 (true)
				if(Integer.parseInt(json.getString(KEY_SUCCESS)) == 1){
					return true;
				}
			}
		}
		catch (JSONException e) {
			try {
				Log.d("APIHandler jsonSuccess : " + whichAPIHandler, json.getString(KEY_ERROR_MESSAGE));
			} catch (JSONException e1) {
				Log.e("APIHandler jsonSuccess : " + whichAPIHandler, "An error occurred and there was no error message.");
			}	
		}
		return false;		
	}
}
