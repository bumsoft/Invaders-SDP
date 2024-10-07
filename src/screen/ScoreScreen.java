package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import engine.Core;
import engine.GameState;
import engine.Score;
import entity.Wallet;

/**
 * Implements the score screen.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class ScoreScreen extends Screen {

	/** Maximum number of high scores. */
	private static final int MAX_HIGH_SCORE_NUM = 7;


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
	private boolean isNewRecord;
	/** Number of coins earned in the game */
	private int coinsEarned;
	/** Player's name */
	private String name1, name2;

	// coin_lv 별 비율 설정 - lv1, lv2, lv3, lv4 단계 순서로 배열에 넣고 꺼내쓸거임 ex. lv1;score 100 * 0.1
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
	public ScoreScreen(final int width, final int height, final int fps,
			final GameState gameState, final Wallet wallet) {
		super(width, height, fps);

		this.name1 = name1;
		this.name2 = name2;

		this.score = gameState.getScore();
		this.livesRemaining = gameState.getLivesRemaining();
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();

		// 사용자의 coin_lv 가져오기
		int coin_lv = wallet.getCoin_lv();

		// coin_lv에 따라서 다른 비율 적용
		double coin_ratio = COIN_RATIOS[coin_lv-1];

		// 게임 단계 업그레이드 단계 별 점수에 따른 코인 획득 비율 조정
		// - 코인 단위는 정수로 떨어지니까 소수점 이라 반올림 시켜서 int로 변환
		this.coinsEarned = (int)Math.round(gameState.getScore() * coin_ratio);

		// wallet에 획득한 코인 저축
		wallet.deposit(coinsEarned);

		this.isNewRecord = false;

		try {
			this.highScores = Core.getFileManager().loadHighScores();
			if (highScores.size() < MAX_HIGH_SCORE_NUM
					|| highScores.get(highScores.size() - 1).getScore()
					< this.score)
				this.isNewRecord = true;

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
				if (this.isNewRecord)
					saveScore();
			} else if (inputManager.isKeyDown(KeyEvent.VK_SPACE)) {
				// Play again.
				this.returnCode = 2;
				this.isRunning = false;
				if (this.isNewRecord)
					saveScore();
			}

		}

	}

	/**
	 * Saves the score as a high score.
	 */
	private void saveScore() {
		//highScores.add(new Score(new String(this.name1), score));
		Collections.sort(highScores);
		if (highScores.size() > MAX_HIGH_SCORE_NUM)
			highScores.remove(highScores.size() - 1);

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
				this.shipsDestroyed, (float) this.shipsDestroyed
						/ this.bulletsShot, this.isNewRecord, this.coinsEarned);

		drawManager.completeDrawing(this);
	}
}
