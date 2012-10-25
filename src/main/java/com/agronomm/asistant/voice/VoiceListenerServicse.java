package com.agronomm.asistant.voice;

import java.io.*;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.agronomm.asistant.voice.capture.OralToWriten;
import com.agronomm.asistant.voice.capture.VoiceCapturer;
import com.agronomm.asistant.voice.capture.VoiceCapturersFactory;


public class VoiceListenerServicse implements Runnable {

	private String prPath;

	public VoiceListenerServicse() {
		Thread t = new Thread(this);
		t.start();
	}

	public void setPath(String prPath) {
		this.prPath = prPath;
	}

	public synchronized void run() {

		System.out.println("[record] Service started");

		final Properties prop = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(prPath + "/conf/main.property");
			prop.load(is);
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		int microphonesCount = Integer.valueOf(prop.getProperty("voice.microphones"));
		int period, threadCount;
		period = threadCount = Integer.valueOf(prop.getProperty("voice.listeningPeriod"));
		ScheduledExecutorService service = new ScheduledThreadPoolExecutor(period * microphonesCount);
		for (int m = 1; m <= microphonesCount; m++) {
			for (int i = 0; i < threadCount; i++) {
				service.scheduleAtFixedRate(new VoiceParser(prop.getProperty("microphoneDevice" + m)), i, period, TimeUnit.SECONDS);
			}
		}
		
	}

	private class VoiceParser implements Runnable {
		private String deviceNumber;
		public VoiceParser(String deviceNumber) {
			super();
			this.deviceNumber = deviceNumber;
		}

		public void run() {
			Long startTime = System.currentTimeMillis();
			VoiceCapturer capturer = VoiceCapturersFactory.getCapturer();
			capturer.init(deviceNumber);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Future<File> futureFile = executor.submit(capturer);
			OralToWriten flacConverter = new OralToWriten(futureFile, startTime);
			executor.submit(flacConverter);		
		}

	}
}
