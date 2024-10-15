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
    /** Default value of currentVolume */
    private static int currentVolume = 10;
    /** Maximum and minimum values of volume */
    private final float MIN_VOL = -80.0f;
    private final float MAX_VOL = 6.0f;
    /** Current playing BGM */
    private Sound currentBGM;

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
            loadSound(Sound.COUNTDOWN, "res/sound/SFX/countdown.wav");
            loadSound(Sound.ALIEN_HIT, "res/sound/SFX/alienHit.wav");
            loadSound(Sound.ALIEN_LASER, "res/sound/SFX/alienLaser.wav");
            loadSound(Sound.PLAYER_HIT, "res/sound/SFX/playerHit.wav");
            loadSound(Sound.PLAYER_LASER, "res/sound/SFX/playerLaser.wav");
            loadSound(Sound.PLAYER_MOVE, "res/sound/SFX/playerMove.wav");
            loadSound(Sound.COIN_INSUFFICIENT, "res/sound/SFX/coinInsufficient.wav");
            loadSound(Sound.COIN_USE, "res/sound/SFX/coinUse.wav");
            loadSound(Sound.GAME_END, "res/sound/SFX/gameEnd.wav");
            loadSound(Sound.UFO_APPEAR, "res/sound/SFX/ufoAppear.wav");
            loadSound(Sound.BULLET_BLOCKING, "res/sound/SFX/bulletBlocking.wav");
            loadSound(Sound.ITEM_2SHOT, "res/sound/SFX/item_2shot.wav");
            loadSound(Sound.ITEM_3SHOT, "res/sound/SFX/item_3shot.wav");
            loadSound(Sound.ITEM_BARRIER_ON, "res/sound/SFX/item_barrierOn.wav");
            loadSound(Sound.ITEM_BARRIER_OFF, "res/sound/SFX/item_barrierOff.wav");
            loadSound(Sound.ITEM_BOMB, "res/sound/SFX/item_bomb.wav");
            loadSound(Sound.ITEM_GHOST, "res/sound/SFX/item_ghost.wav");
            loadSound(Sound.ITEM_TIMESTOP_ON, "res/sound/SFX/item_timestopOn.wav");
            loadSound(Sound.ITEM_TIMESTOP_OFF, "res/sound/SFX/item_timestopOff.wav");
            loadSound(Sound.ITEM_SPAWN, "res/sound/SFX/item_spawn.wav");
            loadSound(Sound.BGM_MAIN, "res/sound/BGM/MainTheme.wav");
            loadSound(Sound.BGM_GAMEOVER, "res/sound/BGM/GameOver.wav");
            loadSound(Sound.BGM_SHOP, "res/sound/BGM/Shop.wav");
            loadSound(Sound.BGM_LV1, "res/sound/BGM/Lv1.wav");
            loadSound(Sound.BGM_LV2, "res/sound/BGM/Lv2.wav");
            loadSound(Sound.BGM_LV3, "res/sound/BGM/Lv3.wav");
            loadSound(Sound.BGM_LV4, "res/sound/BGM/Lv4.wav");
            loadSound(Sound.BGM_LV5, "res/sound/BGM/Lv5.wav");
            loadSound(Sound.BGM_LV6, "res/sound/BGM/Lv6.wav");
            loadSound(Sound.BGM_LV7, "res/sound/BGM/Lv7.wav");

            setVolume(currentVolume);
            logger.info("Finished loading all sounds.");

        } catch (IOException e) {
            soundEnabled = false;
            logger.warning("Loading failed: IO Exception");
        } catch (UnsupportedAudioFileException e) {
            soundEnabled = false;
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
     * @return current playing BGM
     * */
    public Sound getCurrentBGM() { return currentBGM; }

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
                logger.warning("Sound not found: " + sound);
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
                logger.warning("Sound not playing or not found: " + sound);
            }
        }
    }

    /**
     * Checks if the specified sound is currently playing.
     *
     * @param sound Key value of the sound to check.
     * @return true if the sound is playing, false otherwise.
     */
    public boolean isSoundPlaying(Sound sound) {
        if (soundEnabled) {
            Clip clip = soundClips.get(sound);
            if (clip != null) {
                return clip.isRunning();
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
        return false;  // Return false if sound is not enabled or not found
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
                currentBGM = sound;
                clip.setFramePosition(0);
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
    }

    /** Stop and close all sound files **/
    public void closeAllSounds() {
        if (soundEnabled) {
            for (Sound sound : soundClips.keySet()) {
                Clip clip = soundClips.get(sound);
                if (clip != null && clip.isRunning())
                    clip.stop();
                if (clip != null) {
                    clip.close();
                }
            }
        }
    }

}