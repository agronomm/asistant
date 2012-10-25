package com.agronomm.asistant.executor.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProgrammRunnerCommand extends AbstractCommand {

	private String alias = "";
	private String key = "";
	@SuppressWarnings("serial")
	private Map<String, String> aliasMap = new HashMap<String, String>(){{
		put("БЛОКНОТ", "notepad");
	}};

	@Override
	public void run() {
		if (alias.isEmpty()) {
			return;
		}
		try {
			System.out.println("now running " + alias);
			new ProcessBuilder(alias).start();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void revert() {

	}

	@Override
	//TODO придумать как задавать алиасы
	public boolean init(String str) {
		String [] strs = str.split(" ");
		for (int i = 0; i < strs.length; i++) {
			String current = aliasMap.get(strs[i]);
			if (!(current == null || current.isEmpty())) {
				key = strs[i];
				alias = current;
				//alias = str.substring(str.indexOf(strs[i]), str.length());
			}
		}
		System.out.println("alias = " + alias);
		return alias.length() == 0;
	}

	@Override
	public String toString() {
		return "Запущена программа " + key;
	}
	
	
}
