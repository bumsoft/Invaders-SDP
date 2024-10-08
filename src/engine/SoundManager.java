package engine;

import javax.sound.sampled.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Manages sound effects and BGM.
 *
 * @author <a href="mailto:dpdudyyy@gmail.com">Yun Yeyoung</a>
 *
 */

public class SoundManager {

    /** Singleton instance of the class. */
    private static SoundManager instance;
    /** Save the sound file **/
    private static HashMap<Sound, Clip> soundClips;
    /** Application logger. */
    private static Logger logger;
    /** Sound manager activation flag */
    private boolean soundEnabled;
    /** Value of current volume */
    private static int currentVolume = 10;
    /** Maximum and minimum values of volume */
    private final float MIN_VOL = -80.0f;
    private final float MAX_VOL = 6.0f;

    /**
     * Private constructor.
     */
    private SoundManager() {
        logger = Core.getLogger();
        logger.info("Started loading sound resources.");

        soundClips = new HashMap<>();
        soundEnabled = true;
        try {
            loadSound(Sound.MENU_BACK, "res/sound/SFX/menuBack.wav");
            loadSound(Sound.MENU_CLICK, "res/sound/SFX/menuClick.wav");
            loadSound(Sound.MENU_MOVE, "res/sound/SFX/menuMove.wav");
            loadSound(Sound.MENU_TYPING, "res/sound/SFX/nameTyping.wav");

            logger.info("Finished loading all sounds.");

        } catch (IOException e) {
            logger.warning("Loading failed: IO Exception");
        } catch (UnsupportedAudioFileException e) {
            logger.warning("Loading failed: Unsupported audio file.");
        } catch (LineUnavailableException | IllegalArgumentException e) {
            soundEnabled = false;
            logger.warning("Loading failed: Sound device not found.");
        }
    }

    /**
     * Returns shared instance of SoundManager.
     *
     * @return Shared instance of SoundManager.
     */
    public static SoundManager getInstance() {
        if (instance == null)
            instance = new SoundManager();
        return instance;
    }

    /**
     * Load the sound and save the map.
     *
     * @param sound Key value of sound
     * @param filePath Path of the sound file
     * @throws IOException,UnsupportedAudioFileException,LineUnavailableException,IllegalArgumentException exception
     */
    public void loadSound(Sound sound, String filePath) throws IOException, UnsupportedAudioFileException, LineUnavailableException, IllegalArgumentException {
        File soundFile = new File(filePath);
        if (!soundFile.exists()) {
            throw new IOException("Sound file not found: " + filePath);
        }

        AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
        Clip clip = AudioSystem.getClip();
        clip.open(audioStream);

        soundClips.put(sound, clip);
    }

    /**
     * Apply volume to all audio files by converting integer volume to decibels non-linearly.
     *
     * @param volume Int value of volume (0-10)
     */
    private void setVolume(int volume) {
        float newVolume = MIN_VOL + (float)(Math.log(volume + 1) / Math.log(11)) * (MAX_VOL - MIN_VOL);

        for (Clip clip : soundClips.values()) {
            try {
                FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                volumeControl.setValue(newVolume);
            } catch (IllegalArgumentException e) {
                logger.warning("Failed to set volume: " + e.getMessage());
            }
        }
    }

    /**
     * @return current volume
     * */
    public int getVolume() { return currentVolume; }

    /**
     * Increases the volume of all sounds by 1.
     */
    public void volumeUp() {
        if (soundEnabled && (currentVolume < 10)) {
            currentVolume++;
            setVolume(currentVolume);
        }
    }

    /**
     * Decreases the volume of all sounds by 1.
     */
    public void volumeDown() {
        if (soundEnabled && (currentVolume > 0)) {
            currentVolume--;
            setVolume(currentVolume);
        }
    }

    /**
     * Play the sound file.
     *
     * @param sound Key value of sound
     */
    public void playSound(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null) {
                clip.setFramePosition(0);
                clip.start();
            } else {
                System.out.println("Sound not found: " + sound);
            }
        }
    }

    /**
     * Stop the sound file.
     *
     * @param sound Key value of sound
     */
    public void stopSound(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null && clip.isRunning()) {
                clip.stop();
            } else {
                System.out.println("Sound not playing or not found: " + sound);
            }
        }
    }

    /**
     * Loop the sound file.
     *
     * @param sound Key value of sound
     */
    public void loopSound(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                System.out.println("Sound not found: " + sound);
            }
        }
    }

    /** Close all sound files **/
    public void closeAllSounds() {
        if (soundEnabled) {
            for (Sound sound : soundClips.keySet()) {
                Clip clip = soundClips.get(sound);
                if (clip != null) {
                    clip.close();
                }
            }
        }
    }

}