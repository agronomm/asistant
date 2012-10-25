package com.agronomm.asistant.executor.command;

public class SystemCommand extends AbstractCommand {
	
	private String shellLine = "";
	@Override
	public boolean init(String str) {
		return true;
		
	}

	@Override
	public void run() {
		System.out.println("SystemCommand vas runned");

	}

	@Override
	public void revert() {
		// TODO Auto-generated method stub

	}
}
