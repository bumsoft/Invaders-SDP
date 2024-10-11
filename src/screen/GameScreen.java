package screen;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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
public class GameScreen extends Screen {

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
	/** Current ship type. */
	private Ship.ShipType shipType;
	/** Player lives left. */
	private int lives;
	/** Total bullets shot by the player. */
	private int bulletsShot;
	/** Total ships destroyed by the player. */
	private int shipsDestroyed;
	/** Total ships destroyed consecutive by the player. */
	private int combo = 0;
	/** Moment the game starts. */
	private long gameStartTime;
	/** Checks if the level is finished. */
	private boolean levelFinished;
	/** Checks if a bonus life is received. */
	private boolean bonusLife;
	/** list of highScores for find recode. */
	private List<Score>highScores;
	/** Elapsed time while playing this game. */
	private int elapsedTime;
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
	private List<Blocker> blockers;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();



	private int MAX_BLOCKERS = 0;

	private GameState gameState;
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
		this.blockers = new ArrayList<>();
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
		this.bullets = new HashSet<Bullet>();

		// Special input delay / countdown.
		this.gameStartTime = System.currentTimeMillis();
		this.inputDelay = Core.getCooldown(INPUT_DELAY);
		this.inputDelay.reset();
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

			/*Elapsed Time Update*/
			long currentTime = System.currentTimeMillis();
			if (this.prevTime != null)
				this.elapsedTime += (int) (currentTime - this.prevTime);
			this.prevTime = (int) currentTime;

			if (!this.ship.isDestroyed()) {
				boolean moveRight = inputManager.isKeyDown(KeyEvent.VK_RIGHT)
						|| inputManager.isKeyDown(KeyEvent.VK_D);
				boolean moveLeft = inputManager.isKeyDown(KeyEvent.VK_LEFT)
						|| inputManager.isKeyDown(KeyEvent.VK_A);

				boolean isRightBorder = this.ship.getPositionX()
						+ this.ship.getWidth() + this.ship.getSpeed() > this.width - 1;
				boolean isLeftBorder = this.ship.getPositionX()
						- this.ship.getSpeed() < 1;

				if (moveRight && !isRightBorder) {
					this.ship.moveRight();
				}
				if (moveLeft && !isLeftBorder) {
					this.ship.moveLeft();
				}
				if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
					if (this.ship.shoot(this.bullets))
						this.bulletsShot++;
				boolean conti;



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
				soundManager.playSound(Sound.UFO_APPEAR);
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
			this.enemyShipFormation.update();
			this.enemyShipFormation.shoot(this.bullets, this.level);
			 if (level >= 3) {//Events where vision obstructions appear start from level 3 onwards.
				handleBlockerAppearance();
			}
		}

		manageCollisions();
		cleanBullets();
		draw();

		if ((this.enemyShipFormation.isEmpty() || this.lives <= 0)
				&& !this.levelFinished) {
			this.levelFinished = true;
			soundManager.stopSound(soundManager.getCurrentBGM());
			if (this.lives == 0)
				soundManager.playSound(Sound.GAME_END);
			this.screenFinishedCooldown.reset();
		}

		if (this.levelFinished && this.screenFinishedCooldown.checkFinished())
			this.isRunning = false;

	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);
		drawManager.drawGameTitle(this);


		drawManager.drawLaunchTrajectory( this,this.ship.getPositionX());

		drawManager.drawEntity(this.ship, this.ship.getPositionX(), this.ship.getPositionY());

		drawManager.drawEntity(this.ship, this.ship.getPositionX(),
				this.ship.getPositionY());
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

		for (Bullet bullet : this.bullets)
			drawManager.drawEntity(bullet, bullet.getPositionX(),
					bullet.getPositionY());


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


		for (Bullet bullet : this.bullets)
			if (bullet.getSpeed() > 0) {
				if (checkCollision(bullet, this.ship) && !this.levelFinished) {
					recyclable.add(bullet);
					if (!this.ship.isDestroyed()) {
						this.ship.destroy();
						lvdamage();
						this.logger.info("Hit on player ship, " + this.lives
								+ " lives remaining.");
						}
					}


			} else {
				for (EnemyShip enemyShip : this.enemyShipFormation)
					if (!enemyShip.isDestroyed()
							&& checkCollision(bullet, enemyShip)) {
						if (combo >= 5)
							this.score += enemyShip.getPointValue() * (combo / 5 + 1);
						else
							this.score += enemyShip.getPointValue();
						this.shipsDestroyed++;
						this.combo++;
						this.enemyShipFormation.destroy(enemyShip);
						timer.cancel();
						isExecuted = false;
						recyclable.add(bullet);
					}

				if (this.enemyShipSpecial != null
						&& !this.enemyShipSpecial.isDestroyed()
						&& checkCollision(bullet, this.enemyShipSpecial)) {
					if (combo >= 5)
				    this.score += enemyShipSpecial.getPointValue() * (combo / 5 + 1);
					else
						this.score += enemyShipSpecial.getPointValue();
					this.shipsDestroyed++;
					this.combo++;
					this.enemyShipSpecial.destroy();
					this.enemyShipSpecialExplosionCooldown.reset();
					timer.cancel();
					isExecuted = false;

					recyclable.add(bullet);

				}
					//check the collision between the obstacle and the bullet
					for (Block block : this.block) {
						if (checkCollision(bullet, block)) {
							recyclable.add(bullet);
							break;
						}
					}
				}
			//check the collision between the obstacle and the enemyship
			Set<Block> removableBlocks = new HashSet<>();
			for (EnemyShip enemyShip : this.enemyShipFormation) {
				if (!enemyShip.isDestroyed()) {
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
				this.bulletsShot, this.shipsDestroyed, this.elapsedTime, this.alertMessage, 0);
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