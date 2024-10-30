package engine;

import entity.Ship;

/**
 * Implements an object that stores the state of the game between levels.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameState {
	/** Current game level. */
	private int level;
	/** Current score. */
	private int score;
	/** Current ship type. */
	private Ship.ShipType shipType;
	/** Lives currently remaining. */
	private int livesRemaining;
	/** Bullets shot until now. */
	private int bulletsShot;
	/** Ships destroyed until now. */
	private int shipsDestroyed;
	/** Elapsed time */
	private int elapsedTime;
	/** Special enemy appearances alert message */
	private String alertMessage;
    /** Number of consecutive hits */
	private int combo;
	/** Intermediate aggregation variables
	 * max combo, elapsed time and total score
	 * you get from previous level */
	private int maxCombo;
	private int prevTime;
	private int prevScore;

	private int hitBullets;

	/**
	 * Constructor.
	 * 
	 * @param level
	 *            Current game level.
	 * @param score
	 *            Current score.
	 * @param shipType
	 * 		  	  Current ship type.
	 * @param livesRemaining
	 *            Lives currently remaining.
	 * @param bulletsShot
	 *            Bullets shot until now.
	 * @param shipsDestroyed
	 *            Ships destroyed until now.
	 * @param elapsedTime
	 * 			  Elapsed time.
	 * @param alertMessage
	 *  		  Display alert message before a bonus enemy created.
	 * @param combo
	 *            Ships destroyed consequtive.
	 */
	public GameState(final int level, final int score,
			final Ship.ShipType shipType,
			final int livesRemaining, final int bulletsShot,
			final int shipsDestroyed, final int elapsedTime, final String alertMessage, final int combo,
					 final int maxCombo, final int prevTime, final int prevScore, final int hitBullets) {
				
		this.level = level;
		this.score = score;
		this.shipType = shipType;
		this.livesRemaining = livesRemaining;
		this.bulletsShot = bulletsShot;
		this.shipsDestroyed = shipsDestroyed;
		this.elapsedTime = elapsedTime;
		this.alertMessage = alertMessage;
		this.combo = combo;
		this.maxCombo = maxCombo;
		this.prevTime = prevTime;
		this.prevScore = prevScore;
		this.hitBullets = hitBullets;
	}

	public GameState(GameState gameState) {
		this.level = gameState.level;
		this.score = gameState.score;
		this.shipType = gameState.shipType;
		this.livesRemaining = gameState.livesRemaining;
		this.bulletsShot = gameState.bulletsShot;
		this.shipsDestroyed = gameState.shipsDestroyed;
		this.elapsedTime = gameState.elapsedTime;
		this.combo = 0;
		this.maxCombo = gameState.maxCombo;
		this.prevTime = gameState.prevTime;
		this.prevScore = gameState.prevScore;
		this.hitBullets = gameState.hitBullets;
	}


	public GameState(GameState gameState, int nextLevel) {
		this.level = nextLevel;
		this.score = gameState.score;
		this.shipType = gameState.shipType;
		this.livesRemaining = gameState.livesRemaining;
		this.bulletsShot = gameState.bulletsShot;
		this.shipsDestroyed = gameState.shipsDestroyed;
		this.elapsedTime = gameState.elapsedTime;
		this.combo = 0;
		this.maxCombo = gameState.maxCombo;
		this.prevTime = gameState.prevTime;
		this.prevScore = gameState.prevScore;
		this.hitBullets = gameState.hitBullets;
	}


	/**
	 * @return the level
	 */
	public final int getLevel() {
		return level;
	}

	/**
	 * @return the score
	 */
	public final int getScore() {
		return score;
	}

	/**
	 * @return the shipType
	 */
	public final Ship.ShipType getShipType() {
		return shipType;
	}

	/**
	 * @return the livesRemaining
	 */
	public final int getLivesRemaining() {
		return livesRemaining;
	}

	/**
	 * @return the bulletsShot
	 */
	public final int getBulletsShot() {
		return bulletsShot;
	}

	/**
	 * @return the shipsDestroyed
	 */
	public final int getShipsDestroyed() {
		return shipsDestroyed;
	}


	/**
	 * @return the elapsedTime
	 */
	public final int getElapsedTime() { return elapsedTime; }

	/**
	 * @return the alertMessage
	 */
	public final String getAlertMessage() { return alertMessage; }

	public double getAccuracy() {
		if (bulletsShot == 0){
			return 0;
		}
		return ((double) hitBullets / bulletsShot) * 100;
	}

	/**
	 * @return the maxCombo
	 */
	public final int getMaxCombo() { return maxCombo;}

	/**
	 * @return the prevTime/lapTime
	 */
	public final int getPrevTime() { return prevTime;}

	/**
	 * @return the prevScore/tempScore
	 */
	public final int getPrevScore() { return prevScore;}

	public final int getHitBullets() { return hitBullets;}

}

