package util;

import java.io.*;
import javax.sound.sampled.*;

public class SoundPlayer extends Thread {

	private boolean ready;
	private boolean playRadioSong;
	private boolean playBattleMusic;

	public SoundPlayer() {

	}

	public void run() {
		while(playRadioSong){
			playSound("resources/sounds2/RadioSong.wav");
			try {
				this.sleep(22000);
			} catch (InterruptedException e) {
			}
		}
		
		try {
			this.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ready = true;
	}

	public void playSound() {
		if (ready) {
			String[] arr = { "1", "2", "5", "9", "10", "11", "14", "15", "16",
					"17", "18", "19", "20", "21", "22", "23" };
			try {

				File yourFile = new File("resources/sounds2/"
						+ arr[(int) (Math.random() * arr.length)] + ".wav");
				AudioInputStream stream;
				AudioFormat format;
				DataLine.Info info;
				Clip clip;

				stream = AudioSystem.getAudioInputStream(yourFile);
				format = stream.getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(stream);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ready = false;
	}
	public void playSound(String fileName) {
			try {

				File yourFile = new File(fileName);
				AudioInputStream stream;
				AudioFormat format;
				DataLine.Info info;
				Clip clip;

				stream = AudioSystem.getAudioInputStream(yourFile);
				format = stream.getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.open(stream);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public void setRadioSong(boolean rs){
		playRadioSong = rs;
	}
	
	public void setBattleMusic(boolean bm){
		playBattleMusic = bm;
	}

}
