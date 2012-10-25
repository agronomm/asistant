package com.agronomm.asistant.voice;


import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agronomm.asistant.voice.play.Synthesizer;


// Класс отвечает за озвучивание переданной в POST фразы

public class VoiceTolkerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		request.setCharacterEncoding("UTF-8");

		Synthesizer speak = new Synthesizer();

		String words = request.getParameter("userText");

		System.out.println("[voice] Speaking: " + words);
		speak.setAnswer(words);
		speak.run();
	}
}