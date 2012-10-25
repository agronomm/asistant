package com.agronomm.asistant.voice.capture;

import com.agronomm.asistant.utils.OSUtil;

public class VoiceCapturersFactory {
	
	public static VoiceCapturer getCapturer() {
		if (OSUtil.isUnix()) {
			return new LinuxVoiceCapturer();
		} else if (OSUtil.isWindows()) {
			return new WindowsVoiceCapturer();
		} else {
			throw new IllegalStateException("Can not start voice capturer. OS does not support.");
		}
		
	}

}
