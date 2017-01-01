package com.casmall.common;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SoundUtil {
	protected static Log log = LogFactory.getLog(SoundUtil.class);
	private String audioFile;
	private Clip clip;
	private AudioInputStream audioInputStream;

	public SoundUtil(String file) {
		this.audioFile = file;
	}

	public static void main(String[] args) {
		new SoundUtil("chimes.wav").playAudioFile();
	}

	public void playAudioFile() {
		try {
			audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(new FileInputStream(audioFile)));
			
			if(log.isDebugEnabled())
				log.debug("Sound Play:"+audioFile);
			
			clip = AudioSystem.getClip();
			clip.open(audioInputStream);

			clip.start();
			while (clip.isActive()) {
				try {
					Thread.sleep(99);
				} catch (Exception e) {
					break;
				}
			}
			clip.stop();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (clip != null) {
				try {
					clip.close();
				} catch (Exception e) {
				}
			}
		}
	}
}