package namePong;

import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;

/**
 * Sound will handle the loading and playing of all the sounds...
 */
public class Sound {

  private ArrayList<AudioClip> clips;

  /**
   * Default constructor
   */
  public Sound() {
    clips = new ArrayList<AudioClip>();
  }

  /**
   * takes an array of String file names of the sound files to load.. use only
   * .wav or .au ... you may try .ogg ...
   */
  public void loadSoundFiles(String[] soundFiles) {
    AudioClip clip = null;
    for (int i = 0; i < soundFiles.length; i++) {
      try {
        clip = Applet.newAudioClip(getClass().getResource(soundFiles[i]));
      } catch (Exception e) {
        e.printStackTrace();
      }
      if (clip == null)
        System.out.println("Problem loading sound file: " + soundFiles[i]);
      else {
        clips.add(clip);
      }
    }
  }

  /**
   * stop the playing of a certain clip
   */
  public void stop(int clipNum) {
    clips.get(clipNum).stop();

  }

  /**
   * stops all clips (playing or not)
   */
  public synchronized void stopAll() {
    for (AudioClip clip : clips) {
      clip.stop();
    }
  }

  /**
   * play clip with number clipNum, loop is true if the sound should be played
   * until it is stopped by Menu
   */
  public void play(int clipNum, boolean loop) {
    if (loop) {
      clips.get(clipNum).loop();
    } else
      clips.get(clipNum).play();
  }


}