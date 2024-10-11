package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;

import engine.AchievementManager;
import engine.Core;
import engine.Score;
import engine.Sound;
import engine.SoundManager;

/**
 * Implements the achievement screen.
 *
 * Team NOF
 * 
 */
public class AchievementScreen extends Screen {

	/** List of past high scores. */
	private List<Score> highScores;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private int totalScore;
	private int totalPlayTime;
	private int currentPerfectStage;
	private double accuracy;
	private boolean checkFlawlessFailure;
	private boolean checkBestFriends;

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public AchievementScreen(final int width, final int height, final int fps, final AchievementManager achievementManager) {
		super(width, height, fps);

		this.returnCode = 1;

		try {
			this.highScores = Core.getFileManager().loadHighScores();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load high scores!");
		}
		try {
			this.totalScore = Core.getFileManager().loadAchievement().getTotalScore();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load total scores!");
		}

		try {
			this.totalPlayTime = Core.getFileManager().loadAchievement().getTotalPlayTime();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load total play time!");
		}
		try {
			this.currentPerfectStage = Core.getFileManager().loadAchievement().getPerfectStage();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load current perfect stage");
		}
		try {
			this.accuracy = Core.getFileManager().loadAchievement().getHighAccuracy();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load Current accuracy achievement");
		}
		try {
			this.checkFlawlessFailure = Core.getFileManager().loadAchievement().getFlawlessFailure();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load flawless failure achievement");
		}
		try {
			this.checkBestFriends = Core.getFileManager().loadAchievement().getBestFriends();
		} catch (NumberFormatException | IOException e) {
			logger.warning("Couldn't load best friends achievement");
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();

		draw();
		if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)
				&& this.inputDelay.checkFinished()) {
			this.isRunning = false;
			soundManager.playSound(Sound.MENU_BACK);
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawAchievementMenu(this, this.totalScore, this.totalPlayTime,
				this.accuracy, this.currentPerfectStage, this.currentPerfectStage+1,
				this.checkFlawlessFailure, this.checkBestFriends);
		drawManager.drawHighScores(this, this.highScores);
		drawManager.completeDrawing(this);
	}
}
