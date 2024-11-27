package entity;

public class Achievement {

    private int totalPlayTime;
    private int totalScore;
    private int maxCombo;
    private int currentPerfectStage;
    private boolean flawlessFailure;


    public Achievement(int totalPlayTime, int totalScore, int maxCombo, int currentPerfectStage,
                       boolean flawlessFailure) {
        this.totalPlayTime = totalPlayTime;
        this.totalScore = totalScore;
        this.maxCombo = maxCombo;
        this.currentPerfectStage = currentPerfectStage;
        this.flawlessFailure = flawlessFailure;
    }

    // Functions to get the status of each achievement.
    public int getTotalPlayTime() { return totalPlayTime; }
    public int getTotalScore() { return totalScore; }
    public int getHighmaxCombo() { return maxCombo; }
    public int getPerfectStage() { return currentPerfectStage; }
    public boolean getFlawlessFailure() { return flawlessFailure; }

}