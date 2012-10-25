package com.agronomm.asistant.voice.capture;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.util.EntityUtils;

import com.agronomm.asistant.facade.CommandProcessorFacade;


/**
 * Using HTTPClient to POST .flac recording file to Google.com Speech API Expect Response Times ~ 2 seconds. But vary based on the length of the
 * recorded file Do not create multiple objects for concurrent use.
 */

public class OralToWriten implements Runnable {

	private HttpClient httpclient;
	private HttpPost httppost;
	private BufferedReader br;
	private Future<File> futureFile;

	// Immutable data members
	private final String speechAPIURL = "http://www.google.com/speech-api/v1/recognize?xjerr=1&client=chromium&lang=ru-RU";
	private final String HeaderType = "Content-Type";
	private final String HeaderContent = "audio/x-flac; rate=16000";
	private final String User_Agent = "Mozilla/5.0";
	private Long time;

	/**
	 * Constructor will setup httpclient, post request method and useragent information as required
	 */
	public OralToWriten(Future<File> futureFile, Long time) {
		this.time = time;
		this.futureFile = futureFile;
		httpclient = new DefaultHttpClient();
		System.setProperty("http.agent", "");
		httppost = new HttpPost(speechAPIURL);
		HttpProtocolParams.setUserAgent(httpclient.getParams(), User_Agent);
		httppost.setHeader(HeaderType, HeaderContent);
	}

	/**
	 * This file will post the flac file to google and store the Json String in jsonResponse data member
	 */
	public void run() {
		String jsonResponse = "";
		File FLACFile = null;
		try {
			FLACFile = futureFile.get();
			if (FLACFile == null) {
				return;
			}

			InputStreamEntity reqEntity = new InputStreamEntity(new FileInputStream(FLACFile), -1);

			// Set the content type of the request entity to binary octet stream.. Taken from the chunked post example HTTPClient
			reqEntity.setContentType("binary/octet-stream");

			// set the POST request entity...
			httppost.setEntity(reqEntity);

			// Create an httpResponse object and execute the POST
			HttpResponse response = httpclient.execute(httppost);

			// Capture the Entity and get content
			HttpEntity resEntity = response.getEntity();

			// System.out.println(System.currentTimeMillis()-start);

			String buffer;
			jsonResponse = "";

			br = new BufferedReader(new InputStreamReader(resEntity.getContent()));
			while ((buffer = br.readLine()) != null) {
				jsonResponse = jsonResponse + buffer;
			}

			// Close Buffered Reader and content stream.
			EntityUtils.consume(resEntity);
			br.close();
			FLACFile.delete();
		}
		catch (ClientProtocolException e) {
			e.printStackTrace();
			FLACFile.delete();
			return;
		}
		catch (IOException e) {
			e.printStackTrace();
			FLACFile.delete();
			return;
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		catch (ExecutionException e) {
			e.printStackTrace();
		} finally {
			// Finally shut down the client
			httpclient.getConnectionManager().shutdown();
		}
		String userText = "";
		if (!jsonResponse.contains("\"utterance\":")) {
			// System.err.println("[record] Recognizer: No Data");
		} else {
			// Include -> System.out.println(wGetResponse); // to view the Raw output
			int startIndex = jsonResponse.indexOf("\"utterance\":") + 13; // Account for term
																					// "utterance":"<TARGET>","confidence"
			int stopIndex = jsonResponse.indexOf(",\"confidence\":") - 1; // End position
			userText = jsonResponse.substring(startIndex, stopIndex);

			// Determine Confidence
			startIndex = stopIndex + 15;
			stopIndex = jsonResponse.indexOf("}]}") - 1;
			double confidence = Double.parseDouble(jsonResponse.substring(startIndex, stopIndex));
			if (confidence < 0.4) {
				return;
			}
			System.out.println("[data] Utterance : " + userText.toUpperCase() + "\n[data] Confidence Level: " + (confidence * 100));
		}
		CommandProcessorFacade.processUserCommand(userText, time);
	}

}