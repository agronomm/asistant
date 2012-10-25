package com.agronomm.asistant.facade;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class VoiceServletFacade extends ServletFacade {
	
	public static void say(String userFrase) {
		try {
			call("speak", "userText=" + URLEncoder.encode(userFrase, "UTF-8"));
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
