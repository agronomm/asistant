package com.agronomm.asistant.utils;

import java.io.File;

public class FileUtil {

	public static void delete(File file) {
		// Make sure the file or directory exists and isn't write protected
		if (!file.exists()) {
			System.err.println("Delete: no such file or directory: " + file.getName());
		}
		if (!file.canWrite()) {
			System.err.println("Delete: write protected: " + file.getName());
		}
		// Attempt to delete it
		boolean success = file.delete();
		if (!success) {
			System.err.println("Delete: deletion failed" + file.getName());
		}
	}

}
