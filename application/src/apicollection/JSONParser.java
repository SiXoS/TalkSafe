package apicollection;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;



/**
 * JSONParser.java
 *
 * This class parses a web page and creates a JSONObject of the information
 * 
 * 
 * @author Kristoffer Petersson
 * @version 2013-09-17
 */
public class JSONParser {
	InputStream is = null;
	JSONObject jObj = null;
	String json = "";

	public JSONObject getJSONFromUrl(String url, List<NameValuePair> params) {

		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(url);
			httpPost.setEntity(new UrlEncodedFormEntity(params));

			HttpResponse httpResponse = httpClient.execute(httpPost);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();

		} catch (Exception e)
		{
			Log.e("Connection Error", "@ JSONParser.getJSONFromUrl");
			e.printStackTrace();
		}

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "n");
			}
			is.close();
			json = sb.toString();
			Log.d("JSON", json);
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result ## " + e.toString());
			e.printStackTrace();
		}

		// try parse the string to a JSON object
		try {
			Log.d("TestLogging - MeetUp - " + "getJSONFromUrl  : ", json);
			jObj = new JSONObject(json);           
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}

		// return JSON String
		return jObj;
	}
}