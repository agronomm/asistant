package com.agronomm.asistant.voice.capture;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LinuxVoiceCapturer extends VoiceCapturer {
	private ProcessBuilder procBuilder;
	
	public void init(String microphone) {

		wavFile = getNextFile("wav");
		flacFile = getNextFile("flac");

		// Тут захват и обработка звука
		// ////////////////////////////////

		// указываем в конструкторе ProcessBuilder,
		// что нужно запустить программу rec (из пакета sox)

		if (microphone == null) {
			procBuilder = new ProcessBuilder("rec", "-q", "-c", "1", "-r", "16000", wavFile.getPath() + wavFile.getName(), "trim", "0", "5");
		} else {
			procBuilder = new ProcessBuilder("rec", "-q", "-c", "1", "-r", "16000", "-d", microphone, wavFile.getPath() + wavFile.getName(),
					"trim", "0", "5");
		}

		// перенаправляем стандартный поток ошибок на
		// стандартный вывод
		procBuilder.redirectErrorStream(true);
	}

	public File call() {
		// запуск программы
		Process process = null;
		try {
			process = procBuilder.start();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// читаем стандартный поток вывода
		// и выводим на экран
		InputStream stdout = process.getInputStream();
		InputStreamReader isrStdout = new InputStreamReader(stdout);
		BufferedReader brStdout = new BufferedReader(isrStdout);

		String line = null;
		try {
			while ((line = brStdout.readLine()) != null) {
				System.out.println(line);
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		// ждем пока завершится вызванная программа
		// и сохраняем код, с которым она завершилась в
		// в переменную exitVal
		try {
			@SuppressWarnings("unused")
			int exitVal = process.waitFor();
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
		return getFlacFile();
	}

}
