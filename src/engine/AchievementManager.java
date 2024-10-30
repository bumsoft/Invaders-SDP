package engine;

import entity.Achievement;

import java.io.IOException;
import java.util.logging.Logger;

public class AchievementManager {

    /** Created AchievementManager class to easily manage achievement-related aspects */

    private static final Logger logger = Logger.getLogger(AchievementManager.class.getName());


    private Achievement achievement;

    // Variables related to Perfect Achievement
    private static int currentPerfectLevel;
    private final int MAX_PERFECT_STAGE = 7;

    // Variables related to Accuracy Achievement
    private int highMaxCombo; // List of accuracy achievements

    // Variables related to Flawless Failure Achievement
    private boolean checkFlawlessFailure;

    // Coin rewards update
    private int coinReward = 0;

    private final int[] COMBO_COIN_REWARD = {500, 1500, 2000, 2500};
    private final int[] PERFECT_COIN_REWARD = {200, 400, 800, 2000, 3000, 4000, 5000};
    private final int FLAWLESS_FAILURE_COIN = 1000;
    private final int PLAY_TIME_COIN = 1000;

    private boolean checkPlayTimeAch;


    // Variables needed for each achievement are loaded through a file.
    public AchievementManager() throws IOException {
        achievement = FileManager.getInstance().loadAchievement();
        this.currentPerfectLevel = achievement.getPerfectStage();
        this.highMaxCombo = achievement.getHighmaxCombo();
        this.checkFlawlessFailure = achievement.getFlawlessFailure();
    }

    public int getAchievementReward() {
        return coinReward;
    }

    public void updateTotalPlayTime(int playTime) {
        if (achievement.getTotalPlayTime() < 600 && achievement.getTotalPlayTime() + playTime >= 600) {
            coinReward += PLAY_TIME_COIN;
        }
        achievement.setTotalPlayTime(playTime);
    }

    public void updateTotalScore(int score) {
        achievement.setTotalScore(score);
    }

    /**
     * Function to update the accuracy achievement.
     */
    public void updateMaxCombo(int maxCombo) {
        if (highMaxCombo >= maxCombo) {
            return;
        }
        int maxComboGoal = 10;
        if (highMaxCombo < 10) {
            maxComboGoal = 10;
        } else if (highMaxCombo < 15) {
            maxComboGoal = 15;
        } else if (highMaxCombo < 20) {
            maxComboGoal = 20;
        } else if (highMaxCombo < 25) {
            maxComboGoal = 25;
        }
        int rewardIndex = highMaxCombo / 5 - 1 <= 9 ? 0 : highMaxCombo / 5 - 1;
        highMaxCombo = maxCombo;
        if (highMaxCombo < maxComboGoal) {
            achievement.setHighMaxcombo(highMaxCombo);
            return;
        }
        // When an accuracy achievement is reached, all lower achievements are achieved together.
        if (highMaxCombo >= 25) {
            for (int i = rewardIndex; i < 4; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 20) {
            for (int i = rewardIndex; i < 3; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 15) {
            for (int i = rewardIndex; i < 2; i++) {
                coinReward += COMBO_COIN_REWARD[i];
            }
        } else if (highMaxCombo >= 10) {
            coinReward += COMBO_COIN_REWARD[0];
        }
        // Save the updated achievement.
        achievement.setHighMaxcombo(highMaxCombo);
    }
    /**
     * Check if the perfect achievement has been reached.
     */
    public void updatePerfect(final int MAX_LIVES, int checkLives, int gameLevel) {
        if (checkLives >= MAX_LIVES && currentPerfectLevel < MAX_PERFECT_STAGE && gameLevel > currentPerfectLevel) {
            // Check if the current perfect stage has not exceeded the total stages.
            currentPerfectLevel += 1;
            coinReward += PERFECT_COIN_REWARD[currentPerfectLevel-1];
            achievement.setCurrentPerfectStage(currentPerfectLevel);
        }
    }

    public void updateFlawlessFailure(double accuracy) {
        if (!checkFlawlessFailure && accuracy <= 0) {
            checkFlawlessFailure = true;
            coinReward += FLAWLESS_FAILURE_COIN;
            achievement.setFlawlessFailure(true);
        }
    }

    public void updateAllAchievements() throws IOException {
        FileManager.getInstance().saveAchievement(achievement);
    }

    public void updatePlaying(int maxCombo ,int playtime, int max_lives, int LivesRemaining, int level ) throws IOException{
        updateTotalPlayTime(playtime);
        updatePerfect(max_lives,LivesRemaining,level);
        updateMaxCombo(maxCombo);
    }

    public void updatePlayed(double accuracy, int score) throws IOException{
        updateTotalScore(score);
        updateFlawlessFailure(accuracy);
    }
}