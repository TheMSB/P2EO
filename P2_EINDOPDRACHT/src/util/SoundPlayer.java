package util;

import java.io.*;
import javax.sound.sampled.*;

public class SoundPlayer extends Thread {

	private boolean ready;
	private static boolean playRadioSong;
	private boolean playBattleMusic;
	private static AudioInputStream stream;
	private static AudioFormat format;
	private static DataLine.Info info;
	private static Clip clip;
	private static File yourFile;

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
				AudioInputStream stream2;
				AudioFormat format2;
				DataLine.Info info2;
				Clip clip2;

				stream2 = AudioSystem.getAudioInputStream(yourFile);
				format2 = stream2.getFormat();
				info2 = new DataLine.Info(Clip.class, format2);
				clip2 = (Clip) AudioSystem.getLine(info2);
				clip2.open(stream2);
				clip2.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		ready = false;
	}
	public static void playSound(String fileName) {
			try {
				if(clip!=null){
					clip.stop();
				}
				
				
				
				yourFile = new File(fileName);
				stream = AudioSystem.getAudioInputStream(yourFile);
				
				format = stream.getFormat();
				info = new DataLine.Info(Clip.class, format);
				clip = (Clip) AudioSystem.getLine(info);
				clip.stop();
				
				clip.open(stream);
				clip.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	public static void setRadioSong(boolean rs){
		playRadioSong = rs;
	}
	
	public void setBattleMusic(boolean bm){
		playBattleMusic = bm;
	}

	public static void upVolume(){
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(6.0206f); // Higher volume by 10 decibels.
		
	}
}
