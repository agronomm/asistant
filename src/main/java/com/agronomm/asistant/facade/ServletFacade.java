package com.agronomm.asistant.facade;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class ServletFacade {
	
	public static String serverPort = "8080";
	
	public static InputStream call(String url, String params) {

		java.net.URLConnection urlConnect;
		try {
			urlConnect = new URL("http://localhost:" + serverPort + "/control/" + url + "?" + params).openConnection();
			urlConnect.setRequestProperty("User-Agent", "Mozilla/5.0");
			urlConnect.setRequestProperty("Accept-Charset", "UTF-8");
			return urlConnect.getInputStream();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

}
