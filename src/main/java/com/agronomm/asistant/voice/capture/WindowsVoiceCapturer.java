package com.agronomm.asistant.voice.capture;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

/**
 * Reads data from the input channel and writes to the output stream
 */
public class WindowsVoiceCapturer extends VoiceCapturer {

	// record microphone && generate stream/byte array
	private AudioInputStream audioInputStream;
	private AudioFormat format;
	public TargetDataLine line;
	private double duration;

	public File call() {
		duration = 0;
		line = getTargetDataLineForRecord();
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final int frameSizeInBytes = format.getFrameSize();
		final int bufferLengthInFrames = line.getBufferSize() / 8;
		final int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		final byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead;
		line.start();
		long startTime = System.currentTimeMillis();
		do {
			if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			out.write(data, 0, numBytesRead);
		} while (System.currentTimeMillis() - startTime < 3000);
		// we reached the end of the stream. stop and close the line.
		line.stop();
		line.close();
		line = null;
		// stop and close the output stream
		try {
			out.flush();
			out.close();
		}
		catch (final IOException ex) {
			ex.printStackTrace();
		}
		// load bytes into the audio input stream for playback
		final byte audioBytes[] = out.toByteArray();
		final ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
		audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
		final long milliseconds = (long) ((audioInputStream.getFrameLength() * 1000) / format.getFrameRate());
		duration = milliseconds / 1000.0;
		System.out.println(duration);
		// save
		try {
			audioInputStream.reset();

			wavFile = getNextFile("wav");
			AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, wavFile);
		}
		catch (IOException e) {
			System.err.println("Can'nt write voice file");
		}
		return getFlacFile();
	}

	private TargetDataLine getTargetDataLineForRecord() {
		TargetDataLine line;
		final DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
		if (!AudioSystem.isLineSupported(info)) {
			return null;
		}
		// get and open the target data line for capture.
		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		}
		catch (final Exception ex) {
			return null;
		}
		return line;
	}

	@Override
	public void init(String microphone) {
		AudioFormat.Encoding encoding = AudioFormat.Encoding.PCM_SIGNED;
		float rate = 16000.0f;
		int channels = 1;
		int sampleSize = 16;
		boolean bigEndian = true;

		AudioFormat format = new AudioFormat(encoding, rate, sampleSize, channels, (sampleSize / 8) * channels, rate, bigEndian);
		this.format = format;
	}
}