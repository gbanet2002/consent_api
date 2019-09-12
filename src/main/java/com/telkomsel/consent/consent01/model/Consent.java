package com.telkomsel.consent.consent01.model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import com.telkomsel.login.login01.model.Login;

@Service
@ConfigurationProperties
@Validated
public class Consent {
	@Autowired 
	//get parameter from application.properties
	
	@Value("${api_key_login}")
    @NotEmpty
    private String api_key_login;
	
	@Value("${api_secret_login}")
    @NotEmpty
    private String api_secret_login;
	
	@Value("${api_url_login}")
    @NotEmpty
    private String api_url_login;
	
	@Value("${api_key_consent}")
    @NotEmpty
    private String api_key_consent;
	
	@Value("${api_secret_consent}")
    @NotEmpty
    private String api_secret_consent;
	
	@Value("${api_url_consent}")
    @NotEmpty
    private String api_url_consent;
	
	public String consent(String username, String password, String msisdn, String product_code, String customer_name, String consent_id, String consent_channel) {
		
		String result = "000";
		
		//login first
		Login lg = new Login();
		String resp = lg.login(username, password, api_key_login, api_secret_login, api_url_login);
		String session_id = this.getSessionID(resp);
		String consent_timestamp = Long.toString(System.currentTimeMillis());

		JSONObject requestBody = constructSMSBody(session_id,msisdn,product_code,customer_name,consent_id,consent_channel,consent_timestamp);
		String x_signature = getXSignature(api_key_consent, api_secret_consent);
		
		// Response variable
		String mashery_response = null;
		int mashery_response_code = 0;

		try {
					URL obj = new URL(api_url_consent);
					HttpURLConnection con = (HttpURLConnection) obj.openConnection();

					// add request header
					con.setRequestMethod("POST");
					con.setRequestProperty("Content-Type", "application/json");
					con.setRequestProperty("api_key", api_key_consent);
					con.setRequestProperty("x-signature", x_signature);
					// con.setReadTimeout(mashery_timeout);
					con.setDoOutput(true);

					// Send post request
					DataOutputStream wr = new DataOutputStream(con.getOutputStream());
					wr.writeBytes(requestBody.toString());
					wr.flush();
					wr.close();

					mashery_response_code = con.getResponseCode();

					BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					String inputLine;
					StringBuffer response = new StringBuffer();
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					in.close();
					mashery_response = response.toString();

					Map<String, List<String>> map = con.getHeaderFields();
					for (Map.Entry<String, List<String>> entry : map.entrySet()) {
						// System.out.println("Key : " + entry.getKey()
						// + " ,Value : " + entry.getValue());
						//System.out.println(entry.getKey() + " : " + entry.getValue());
					}
		} catch (IOException e) {
					e.printStackTrace();
		}
		
		result = "Resp Code : " + mashery_response_code + ", Resp : " + mashery_response;
		
		return result;
	}
	
	private static JSONObject constructSMSBody(String session_id, String msisdn, String product_code, String customer_name, String consent_id, String consent_channel, String consent_timestamp) {

		JSONObject consent_body = new JSONObject();
		consent_body.put("session_id", session_id);
		consent_body.put("msisdn", msisdn);
		consent_body.put("product_code", product_code);
		consent_body.put("customer_name", customer_name);
		consent_body.put("consent_id", consent_id);
		consent_body.put("consent_channel", consent_channel);
		consent_body.put("consent_timestamp", consent_timestamp);

		return consent_body;
	}
	
	private static String getXSignature(String api_key, String api_secret) {
		// System.out.println(api_key + api_secret +
		// Long.toString(System.currentTimeMillis() / 1000L));
		return DigestUtils.sha256Hex(api_key + api_secret + Long.toString(System.currentTimeMillis() / 1000L));
	}
	
	private String getSessionID(String json) {
	    
		String SessionID = "";
		
	    JSONObject obj = new JSONObject(json);
	    SessionID = obj.getString("session_id");

	    return SessionID;
	    
	}
}
