package com.agronomm.asistant.voice.play;



import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class WritenToOral {

	private static final String GOOGLE_TRANSLATE_TTS_URL = "http://translate.google.com/translate_tts?tl=ru&q=";

	public static InputStream getUsInputStream(String userFrase, Object lock) throws IOException {

		// Safe approach to setting http.agent system property
		synchronized (lock) {
			System.setProperty("http.agent", "");
		}

		// Set up URL connection
		java.net.URLConnection urlConnect = new URL(GOOGLE_TRANSLATE_TTS_URL + userFrase).openConnection();
		urlConnect.setRequestProperty("User-Agent", "Mozilla/5.0");

		// Open connection and stream to inputStream
		InputStream iStream = urlConnect.getInputStream();
		return iStream;
	}

	public static String getUsString(String userFrase, Object lock) throws IOException, InterruptedException {
		// URL connection inputstream is read into response
		BufferedReader br = new BufferedReader(new InputStreamReader(getUsInputStream(userFrase, lock)));
		String buffer;
		String response = "";
		while ((buffer = br.readLine()) != null) {
			response += buffer;
			if (Thread.interrupted()) {
				br.close();
				throw new InterruptedException("Thread Task Cancelled");
			}
		}
		br.close();
		return response;
	}

	public static byte[] getUsByteArray(String userFrase, Object lock) throws IOException, InterruptedException {
		// Open connection and stream to inputStream
		InputStream iStream = getUsInputStream(userFrase, lock);

		ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		int readLength;
		byte[] buffer = new byte[1024];

		// Read the GET Response into the ByteArray Stream
		while (-1 != (readLength = iStream.read(buffer))) {
			byteStream.write(buffer, 0, readLength);
			if (Thread.interrupted()) {
				byteStream.close();
				iStream.close();
				throw new InterruptedException("Thread Task Cancelled");
			}
		}

		// Close the Streams
		byteStream.close();
		iStream.close();

		// Return byte array with MP3 files
		return byteStream.toByteArray();
	}
}
