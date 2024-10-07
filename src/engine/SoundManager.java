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