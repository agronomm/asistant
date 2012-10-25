package com.agronomm.asistant.executor.command;

public abstract class AbstractCommand implements Runnable {
	
	public abstract boolean init(String str);
	public abstract void run();
	public abstract void revert();

}
