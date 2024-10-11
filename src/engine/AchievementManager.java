package engine;

import entity.Achievement;
import entity.Wallet;

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
    private double highAccuracy; // List of accuracy achievements

    // Variables related to Flawless Failure Achievement
    private boolean checkFlawlessFailure;

    // Variables related to Best Friends Achievement
    private boolean checkBestFriends;

    // Coin rewards update
    private int coinReward = 0;

    private final int[] ACCURACY_COIN_REWARD = {500, 1500, 2000, 2500};
    private final int[] PERFECT_COIN_REWARD = {200, 400, 800, 2000, 3000, 4000, 5000};
    private final int FLAWLESS_FAILURE_COIN = 1000;
    private final int BEST_FRIENDS_COIN = 1000;

    // Variables needed for each achievement are loaded through a file.
    public AchievementManager() throws IOException {
        achievement = FileManager.getInstance().loadAchievement();
        this.currentPerfectLevel = achievement.getPerfectStage();
        this.highAccuracy = achievement.getHighAccuracy();
        this.checkFlawlessFailure = achievement.getFlawlessFailure();
        this.checkBestFriends = achievement.getBestFriends();
    }

    public int getAchievementReward() {
        return coinReward;
    }

    public void updateTotalPlayTime(int playTime) {
        achievement.setTotalPlayTime(playTime);
    }

    public void updateTotalScore(int score) {
        achievement.setTotalScore(score);
    }

    /**
     * Function to update the accuracy achievement.
     */
    public void updateAccuracy(double accuracy) {
        if (highAccuracy >= accuracy) {
            return;
        }
        int accuracyGoal = (int)(highAccuracy/10)*10+10;
        highAccuracy = accuracy;
        if (accuracyGoal < 70) {
            accuracyGoal = 70;
        }
        if (highAccuracy < accuracyGoal) {
            achievement.setHighAccuracy(highAccuracy);
            return;
        }
        int rewardIndex = accuracyGoal/10-7;
        // When an accuracy achievement is reached, all lower achievements are achieved together.
        if (highAccuracy >= 100) {
            for (int i = rewardIndex; i < 4; i++) {
                coinReward += ACCURACY_COIN_REWARD[i];
            }
        } else if (highAccuracy >= 90) {
            for (int i = rewardIndex; i < 3; i++) {
                coinReward += ACCURACY_COIN_REWARD[i];
            }
        } else if (highAccuracy >= 80) {
            for (int i = rewardIndex; i < 2; i++) {
                coinReward += ACCURACY_COIN_REWARD[i];
            }
        } else if (highAccuracy >= 70) {
            coinReward += ACCURACY_COIN_REWARD[0];
        }
        // Save the updated achievement.
        achievement.setHighAccuracy(highAccuracy);
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

    public void updateBestFriends(boolean checkTwoPlayMode) {
        if (!checkBestFriends && checkTwoPlayMode) {
            checkBestFriends = true;
            coinReward += BEST_FRIENDS_COIN;
            achievement.setBestFriends(true);
        }
    }

    public void updateAllAchievements() throws IOException {
        FileManager.getInstance().saveAchievement(achievement);
    }

    public void updatePlaying(int playtime, int max_lives, int LivesRemaining, int level ) throws IOException{
        updateTotalPlayTime(playtime);
        updatePerfect(max_lives,LivesRemaining,level);
    }

    public void updatePlayed(double accuracy, int score, boolean multiPlay) throws IOException{
        updateAccuracy(accuracy);
        updateTotalScore(score);
        updateFlawlessFailure(accuracy);
        updateBestFriends(multiPlay);
    }
}