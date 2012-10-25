package com.agronomm.asistant;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import com.agronomm.asistant.facade.VoiceServletFacade;
import com.agronomm.asistant.utils.OSUtil;
import com.agronomm.asistant.voice.VoiceListenerServicse;
import com.agronomm.asistant.web.WebService;


public class Asistant {

	public static Thread wwwThread = null;
	public static Thread sheduleThread = null;
	public static Thread devicesThread = null;
	public static long startTime = 0;
	public static PrintWriter zwaveSocketOut = null;
	public static BufferedReader zwaveSocketIn = null;
	private static String orPath;

	public static void main(String[] args) {

		try {
			orPath = Asistant.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			startTime = System.currentTimeMillis();
			Properties prop = new Properties();
			InputStream is = new FileInputStream(orPath + "conf/main.property");
			prop.load(is);

			System.out.println("System starting" + "\n Version: " + prop.getProperty("version"));

			// Запускам поток с записью звука
			VoiceListenerServicse voice = new VoiceListenerServicse();
			voice.setPath(orPath);

			/**
			try {

				Socket echoSocket = new Socket("127.0.0.1", 6004);
				zwaveSocketOut = new PrintWriter(echoSocket.getOutputStream(), true);
				zwaveSocketIn = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

			}
			catch (IOException e) {
				System.err.println("[zwave] Couldn't get I/O for the connection to z-wave server");
			}

			// Запускам поток с планировщиком
			SheduleService shedule = new SheduleService();
			sheduleThread = shedule.getThread();
**/
			// Запускам поток с веб-интерфейсом
			WebService www = new WebService();
			www.setPath(orPath);
			wwwThread = www.getThread();


			Thread.sleep(2000);
			VoiceServletFacade.say("Система запущена");

			
		}
		catch (Exception ee) {
			ee.printStackTrace();
		}
	}

}
