package application;

import java.io.File;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class SoundControl {

	public static synchronized void playSound() {
		final File file = new File("resource/sound.wav");
		
		new Thread(new Runnable() {

			public void run() {
				try {
					AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(file);
					Clip clip = AudioSystem.getClip();
					clip.stop();
					clip.open(audioInputStream);
					// FloatControl gainControl = (FloatControl)
					// clip.getControl(FloatControl.Type.MASTER_GAIN);
					// gainControl.setValue(5.0f);
					clip.start();
				} catch (Exception e) {
					System.err.println(e.getMessage());
				}
			}
		}).start();
	}
}