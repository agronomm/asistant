package com.agronomm.asistant.executor;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.agronomm.asistant.executor.command.AbstractCommand;
import com.agronomm.asistant.executor.command.ProgrammRunnerCommand;
import com.agronomm.asistant.executor.command.SystemCommand;
import com.agronomm.asistant.facade.VoiceServletFacade;



public class CommandProcessorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static String commandKey = "СИСТЕМА";
	private static AbstractCommand command = null;
	private static Long previosTime = 0l;
	private static boolean gettingCommand = false;
	private static int listeningTime = 3000;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setStatus(HttpServletResponse.SC_OK);
		request.setCharacterEncoding("UTF-8");
		String userText = request.getParameter("userText").toUpperCase();
		Long time = Long.parseLong(request.getParameter("time"));
		System.out.println("ut = " + userText + "   " + time / 1000);
		synchronized (this) {
			if (!gettingCommand && userText.isEmpty()) {
				// если мы не ожидаем ввод и ничего нет - выходим
				return;
			} else if (userText.isEmpty() && (System.currentTimeMillis() - previosTime < listeningTime)) {
				// если мы ожидаем ввод и ничего нет но не истекло время ожидания - выходим
				return;
			} else if (userText.isEmpty() && (System.currentTimeMillis() - previosTime > listeningTime)) {
				System.err.println("Выполнение");
				// если мы ожидаем ввод и ничего нет но истекло время ожидания - выполняем команду
				gettingCommand = false;
				previosTime = time;
				if (command == null) {
					System.err.println("Сброс");
					return;
				}
				new Thread(command).start();
				VoiceServletFacade.say(command.toString());
				command = null;
				return;
			} else if (!userText.isEmpty() && !gettingCommand) {
				System.err.println(" init = " + userText.contains(commandKey));
				// обрабатываем команду
				// если начало комманды ставим флаг чтения комманды
				if (userText.contains(commandKey) && (System.currentTimeMillis() - previosTime > listeningTime)) {
					gettingCommand = true;
					listeningTime = 7000;
					previosTime = time;
					if (userText.contains("ЗАПУСТИТЬ")) {
						command = new ProgrammRunnerCommand();
					} else if (userText.contains("ВЫПОЛНИТЬ")) {
						command = new SystemCommand();
					} else {
						VoiceServletFacade.say("Слушаю");
						return;
					}
				} else {
					return;
				}

			}
			// если команда еще не создана выбираем тип команды
			if (command == null) {
				if (userText.contains("ЗАПУСТИТЬ")) {
					command = new ProgrammRunnerCommand();
				} else if (userText.contains("ВЫПОЛНИТЬ")) {
					command = new SystemCommand();
				} else {
					return;
				}
			}
			//пытаемся иницыализировать команду, если получилось - запускаем
			if (command.init(userText)) {
				System.err.println("Выполнение комманды");
				gettingCommand = false;
				new Thread(command).start();
				VoiceServletFacade.say(command.toString());
				command = null;
			}
			previosTime = time;
		}
	}
}
