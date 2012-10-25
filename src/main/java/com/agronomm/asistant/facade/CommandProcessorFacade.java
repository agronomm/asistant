package com.agronomm.asistant.facade;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class CommandProcessorFacade extends ServletFacade {
	
	public static void processUserCommand(String userFrase, Long time) {
		try {
			call("processcommand", "userText=" + URLEncoder.encode(userFrase, "UTF-8") + "&" + "time=" + time);
		}
		catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
