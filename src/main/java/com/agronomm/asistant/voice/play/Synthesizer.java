package com.agronomm.asistant.voice.play;

import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.Player;

import java.io.ByteArrayInputStream;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 
 * SYNTHESIZER MODULE -- LAST EDITED BY SAMIR AHMED - JUNE 20 2011
 * 
 * OUTLINE: Synthesizer Used for Generating Speech Via Google Translate Text-to-Speech API This Class uses a CachedThreadPool To Increase Increase
 * Efficiency Speed Test: Single Thread 4.3 seconds vs Multi-Threaded 1.2 Seconds
 * 
 **/

public class Synthesizer implements Runnable {

	/*
	 * Synthesizer Data Fields userFrases : LinkedList: String Container with Tokenized Stringfs filesCount : Integer : The number of output files
	 * that will be generated isUnassigned : Boolean : True/False holds status of the AudioDevice object AudioLine and FutureAudioLine are AudioDevice
	 * objects for the JLayer Player
	 */

	private LinkedList<String> userFrases;
	private Integer filesCount;
	private AudioDevice AudioLine;


	/** Constructor: Will submit the AudioDeviceLoader to the executor */

	/**
	 * Constructor for Synthesizer Object*
	 */
	public Synthesizer() {
		AudioLine = getAudioDevice();
	}


	/**
	 * Public method to set desire text for Text to Speech Synthesis
	 */

	public void setAnswer(String rawText) {

		// Convert to good ole' plain english ascii characters
		rawText = Normalizer.normalize(rawText, Normalizer.Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

		// Ensure we have only whitespaces that are single spaces characters
		rawText = rawText.replaceAll("%", " процент ");
		rawText = rawText.replaceAll("&", " и ");
		rawText = rawText.replaceAll("\\+", " плюс ");

		rawText = rawText.replaceAll("\\s", " ");
		rawText = rawText.replaceAll("\\s{2,}", " ");
		rawText = rawText.replaceAll("\"|\'|\\(|\\)|\\]|\\[", "");

		// Tokenize userText and Retreive number of filesCount;
		userFrases = smartTokenize(rawText);
		filesCount = userFrases.size();

		System.out.println("[voice] " + rawText);
	}

	/**
	 * SmartTokenize : Function for parsing by grammar and restructuring output: Average Run Time ~ 12 ms *
	 */
	private LinkedList<String> smartTokenize(String oneString) {
		// Check if we end on a period. If not Add one
		if (!oneString.endsWith(".")) {
			oneString = oneString + ".";
		}

		// Create a regex pattern parse by punctuation;
		Pattern tokenizerPattern = Pattern.compile("([^\\.,;:!]*[\\.,;:!])+?");
		Matcher tokenizerMatcher = tokenizerPattern.matcher(oneString);

		// Declare String variables.
		String buffer;
		String sentence = "";
		int count = 0;
		int length = 0;

		// Create Linked list for putting in strings
		LinkedList<String> answerList = new LinkedList<String>();

		// For every punctuation separated value
		while (tokenizerMatcher.find()) {

			// Instantiate Buffer count and length;
			buffer = tokenizerMatcher.group();
			count = sentence.length();
			length = buffer.length();

			// CASE 1: We can easily add buffer to our sentence because we are under the 100 char limit
			if ((count + length) <= 100) {
				sentence += buffer;
			}

			// CASE 2: If buffer length is greater than 100...
			else if (length > 100) {

				// Add tack on however much of buffer is required to get a 100 character string
				int breakpoint = buffer.lastIndexOf(' ', 100 - sentence.length());
				answerList.addLast(sentence + buffer.subSequence(0, breakpoint));
				length -= breakpoint;

				// If we still have more than 100 characters we cut off the largest chunks until we are under one hundred
				if (length > 100) {
					while (length > 100) {
						int newbreakpoint = buffer.lastIndexOf(' ', breakpoint + 100);
						answerList.addLast(buffer.substring(breakpoint, newbreakpoint));
						length -= (newbreakpoint - breakpoint);
						breakpoint = newbreakpoint;
					}
				}

				// In either case, we just tack on the remainder, assuming it is not zero
				if (length > 0) {
					answerList.addLast(buffer.substring(breakpoint, buffer.length()));
					sentence = "";
				}
			}

			// CASE 3: Buffer is less than 100 but added to sentence is greater
			// We add sentence too list, we remake sentence as buffer
			else {
				answerList.addLast(sentence);
				sentence = buffer;
			}
		}
		// Last case in the event we missed something
		if (sentence != "") {
			answerList.addLast(sentence);
		}

		return answerList;
	}

	/** getSpeech **/

	/**
	 * Speak will create multiple SpeechFileGenerator Objects, which return mp3 chunks the method will concatenate all the files and then play them
	 * with the JLayer Mp3 Player Object.
	 */
	//TODO change to standart audioDevice
	private synchronized void Speak(LinkedList<String> userFrases, Integer Files) {

		// Use the executor service to submit all the TTS strings simultaneously
		try {
			// For every file in userFrases LinkedList; Generate a textToSpeech Object and execute it
			ArrayList<byte[]> audioDataPieces = new ArrayList<byte[]>(Files);
			for (int ii = 1; ii <= Files; ii++) {
				audioDataPieces.add(toSound(userFrases.pop()));
			}

			/* Create a new mp3 file byte[] of size zero */
			byte[] audioDataArray = new byte[0];
			int size = audioDataArray.length;

			/*
			 * For every future in the arrayList, Append it to mp3_file variable;
			 */
			for (byte[] piece : audioDataPieces) {

				/* Calculate piece length */
				int pieceLength = piece.length;

				/* Create a new temporary byte array with size large enough to accomadate the new piece */
				byte[] temp = new byte[size + pieceLength];

				/* Copy the original file into the byte array */
				System.arraycopy(audioDataArray, 0, temp, 0, size);

				/* Copy the new piece into the byte array, offset by the old files length */
				System.arraycopy(piece, 0, temp, size, pieceLength);

				/* Update the size of the whole file and reassign variables */
				audioDataArray = temp;
				size += pieceLength;

			}

			Player player = new Player(new ByteArrayInputStream(audioDataArray), this.AudioLine);

			/* Print Response time and play */
			player.play();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Run Interface*
	 */
	public void run() {
		try {
			Speak(userFrases, filesCount);
		}
		catch (Exception ee) {
			ee.printStackTrace();
		}
	}
	
	private AudioDevice getAudioDevice() {
		try {
			// Get AudioDevice from Registry
			FactoryRegistry factoryregistry = FactoryRegistry.systemRegistry();
			AudioDevice audio = factoryregistry.createAudioDevice();
			return audio;
		}
		catch (Exception ee) {
			System.err.println("[voice] Could not load audio driver");
			ee.printStackTrace();
			return null;
		}
	}


	private byte[] toSound(String text) {
		byte[] result;
		try {
			//
			result = WritenToOral.getUsByteArray(URLEncoder.encode(text, "UTF-8"), this);
		}
		catch (Exception ee) {
			ee.printStackTrace();
			result = new byte[0];
		}

		return result;
	}
}
