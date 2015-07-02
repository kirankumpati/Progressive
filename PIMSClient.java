package com.triumvir.progressive;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import sailpoint.integration.IIQClient;

public class PIMSClient {
	
	public static void main(String[] args) {
				
		String iiqUser = "spadmin";
		String iiqPass = "admin";
		String host = "localhost";
		String port = "8888";
		String iiqUrl = "http://"+host+":"+port+"/iiq63";
		
		String iiqRequest = null;
		DefaultHttpClient client = null;
		IIQClient iiqClient = null;
		HttpGet request =null;
		HttpResponse response = null;
		BufferedReader br = null;
		String accountName = null;
		String password = null;
	
		
		try {
			iiqClient = new IIQClient(iiqUrl, iiqUser, iiqPass);
		} 
		catch (Exception e) {
			
			e.printStackTrace();
		}
		
		System.out.println(" - ready to connect to to IIQ at " + iiqUrl);

		try {
				
				client = new DefaultHttpClient();
				client.getCredentialsProvider().setCredentials(
						new AuthScope(host,Integer.parseInt(port)),
						new UsernamePasswordCredentials(iiqUser, iiqPass));
			
				// checking for ping
				
			  	iiqRequest = iiqUrl+"/rest/ping";
		        System.out.println("\nRequest: " + iiqRequest);
		        request = new HttpGet(iiqRequest);
		        response = client.execute(request);
		        br = new BufferedReader (new InputStreamReader(response.getEntity().getContent()));
		        String line = "";
		        while ((line = br.readLine()) != null)
		                System.out.println(line);
		
		        // calling REST service
		        
		        accountName = "Kiran";
		        password = "admin1234";
		        //iiqRequest = "http://localhost:8888/iiq63/rest/pims/"+accountName;
		        iiqRequest = "http://localhost:8888/iiq63/rest/pims/"+accountName+"/"+password;
		        System.out.println("\nRequest: " + iiqRequest);
		        request = new HttpGet(iiqRequest);
		        response = client.execute(request);
		        System.out.println("the result is : " + response);
		        br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		        line = "";
		        while ((line = br.readLine()) != null) {
		            System.out.println(line);
		        }
		  
			} catch (Exception e) {
						
			e.printStackTrace();
			}
	}
}