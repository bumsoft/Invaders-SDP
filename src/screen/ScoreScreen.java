package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.*;
import entity.Wallet;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 3;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	/** Current score. */
	private int score;
	/** Player lives left. */
	private int livesRemaining;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** List of past high scores. */
	private List<Score> highScores;
	/** Checks if current score is a new high score. */
	private double accuracy;
	private boolean isNewRecord;
	/** Number of coins earned in the game */
	private int coinsEarned;
	/** Player's name */
	private String name1, name2;

	// Set ratios for each coin_lv - placed in an array in the order of lv1, lv2, lv3, lv4, and will be used accordingly,
	// e.g., lv1; score 100 * 0.1
	private static final double[] COIN_RATIOS = {0.1, 0.13, 0.16, 0.19};

	/**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param gameState
	 *            Current game state.
	 */
	public ScoreScreen(final String name1, final int width, final int height, final int fps,
			final GameState gameState, final Wallet wallet, final AchievementManager achievementManager) {
		super(width, height, fps);

		this.name1 = name1;
		this.name2 = name2;

		this.score = gameState.getScore();
		this.livesRemaining = gameState.getLivesRemaining();
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		// Get the user's coin_lv
		int coin_lv = wallet.getCoin_lv();

		// Apply different ratios based on coin_lv
		double coin_ratio = COIN_RATIOS[coin_lv-1];

		// Adjust coin earning ratios based on the game level upgrade stage score
		// Since coins are in integer units, round the decimal points and convert to int
		this.coinsEarned = (int)Math.round(gameState.getScore() * coin_ratio);
		this.coinsEarned += achievementManager.getAchievementReward();

		// deposit the earned coins to wallet
		this.accuracy = gameState.getAccuracy();
		wallet.deposit(coinsEarned);

		soundManager.loopSound(Sound.BGM_GAMEOVER);

		try {
			this.highScores = Core.getFileManager().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
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
		if (this.inputDelay.checkFinished()) {
			if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
				// Return to main menu.
				this.returnCode = 1;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAMEOVER);
				soundManager.playSound(Sound.MENU_BACK);
				saveScore();
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// Play again.
				this.returnCode = 2;
				this.isRunning = false;
				soundManager.stopSound(Sound.BGM_GAMEOVER);
				soundManager.playSound(Sound.MENU_CLICK);
				saveScore();
			}

		}

	}

	/**
	 * Saves the score as a high score.
	 * 중복 방지를 위한 로직 추가.
	 */
	private void saveScore() {
		if (highScores.size() > MAX_HIGH_SCORE_NUM) {
			int index = 0;
			for (Score loadScore : highScores) {
				if (name1.equals(loadScore.getName())) {
					if (score > loadScore.getScore()) {
						highScores.remove(index);
						highScores.add(new Score(name1, score));
						break;
					}
				}
				index += 1;
			}
		} else {
			boolean checkDuplicate = false;
			int index = 0;
			for (Score loadScore : highScores) {
				if (name1.equals(loadScore.getName())) {
					checkDuplicate = true;
					if (score > loadScore.getScore()) {
						highScores.remove(index);
						highScores.add(new Score(name1, score));
						break;
					}
				}
				index += 1;
			}
			if (!checkDuplicate) {
				highScores.add(new Score(name1, score));
			}
		}
		Collections.sort(highScores);
		try {
			Core.getFileManager().saveHighScores(highScores);
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawGameOver(this, this.inputDelay.checkFinished(),
				this.isNewRecord);
		drawManager.drawResults(this, this.score, this.livesRemaining,
				this.shipsDestroyed, this.accuracy, this.isNewRecord, this.coinsEarned);

		drawManager.completeDrawing(this);
	}
}
