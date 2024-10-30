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
    /** Application logger. */
    private static Logger logger;
    /** Sound manager activation flag */
    private boolean soundEnabled;
    /** Value of current volume */
    private static int currentVolume = 10;
    /** Maximum and minimum values of volume */
    private final float MIN_VOL = -80.0f;
    private final float MAX_VOL = 6.0f;
    /** Current playing BGM */
    private Sound currentBGM;

    /** Save the sound file **/
    private static HashMap<Sound, Clip> soundClips;
    /** Sound clip pools for simultaneous playback */
    private static Map<Sound, List<Clip>> soundPools;
    /** Pool size for each sound */
    private static final int POOL_SIZE = 2;

    private static final Set<Sound> POSITIONAL_SOUNDS = Set.of(
            Sound.ALIEN_HIT, Sound.ALIEN_LASER, Sound.PLAYER_HIT, Sound.PLAYER_MOVE, Sound.PLAYER_LASER, Sound.ITEM_BOMB,
            Sound.ITEM_SPAWN, Sound.ITEM_BARRIER_ON, Sound.ITEM_BARRIER_OFF, Sound.ITEM_TIMESTOP_ON, Sound.ITEM_TIMESTOP_OFF,
            Sound.ITEM_2SHOT, Sound.ITEM_3SHOT, Sound.ITEM_GHOST, Sound.BULLET_BLOCKING
    );

    /**
     * Private constructor.
     */
    private SoundManager() {
        logger = Core.getLogger();
        logger.info("Started loading sound resources.");

        soundClips = new HashMap<>();
        soundPools = new HashMap<>();

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

        List<Clip> clipPool = new ArrayList<>();
        for (int i = 0; i < POOL_SIZE; i++) {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clipPool.add(clip);
        }
        soundPools.put(sound, clipPool);

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

        for (List<Clip> clipPool : soundPools.values()) {
            for (Clip clip : clipPool) {
                try {
                    FloatControl volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue(newVolume);
                } catch (IllegalArgumentException e) {
                    logger.warning("Failed to set volume: " + e.getMessage());
                }
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
            if (clip != null){
                clip.setFramePosition(0);
                clip.start();
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
    }

    /**
     * Play the sound file.
     *
     * @param sound Key value of sound
     * @param balance Balance value (-1.0 for left, 1.0 for right, 0.0 for center)
     */
    public void playSound(Sound sound, float balance) {
        if (soundEnabled) {
            List<Clip> clipPool = soundPools.get(sound);
            if (clipPool != null) {
                Clip availableClip = clipPool.stream()
                        .filter(clip -> !clip.isRunning())
                        .findFirst()
                        .orElse(null);

                if (availableClip != null) {
                    availableClip.setFramePosition(0);
                    try {
                        if (POSITIONAL_SOUNDS.contains(sound)) {
                            setVolumeBalance(availableClip, balance, sound);
                        }
                        availableClip.start();
                        logger.info("Started playing sound: " + sound + " with balance: " + balance);
                    } catch (Exception e) {
                        logger.warning("Error playing sound: " + sound + ". Error: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    logger.warning("No available clips in pool for sound: " + sound);
                }
            } else {
                logger.warning("Sound not found: " + sound);
            }
        }
    }

    private void setVolumeBalance(Clip clip, float balance, Sound sound) {
        try {
            if (clip.isControlSupported(FloatControl.Type.BALANCE)) {
                FloatControl balanceControl = (FloatControl) clip.getControl(FloatControl.Type.BALANCE);
                balanceControl.setValue(balance);
                logger.info("Set BALANCE: " + balance + " for sound: " + sound);
            } else if (clip.isControlSupported(FloatControl.Type.PAN)) {
                FloatControl panControl = (FloatControl) clip.getControl(FloatControl.Type.PAN);
                panControl.setValue(balance);
                logger.info("Set PAN: " + balance + " for sound: " + sound);
            } else {
                logger.info("No supported balance control. Playing in center for sound: " + sound);
            }
        } catch (Exception e) {
            logger.warning("Failed to set balance for sound: " + sound + ". Error: " + e.getMessage());
        }
    }

    /**
     * Stop the sound file.
     *
     * @param sound Key value of sound
     */
    public void stopSound(Sound sound) {
        if (soundEnabled) {
            Clip clips = soundClips.get(sound);
            if (clips != null && clips.isRunning()) {
                clips.stop();
            } else {
            logger.warning("Sound not playing or not found: " + sound);
            }

            List<Clip> clipPool = soundPools.get(sound);
            if (clipPool != null) {
                clipPool.forEach(clip -> {
                    if (clip.isRunning()) {
                        clip.stop();
                    }
                });
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
            for (List<Clip> clipPool : soundPools.values()) {
                for (Clip clip : clipPool) {
                    if (clip != null) {
                        if (clip.isRunning())
                            clip.stop();
                        clip.close();
                    }
                }
            }
            for (Clip clip : soundClips.values()) {
                if (clip != null) {
                    if (clip.isRunning())
                        clip.stop();
                    clip.close();
                }
            }
        }
    }

}
