package util;

import java.io.*;
import javax.sound.sampled.*;

/**
 * Used to play various sounds
 * @author I3anaan
 *
 */
public class SoundPlayer extends Thread {

	/**
	 * If this SoundPlayer is ready for another sound file
	 */
	private boolean ready;
	/**
	 * If the radio song should be played
	 */
	private static boolean playRadioSong;
	/**
	 * The stream used
	 */
	private static AudioInputStream stream;
	/**
	 * The format used
	 */
	private static AudioFormat format;
	/**
	 * Info from the dataline
	 */
	private static DataLine.Info info;
	/**
	 * The actual clip
	 */
	private static Clip clip;
	/**
	 * The file played
	 */
	private static File yourFile;

	/**
	 * Constructor, does nothing currently
	 */
	public SoundPlayer() {
	}

	/**
	 * Keeps looping the radio song, or setting the ready variable to true
	 */
	public void run() {
		while(playRadioSong){
			this.playSound("resources/sounds2/RadioSong.wav");
			try {
				this.sleep(22000);
			} catch (InterruptedException e) {
			}
		}
		
		try {
			this.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		ready = true;
	}

	/**
	 * Plays a random glados sound
	 */
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
	/**
	 * Plays the given sound
	 * @param fileName	soundfile
	 */
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
				//e.printStackTrace();
			}
		}
	
	/**
	 * Toggle radio loop
	 * @param rs	the new value for playRadioSong
	 */
	public static void setRadioSong(boolean rs){
		playRadioSong = rs;
		
	}

	/**
	 * Increases the volume
	 */
	public static void upVolume(){
		FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		gainControl.setValue(6.0206f); // Higher volume by 10 decibels.
		
	}
}
