package com.agronomm.asistant.voice.capture;

import java.io.File;
import java.util.concurrent.Callable;

import javaFlacEncoder.FLAC_FileEncoder;

public abstract class VoiceCapturer implements Callable<File> {
	
	protected String flacDir = System.getProperty("user.dir") + "voices/wav/";
	protected File flacFile;
	protected File wavFile;
	protected String microphone;
	public File getFlacFile() {
		FLAC_FileEncoder flacEncoder = new FLAC_FileEncoder();
		flacFile = getNextFile("flac");
		flacEncoder.encode(wavFile, flacFile);
		wavFile.delete();
		return flacFile;
	}
	
	public  abstract File call();
	
	protected File getNextFile(String type) {
		File file = new File(System.getProperty("user.dir") + "/voices/" + type + "/tmp." + type);
		int i = 0;
		while (file.exists()) {
			file = new File(System.getProperty("user.dir") + "/voices/" + type + "/" + i + "tmp." + type);
			i++;
		}
		return file;
	}
	

	
	public abstract void init(String microphone);
}
