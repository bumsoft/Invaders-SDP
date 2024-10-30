package screen;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.Timer;
import java.util.TimerTask;


import engine.*;
import entity.*;


/**
 * Implements the game screen, where the action happens.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameScreen extends Screen implements Callable<GameState> {

	/** Milliseconds until the screen accepts user input. */
	private static final int INPUT_DELAY = 6000;
	/** Bonus score for each life remaining at the end of the level. */
	private static final int LIFE_SCORE = 100;
	/** Minimum time between bonus ship's appearances. */
	private static final int BONUS_SHIP_INTERVAL = 20000;
	/** Maximum variance in the time between bonus ship's appearances. */
	private static final int BONUS_SHIP_VARIANCE = 10000;
	/** Time until bonus ship explosion disappears. */
	private static final int BONUS_SHIP_EXPLOSION = 500;
	/** Time from finishing the level to screen change. */
	private static final int SCREEN_CHANGE_INTERVAL = 1500;
	/** Height of the interface separation line. */
	private static final int SEPARATION_LINE_HEIGHT = 40;

	/** Current game difficulty settings. */
	private GameSettings gameSettings;
	/** Current difficulty level number. */
	private int level;
	/** Formation of enemy ships. */
	private EnemyShipFormation enemyShipFormation;
	/** Player's ship. */
	private Ship ship;
	/** Bonus enemy ship that appears sometimes. */
	private EnemyShip enemyShipSpecial;
	/** Minimum time between bonus ship appearances. */
	private Cooldown enemyShipSpecialCooldown;
	/** Time until bonus ship explosion disappears. */
	private Cooldown enemyShipSpecialExplosionCooldown;
	/** Time from finishing the level to screen change. */
	private Cooldown screenFinishedCooldown;
	private Cooldown shootingCooldown;
	/** Set of all bullets fired by on screen ships. */
	private Set<Bullet> bullets;
	/** Current score. */
	private String name1;

	private int score;
	/** tempScore records the score up to the previous level. */
	private int tempScore;
	/** Current ship type. */
	private Ship.ShipType shipType;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Number of consecutive hits.
	 * maxCombo records the maximum value of combos in that level. */
	private int combo;
	private int maxCombo;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	/** Player number for two player mode **/
	private int playerNumber;
	/** list of highScores for find recode. */
	private List<Score>highScores;
	/** Elapsed time while playing this game.
	 * lapTime records the time to the previous level. */
	private int elapsedTime;
	private int lapTime;
	/** Keep previous timestamp. */
	private Integer prevTime;
	/** Alert Message when a special enemy appears. */
	private String alertMessage;
	/** checks if it's executed. */
  	private boolean isExecuted = false;
	/** timer.. */
	private Timer timer;
	private TimerTask timerTask;
	/** Spider webs restricting player movement */
	private List<Web> web;
	/**
	 * Obstacles preventing a player's bullet
	 */
	private List<Block> block;

	private Wallet wallet;
	/* Blocker appearance cooldown */
	private Cooldown blockerCooldown;
	/* Blocker visible time */
	private Cooldown blockerVisibleCooldown;
	/* Is Blocker visible */
	private boolean blockerVisible;
	private Random random;
	private List<Blocker> blockers = new ArrayList<>();
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();
	/** Singleton instance of ItemManager. */
	private ItemManager itemManager;
	/** Item boxes that dropped when kill enemy ships. */
	private Set<ItemBox> itemBoxes;
	/** Barriers appear in game screen. */
	private Set<Barrier> barriers;
	/** Sound balance for each player*/
	private float balance = 0.0f;

	private int MAX_BLOCKERS = 0;

	private GameState gameState;

	private int hitBullets;

    /**
	 * Constructor, establishes the properties of the screen.
	 * 
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 */
	public GameScreen(final GameState gameState,
			final GameSettings gameSettings, final boolean bonusLife,
			final int width, final int height, final int fps, final Wallet wallet) {
		super(width, height, fps);

		this.gameSettings = gameSettings;
		this.gameState = gameState;
		this.bonusLife = bonusLife;
		this.level = gameState.getLevel();
		this.score = gameState.getScore();
		this.elapsedTime = gameState.getElapsedTime();
		this.alertMessage = gameState.getAlertMessage();
		this.shipType = gameState.getShipType();
		this.lives = gameState.getLivesRemaining();
		if (this.bonusLife)
			this.lives++;
		this.bulletsShot = gameState.getBulletsShot();
		this.shipsDestroyed = gameState.getShipsDestroyed();
		this.playerNumber = -1;
		this.maxCombo = gameState.getMaxCombo();
		this.lapTime = gameState.getPrevTime();
		this.tempScore = gameState.getPrevScore();

		this.hitBullets = gameState.getHitBullets();

		try {
			this.highScores = Core.getFileManager().loadHighScores();

		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}

		this.wallet = wallet;


		this.random = new Random();
		this.blockerVisible = false;
		this.blockerCooldown = Core.getVariableCooldown(10000, 14000);
		this.blockerCooldown.reset();
		this.blockerVisibleCooldown = Core.getCooldown(20000);

		try {
			this.highScores = Core.getFileManager().loadHighScores();
		} catch (IOException e) {
			logger.warning("Couldn't load high scores!");
		}
		this.alertMessage = "";

		this.wallet = wallet;
	}

	/**
	 * Constructor, establishes the properties of the screen for two player mode.
	 *
	 * @param gameState
	 *            Current game state.
	 * @param gameSettings
	 *            Current game settings.
	 * @param bonusLife
	 *            Checks if a bonus life is awarded this level.
	 * @param width
	 *            Screen width.
	 * @param height
	 *            Screen height.
	 * @param fps
	 *            Frames per second, frame rate at which the game is run.
	 * @param playerNumber
	 *            Player number for two player mode
	 */
	public GameScreen(final GameState gameState,
					  final GameSettings gameSettings, final boolean bonusLife,
					  final int width, final int height, final int fps, final Wallet wallet,
					  final int playerNumber) {
		this(gameState, gameSettings, bonusLife, width, height, fps, wallet);
		this.playerNumber = playerNumber;
		this.balance = switch (playerNumber) {
			case 0: yield -1.0f; // 1P
			case 1: yield 1.0f;  // 2P
			default: yield 0.0f; // default
		};
	}

	/**
	 * Initializes basic screen properties, and adds necessary elements.
	 */
	public final void initialize() {
		super.initialize();

		enemyShipFormation = new EnemyShipFormation(this.gameSettings, this.gameState);
		enemyShipFormation.attach(this);
        // Appears each 10-30 seconds.
        this.ship = ShipFactory.create(this.shipType, this.width / 2, this.height - 30);
		logger.info("Player ship created " + this.shipType + " at " + this.ship.getPositionX() + ", " + this.ship.getPositionY());
        ship.applyItem(wallet);
		//Create random Spider Web.
		int web_count = 1 + level / 3;
		web = new ArrayList<>();
		for(int i = 0; i < web_count; i++) {
			double randomValue = Math.random();
			this.web.add(new Web((int) Math.max(0, randomValue * width - 12 * 2), this.height - 30));
			this.logger.info("Spider web creation location : " + web.get(i).getPositionX());
		}
		//Create random Block.
		int blockCount = level / 2;
		int playerTopY_contain_barrier = this.height - 40 - 150;
		int enemyBottomY = 100 + (gameSettings.getFormationHeight() - 1) * 48;
		this.block = new ArrayList<Block>();
		for (int i = 0; i < blockCount; i++) {
			Block newBlock;
			boolean overlapping;
			do {
				newBlock = new Block(0,0);
				int positionX = (int) (Math.random() * (this.width - newBlock.getWidth()));
				int positionY = (int) (Math.random() * (playerTopY_contain_barrier - enemyBottomY - newBlock.getHeight())) + enemyBottomY;
				newBlock = new Block(positionX, positionY);
				overlapping = false;
				for (Block block : block) {
					if (checkCollision(newBlock, block)) {
						overlapping = true;
						break;
					}
				}
			} while (overlapping);
			block.add(newBlock);
		}



		// Appears each 10-30 seconds.
		this.enemyShipSpecialCooldown = Core.getVariableCooldown(
				BONUS_SHIP_INTERVAL, BONUS_SHIP_VARIANCE);
		this.enemyShipSpecialCooldown.reset();
		this.enemyShipSpecialExplosionCooldown = Core
				.getCooldown(BONUS_SHIP_EXPLOSION);
		this.screenFinishedCooldown = Core.getCooldown(SCREEN_CHANGE_INTERVAL);
		this.bullets = new HashSet<>();
		this.barriers = new HashSet<>();
        this.itemBoxes = new HashSet<>();
		this.itemManager = new ItemManager(this.ship, this.enemyShipFormation, this.barriers, this.height, this.width, this.balance);

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
		if (soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.stopSound(Sound.BGM_MAIN);
		soundManager.playSound(Sound.COUNTDOWN);

		switch (this.level) {
			case 1: soundManager.loopSound(Sound.BGM_LV1); break;
			case 2: soundManager.loopSound(Sound.BGM_LV2); break;
			case 3: soundManager.loopSound(Sound.BGM_LV3); break;
			case 4: soundManager.loopSound(Sound.BGM_LV4); break;
			case 5: soundManager.loopSound(Sound.BGM_LV5); break;
			case 6: soundManager.loopSound(Sound.BGM_LV6); break;
            case 7:
				// From level 7 and above, it continues to play at BGM_LV7.
            default: soundManager.loopSound(Sound.BGM_LV7); break;
		}
	}

	/**
	 * Starts the action.
	 * 
	 * @return Next screen code.
	 */
	public final int run() {
		super.run();

		this.score += LIFE_SCORE * (this.lives - 1);
		if(this.lives == 0) this.score += 100;
		this.logger.info("Screen cleared with a score of " + this.score);

		return this.returnCode;
	}

	/**
	 * Updates the elements on screen and checks for events.
	 */
	protected final void update() {
		super.update();
		if (this.inputDelay.checkFinished() && !this.levelFinished) {
			boolean player1Attacking = inputManager.isKeyDown(KeyEvent.VK_SPACE);
			boolean player2Attacking = inputManager.isKeyDown(KeyEvent.VK_SHIFT);

			if (player1Attacking && player2Attacking) {
				// Both players are attacking
				if (this.ship.shoot(this.bullets, this.itemManager.getShotNum()))
					this.bulletsShot += this.itemManager.getShotNum();
			} else {
				switch (playerNumber) {
					case 1:
						if (player2Attacking) {
							if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), 1.0f)) // Player 1 attack
								this.bulletsShot += this.itemManager.getShotNum();
						}
						break;
					case 0:
						if (player1Attacking) {
							if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), -1.0f)) // Player 1 attack
								this.bulletsShot += this.itemManager.getShotNum();
						}
						break;
					default: //playerNumber = -1
						if (player1Attacking) {
							if (this.ship.shoot(this.bullets, this.itemManager.getShotNum(), 0.0f)) // Player 1 attack
								this.bulletsShot += this.itemManager.getShotNum();
						}
						break;
				}
			}
			/*Elapsed Time Update*/
			long currentTime = System.currentTimeMillis();

			if (this.prevTime != null)
				this.elapsedTime += (int) (currentTime - this.prevTime);

			this.prevTime = (int) currentTime;

			if(!itemManager.isGhostActive())
				this.ship.setColor(Color.GREEN);

			if (!this.ship.isDestroyed()) {
				boolean moveRight;
				boolean moveLeft;
				switch (playerNumber) {
					case 0:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_D);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_A);
						break;
					case 1:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT);
						break;
					default:
						moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
								|| inputManager.isKeyDown(KeyEvent.VK_D);
						moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
								|| inputManager.isKeyDown(KeyEvent.VK_A);
				}

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					if (playerNumber == -1)
						this.ship.moveRight();
					else
						this.ship.moveRight(balance);
				}
				if (moveLeft && !isLeftBorder) {
					if (playerNumber == -1)
						this.ship.moveLeft();
					else
						this.ship.moveLeft(balance);
				}
				for(int i = 0; i < web.size(); i++) {
					//escape Spider Web
					if (ship.getPositionX() + 6 <= web.get(i).getPositionX() - 6
							|| web.get(i).getPositionX() + 6 <= ship.getPositionX() - 6) {
						this.ship.setThreadWeb(false);
					}
					//get caught in a spider's web
					else {
						this.ship.setThreadWeb(true);
						break;
					}
				}
			}

			if (this.enemyShipSpecial != null) {
				if (!this.enemyShipSpecial.isDestroyed())
					this.enemyShipSpecial.move(2, 0);
				else if (this.enemyShipSpecialExplosionCooldown.checkFinished())
					this.enemyShipSpecial = null;

			}
			if (this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkFinished()) {
				this.enemyShipSpecial = new EnemyShip();
				this.alertMessage = "";
				this.enemyShipSpecialCooldown.reset();
				soundManager.playSound(Sound.UFO_APPEAR, balance);
				this.logger.info("A special ship appears");
			}
			if(this.enemyShipSpecial == null
					&& this.enemyShipSpecialCooldown.checkAlert()) {
				switch (this.enemyShipSpecialCooldown.checkAlertAnimation()){
					case 1: this.alertMessage = "--! ALERT !--";
						break;

					case 2: this.alertMessage = "-!! ALERT !!-";
						break;

					case 3: this.alertMessage = "!!! ALERT !!!";
						break;

					default: this.alertMessage = "";
						break;
				}

			}
			if (this.enemyShipSpecial != null
					&& this.enemyShipSpecial.getPositionX() > this.width) {
				this.enemyShipSpecial = null;
				this.logger.info("The special ship has escaped");
			}

			this.ship.update();

			// If Time-stop is active, Stop updating enemy ships' move and their shoots.
			if (!itemManager.isTimeStopActive()) {
				this.enemyShipFormation.update();
				this.enemyShipFormation.shoot(this.bullets, this.level, balance);
			}

			if (level >= 3) { //Events where vision obstructions appear start from level 3 onwards.
				handleBlockerAppearance();
			}
		}

		manageCollisions();
		cleanBullets();
		if (playerNumber >= 0)
			drawThread();
		else
			draw();

		if ((this.enemyShipFormation.isEmpty() || this.lives <= 0)
				&& !this.levelFinished) {
			this.levelFinished = true;

			soundManager.stopSound(soundManager.getCurrentBGM());
			if (this.lives == 0)
				soundManager.playSound(Sound.GAME_END);
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished()) {
			//Reset alert message when level is finished
			this.alertMessage = "";
			this.isRunning = false;
		}
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawGameTitle(this);

		drawManager.drawLaunchTrajectory( this,this.ship.getPositionX());

		drawManager.drawEntity(this.ship, this.ship.getPositionX(), this.ship.getPositionY());

		//draw Spider Web
		for (int i = 0; i < web.size(); i++) {
			drawManager.drawEntity(this.web.get(i), this.web.get(i).getPositionX(),
					this.web.get(i).getPositionY());
		}
		//draw Blocks
		for (Block block : block)
			drawManager.drawEntity(block, block.getPositionX(),
					block.getPositionY());


		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY());

		enemyShipFormation.draw();

		for (ItemBox itemBox : this.itemBoxes)
			drawManager.drawEntity(itemBox, itemBox.getPositionX(), itemBox.getPositionY());

		for (Barrier barrier : this.barriers)
			drawManager.drawEntity(barrier, barrier.getPositionX(), barrier.getPositionY());

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());


		// Interface.
		drawManager.drawScore(this, this.score);
		drawManager.drawElapsedTime(this, this.elapsedTime);
		drawManager.drawAlertMessage(this, this.alertMessage);
		drawManager.drawLives(this, this.lives, this.shipType);
		drawManager.drawLevel(this, this.level);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1);
		drawManager.drawReloadTimer(this,this.ship,ship.getRemainingReloadTime());
		drawManager.drawCombo(this,this.combo);


		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown, this.bonusLife);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height / 12);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height / 12);

			//Intermediate aggregation
			if (this.level > 1){
                if (countdown == 0) {
					//Reset mac combo and edit temporary values
                    this.lapTime = this.elapsedTime;
                    this.tempScore = this.score;
                    this.maxCombo = 0;
                } else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
                    drawManager.interAggre(this, this.level - 1, this.maxCombo, this.elapsedTime, this.lapTime, this.score, this.tempScore);
                }
			}
		}


		//add drawRecord method for drawing
		drawManager.drawRecord(highScores,this);


		// Blocker drawing part
		if (!blockers.isEmpty()) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, blocker.getPositionX(), blocker.getPositionY(), blocker.getAngle());
			}
		}

		drawManager.completeDrawing(this);
	}


	// Methods that handle the position, angle, sprite, etc. of the blocker (called repeatedly in update.)
	private void handleBlockerAppearance() {

		if (level >= 3 && level < 6) MAX_BLOCKERS = 1;
		else if (level >= 6 && level < 11) MAX_BLOCKERS = 2;
		else if (level >= 11) MAX_BLOCKERS = 3;

		int kind = random.nextInt(2-1 + 1) +1; // 1~2
		DrawManager.SpriteType newSprite;
		switch (kind) {
			case 1:
				newSprite = DrawManager.SpriteType.Blocker1; // artificial satellite
				break;
			case 2:
				newSprite = DrawManager.SpriteType.Blocker2; // astronaut
				break;
			default:
				newSprite = DrawManager.SpriteType.Blocker1;
				break;
		}

		// Check number of blockers, check timing of exit
		if (blockers.size() < MAX_BLOCKERS && blockerCooldown.checkFinished()) {
			boolean moveLeft = random.nextBoolean(); // Randomly sets the movement direction of the current blocker
			int startY = random.nextInt(this.height - 90) + 25; // Random Y position with margins at the top and bottom of the screen
			int startX = moveLeft ? this.width + 300 : -300; // If you want to move left, outside the right side of the screen, if you want to move right, outside the left side of the screen.
			// Add new Blocker
			if (moveLeft) {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from right to left
			} else {
				blockers.add(new Blocker(startX, startY, newSprite, moveLeft)); // move from left to right
			}
			blockerCooldown.reset();
		}

		// Items in the blocker list that will disappear after leaving the screen
		List<Blocker> toRemove = new ArrayList<>();
		for (int i = 0; i < blockers.size(); i++) {
			Blocker blocker = blockers.get(i);

			// If the blocker leaves the screen, remove it directly from the list.
			if (blocker.getMoveLeft() && blocker.getPositionX() < -300 || !blocker.getMoveLeft() && blocker.getPositionX() > this.width + 300) {
				blockers.remove(i);
				i--; // When an element is removed from the list, the index must be decreased by one place.
				continue;
			}

			// Blocker movement and rotation (positionX, Y value change)
			if (blocker.getMoveLeft()) {
				blocker.move(-1.5, 0); // move left
			} else {
				blocker.move(1.5, 0); // move right
			}
			blocker.rotate(0.2); // Blocker rotation
		}

		// Remove from the blocker list that goes off screen
		blockers.removeAll(toRemove);
	}

	/**
	 * Draws the elements associated with the screen to thread buffer.
	 */
	private void drawThread() {
		drawManager.initThreadDrawing(this, playerNumber);
		drawManager.drawGameTitle(this, playerNumber);

		drawManager.drawLaunchTrajectory( this,this.ship.getPositionX(), playerNumber);

		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY(), playerNumber);

		//draw Spider Web
		for (int i = 0; i < web.size(); i++) {
			drawManager.drawEntity(this.web.get(i), this.web.get(i).getPositionX(),
					this.web.get(i).getPositionY(), playerNumber);
		}
		//draw Blocks
		for (Block block : block)
			drawManager.drawEntity(block, block.getPositionX(),
					block.getPositionY(), playerNumber);

		if (this.enemyShipSpecial != null)
			drawManager.drawEntity(this.enemyShipSpecial,
					this.enemyShipSpecial.getPositionX(),
					this.enemyShipSpecial.getPositionY(), playerNumber);

		enemyShipFormation.draw(playerNumber);

		for (ItemBox itemBox : this.itemBoxes)
			drawManager.drawEntity(itemBox, itemBox.getPositionX(), itemBox.getPositionY(), playerNumber);

		for (Barrier barrier : this.barriers)
			drawManager.drawEntity(barrier, barrier.getPositionX(), barrier.getPositionY(), playerNumber);

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY(), playerNumber);

		// Interface.
		drawManager.drawScore(this, this.score, playerNumber);
		drawManager.drawElapsedTime(this, this.elapsedTime, playerNumber);
		drawManager.drawAlertMessage(this, this.alertMessage, playerNumber);
		drawManager.drawLives(this, this.lives, this.shipType, playerNumber);
		drawManager.drawLevel(this, this.level, playerNumber);
		drawManager.drawHorizontalLine(this, SEPARATION_LINE_HEIGHT - 1, playerNumber);
		drawManager.drawReloadTimer(this,this.ship,ship.getRemainingReloadTime(), playerNumber);
		drawManager.drawCombo(this,this.combo, playerNumber);

		// Show GameOver if one player ends first
		if (this.levelFinished && this.screenFinishedCooldown.checkFinished() && this.lives <= 0) {
			drawManager.drawInGameOver(this, this.height, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12, playerNumber);
		}

		// Countdown to game start.
		if (!this.inputDelay.checkFinished()) {
			int countdown = (int) ((INPUT_DELAY - (System.currentTimeMillis() - this.gameStartTime)) / 1000);
			drawManager.drawCountDown(this, this.level, countdown,
					this.bonusLife, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 - this.height
					/ 12, playerNumber);
			drawManager.drawHorizontalLine(this, this.height / 2 + this.height
					/ 12, playerNumber);

			//Intermediate aggregation
			if (this.level > 1){
				if (countdown == 0) {
					//Reset mac combo and edit temporary values
					this.lapTime = this.elapsedTime;
					this.tempScore = this.score;
					this.maxCombo = 0;
				} else {
					// Don't show it just before the game starts, i.e. when the countdown is zero.
					drawManager.interAggre(this, this.level - 1, this.maxCombo, this.elapsedTime, this.lapTime, this.score, this.tempScore, playerNumber);
				}
			}
		}

		//add drawRecord method for drawing
		drawManager.drawRecord(highScores,this, playerNumber);

		// Blocker drawing part
		if (!blockers.isEmpty()) {
			for (Blocker blocker : blockers) {
				drawManager.drawRotatedEntity(blocker, blocker.getPositionX(), blocker.getPositionY(), blocker.getAngle(), playerNumber);
			}
		}

		drawManager.flushBuffer(this, playerNumber);
	}

	/**
	 * Cleans bullets that go off screen.
	 */
	private void cleanBullets() {
		Set<Bullet> recyclable = new HashSet<Bullet>();
		for (Bullet bullet : this.bullets) {
			bullet.update();
			if (bullet.getPositionY() < SEPARATION_LINE_HEIGHT
					|| bullet.getPositionY() > this.height)
				recyclable.add(bullet);
		}
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Manages collisions between bullets and ships.
	 */
	private void manageCollisions() {
		Set<Bullet> recyclable = new HashSet<Bullet>();

		if (isExecuted == false){
			isExecuted = true;
			timer = new Timer();
			timerTask = new TimerTask() {
				public void run() {
					combo = 0;
				}
			};
			timer.schedule(timerTask, 3000);
		}

		int topEnemyY = Integer.MAX_VALUE;
		for (EnemyShip enemyShip : this.enemyShipFormation) {
			if (enemyShip != null && !enemyShip.isDestroyed() && enemyShip.getPositionY() < topEnemyY) {
				topEnemyY = enemyShip.getPositionY();
			}
		}
		if (this.enemyShipSpecial != null && !this.enemyShipSpecial.isDestroyed() && this.enemyShipSpecial.getPositionY() < topEnemyY) {
			topEnemyY = this.enemyShipSpecial.getPositionY();
		}

		for (Bullet bullet : this.bullets) {

			// Enemy ship's bullets
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished && !itemManager.isGhostActive()) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy(balance);
						lvdamage();
						this.logger.info("Hit on player ship, " + this.lives + " lives remaining.");
					}
				}

				if (this.barriers != null) {
					Iterator<Barrier> barrierIterator = this.barriers.iterator();
					while (barrierIterator.hasNext()) {
						Barrier barrier = barrierIterator.next();
						if (checkCollision(bullet, barrier)) {
							recyclable.add(bullet);
							barrier.reduceHealth(balance);
							if (barrier.isDestroyed()) {
								barrierIterator.remove();
							}
						}
					}
				}

			} else {	// Player ship's bullets
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (enemyShip != null && !enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						// Decide whether to destroy according to physical strength
						this.enemyShipFormation.HealthManageDestroy(enemyShip, balance);
						// If the enemy doesn't die, the combo increases;
						// if the enemy dies, both the combo and score increase.
						this.score += Score.comboScore(this.enemyShipFormation.getPoint(), this.combo);
						this.shipsDestroyed += this.enemyShipFormation.getDistroyedship();
						this.combo++;
						this.hitBullets++;
						if (this.combo > this.maxCombo) this.maxCombo = this.combo;
						timer.cancel();
						isExecuted = false;
						recyclable.add(bullet);

						if (enemyShip.getHealth() < 0 && itemManager.dropItem()) {
							this.itemBoxes.add(new ItemBox(enemyShip.getPositionX() + 6, enemyShip.getPositionY() + 1, balance));
							logger.info("Item box dropped");
						}
					}

				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					this.score += Score.comboScore(this.enemyShipSpecial.getPointValue(), this.combo);
					this.shipsDestroyed++;
					this.combo++;
					this.hitBullets++;
					if (this.combo > this.maxCombo) this.maxCombo = this.combo;
					this.enemyShipSpecial.destroy(balance);
					this.enemyShipSpecialExplosionCooldown.reset();
					timer.cancel();
					isExecuted = false;

					recyclable.add(bullet);
				}

				if (this.itemManager.getShotNum() == 1 && bullet.getPositionY() < topEnemyY) {
					this.combo = 0;
					isExecuted = true;
				}

				Iterator<ItemBox> itemBoxIterator = this.itemBoxes.iterator();
				while (itemBoxIterator.hasNext()) {
					ItemBox itemBox = itemBoxIterator.next();
					if (checkCollision(bullet, itemBox) && !itemBox.isDroppedRightNow()) {
						this.hitBullets++;
						itemBoxIterator.remove();
						recyclable.add(bullet);
						Entry<Integer, Integer> itemResult = this.itemManager.useItem();

						if (itemResult != null) {
							this.score += itemResult.getKey();
							this.shipsDestroyed += itemResult.getValue();
						}
					}
				}

				//check the collision between the obstacle and the bullet
				for (Block block : this.block) {
					if (checkCollision(bullet, block)) {
						recyclable.add(bullet);
                        soundManager.playSound(Sound.BULLET_BLOCKING, balance);
						break;
					}
				}
			}
		}

		//check the collision between the obstacle and the enemyship
		Set<Block> removableBlocks = new HashSet<>();
		for (EnemyShip enemyShip : this.enemyShipFormation) {
			if (enemyShip != null && !enemyShip.isDestroyed()) {
				for (Block block : block) {
					if (checkCollision(enemyShip, block)) {
						removableBlocks.add(block);
					}
				}
			}
		}

		// remove crashed obstacle
		block.removeAll(removableBlocks);
		this.bullets.removeAll(recyclable);
		BulletPool.recycle(recyclable);
	}

	/**
	 * Checks if two entities are colliding.
	 * 
	 * @param a
	 *            First entity, the bullet.
	 * @param b
	 *            Second entity, the ship.
	 * @return Result of the collision test.
	 */
	private boolean checkCollision(final Entity a, final Entity b) {
		// Calculate center point of the entities in both axis.
		int centerAX = a.getPositionX() + a.getWidth() / 2;
		int centerAY = a.getPositionY() + a.getHeight() / 2;
		int centerBX = b.getPositionX() + b.getWidth() / 2;
		int centerBY = b.getPositionY() + b.getHeight() / 2;
		// Calculate maximum distance without collision.
		int maxDistanceX = a.getWidth() / 2 + b.getWidth() / 2;
		int maxDistanceY = a.getHeight() / 2 + b.getHeight() / 2;
		// Calculates distance.
		int distanceX = Math.abs(centerAX - centerBX);
		int distanceY = Math.abs(centerAY - centerBY);

		return distanceX < maxDistanceX && distanceY < maxDistanceY;
	}

	/**
	 * Returns a GameState object representing the status of the game.
	 * 
	 * @return Current game state.
	 */
	public final GameState getGameState() {
		return new GameState(this.level, this.score, this.shipType, this.lives,
				this.bulletsShot, this.shipsDestroyed, this.elapsedTime, this.alertMessage, 0, this.maxCombo, this.lapTime, this.tempScore, this.hitBullets);
	}


	/**
	 * Start the action for two player mode
	 *
	 * @return Current game state.
	 */
	@Override
	public final GameState call() {
		run();
		return getGameState();
	}
	//Enemy bullet damage increases depending on stage level
	public void lvdamage(){
		for(int i=0; i<=level/3;i++){
			this.lives--;
		}
		if(this.lives < 0){
			this.lives = 0;
		}
	}
}
