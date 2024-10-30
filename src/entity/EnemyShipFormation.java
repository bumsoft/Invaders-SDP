package entity;

import java.util.*;
import java.util.logging.Logger;

import engine.*;
import engine.DrawManager.SpriteType;
import screen.Screen;

/**
 * Groups enemy ships into a formation that moves together.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShipFormation implements Iterable<EnemyShip> {

	/** Initial position in the x-axis. */
	private static final int INIT_POS_X = 20;
	/** Initial position in the y-axis. */
	private static final int INIT_POS_Y = 100;
	/** Distance between ships. */
	private static final int SEPARATION_DISTANCE = 40;
	/** Proportion of E-type ships. */
	private static final double PROPORTION_E = 0.1;
	/** Proportion of D-type ships. */
	private static final double PROPORTION_D = 0.1;
	/** Proportion of C-type ships. */
	private static final double PROPORTION_C = 0.1;
	/** Proportion of B-type ships. */
	private static final double PROPORTION_B = 0.2;
	/** Lateral speed of the formation. */
	private static final int X_SPEED = 8;
	/** Downwards speed of the formation. */
	private static final int Y_SPEED = 4;
	/** Speed of the bullets shot by the members. */
	private static final int BULLET_SPEED = 4;
	/** Proportion of differences between shooting times. */
	private static final double SHOOTING_VARIANCE = .2;
	/** Margin on the sides of the screen. */
	private static final int SIDE_MARGIN = 20;
	/** Margin on the bottom of the screen. */
	private static final int BOTTOM_MARGIN = 80;
	/** Distance to go down each pass. */
	private static final int DESCENT_DISTANCE = 20;
	/** Minimum speed allowed. */
	private static final int MINIMUM_SPEED = 10;

	/** DrawManager instance. */
	private DrawManager drawManager;
	/** Application logger. */
	private Logger logger;
	/** Screen to draw ships on. */
	private Screen screen;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	/** List of enemy ships forming the formation. */
	private List<List<EnemyShip>> enemyShips;
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Number of ships in the formation - horizontally. */
	private int nShipsWide;
	/** Number of ships in the formation - vertically. */
	private int nShipsHigh;
	/** Time between shots. */
	private int shootingInterval;
	/** Variance in the time between shots. */
	private int shootingVariance;
	/** Initial ship speed. */
	private int baseSpeed;
	/** Speed of the ships. */
	private int movementSpeed;
	/** Current direction the formation is moving on. */
	private Direction currentDirection;
	/** Direction the formation was moving previously. */
	private Direction previousDirection;
	/** Interval between movements, in frames. */
	private int movementInterval;
	/** Total width of the formation. */
	private int width;
	/** Total height of the formation. */
	private int height;
	/** Position in the x-axis of the upper left corner of the formation. */
	private int positionX;
	/** Position in the y-axis of the upper left corner of the formation. */
	private int positionY;
	/** Width of one ship. */
	private int shipWidth;
	/** Height of one ship. */
	private int shipHeight;
	/** List of ships that are able to shoot. */
	private List<EnemyShip> shooters;
	/** Number of not destroyed ships. */
	private int shipCount;

	private int point = 0;

	private int distroyedship = 0;

	private GameState gameState;

	/** Directions the formation can move. */
	private enum Direction {
		/** Movement to the right side of the screen. */
		RIGHT,
		/** Movement to the left side of the screen. */
		LEFT,
		/** Movement to the bottom of the screen. */
		DOWN
	};

	/**
	 * Constructor, sets the initial conditions.
	 * 
	 * @param gameSettings
	 *            Current game settings.
	 */
	public EnemyShipFormation(final GameSettings gameSettings, final GameState gameState) {
		this.drawManager = Core.getDrawManager();
		this.logger = Core.getLogger();
		this.enemyShips = new ArrayList<List<EnemyShip>>();
		this.currentDirection = Direction.RIGHT;
		this.movementInterval = 0;
		this.nShipsWide = gameSettings.getFormationWidth();
		this.nShipsHigh = gameSettings.getFormationHeight();
		this.shootingInterval = gameSettings.getShootingFrecuency();
		this.shootingVariance = (int) (gameSettings.getShootingFrecuency()
				* SHOOTING_VARIANCE);
		this.baseSpeed = gameSettings.getBaseSpeed();
		this.movementSpeed = this.baseSpeed;
		this.positionX = INIT_POS_X;
		this.positionY = INIT_POS_Y;
		this.shooters = new ArrayList<EnemyShip>();
		this.gameState = gameState;
		SpriteType spriteType;

		this.logger.info("Initializing " + nShipsWide + "x" + nShipsHigh
				+ " ship formation in (" + positionX + "," + positionY + ")");

		// Each sub-list is a column on the formation.
		for (int i = 0; i < this.nShipsWide; i++)
			this.enemyShips.add(new ArrayList<EnemyShip>());

		for (List<EnemyShip> column : this.enemyShips) {
			for (int i = 0; i < this.nShipsHigh; i++) {
				if (i / (float) this.nShipsHigh < PROPORTION_E)
					spriteType = SpriteType.EnemyShipE1;
				else if (i / (float) this.nShipsHigh <  PROPORTION_E + PROPORTION_D)
					spriteType = SpriteType.EnemyShipD1;
				else if (i / (float) this.nShipsHigh <  PROPORTION_E + PROPORTION_D + PROPORTION_C)
					spriteType = SpriteType.EnemyShipC1;
				else if (i / (float) this.nShipsHigh <  PROPORTION_E + PROPORTION_D + PROPORTION_C + PROPORTION_B)
					spriteType = SpriteType.EnemyShipB1;
				else
					spriteType = SpriteType.EnemyShipA1;

				column.add(new EnemyShip((SEPARATION_DISTANCE 
						* this.enemyShips.indexOf(column))
								+ positionX, (SEPARATION_DISTANCE * i)
								+ positionY, spriteType, gameState));
				this.shipCount++;
			}
		}

		this.shipWidth = this.enemyShips.get(0).get(0).getWidth();
		this.shipHeight = this.enemyShips.get(0).get(0).getHeight();

		this.width = (this.nShipsWide - 1) * SEPARATION_DISTANCE
				+ this.shipWidth;
		this.height = (this.nShipsHigh - 1) * SEPARATION_DISTANCE
				+ this.shipHeight;

		for (List<EnemyShip> column : this.enemyShips)
			this.shooters.add(column.get(column.size() - 1));
	}

	/**
	 * Associates the formation to a given screen.
	 * 
	 * @param newScreen
	 *            Screen to attach.
	 */
	public final void attach(final Screen newScreen) {
		screen = newScreen;
	}

	/**
	 * Draws every individual component of the formation.
	 */
	public final void draw() {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				if (enemyShip != null)
				    drawManager.drawEntity(enemyShip, enemyShip.getPositionX(), enemyShip.getPositionY());
	}

	/**
	 * Draws every individual component of the formation for two player mode.
	 */
	public final void draw(final int playerNumber) {
		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				if (enemyShip != null)
					drawManager.drawEntity(enemyShip, enemyShip.getPositionX(),
							enemyShip.getPositionY(), playerNumber);
	}

	/**
	 * Updates the position of the ships.
	 */
	public final void update() {
		if(this.shootingCooldown == null) {
			this.shootingCooldown = Core.getVariableCooldown(shootingInterval,
					shootingVariance);
			this.shootingCooldown.reset();
		}

		adjustFormationBounds();

		int movementX = 0;
		int movementY = 0;
		double remainingProportion = (double) this.shipCount
				/ (this.nShipsHigh * this.nShipsWide);
		this.movementSpeed = this.baseSpeed;
		this.movementSpeed += MINIMUM_SPEED;

		movementInterval++;
		if (movementInterval >= this.movementSpeed) {
			movementInterval = 0;

			boolean isAtBottom = positionY
					+ this.height > screen.getHeight() - BOTTOM_MARGIN;
			boolean isAtRightSide = positionX
					+ this.width >= screen.getWidth() - SIDE_MARGIN;
			boolean isAtLeftSide = positionX <= SIDE_MARGIN;
			boolean isAtHorizontalAltitude = positionY % DESCENT_DISTANCE == 0;

			if (currentDirection == Direction.DOWN) {
				if (isAtHorizontalAltitude)
					if (previousDirection == Direction.RIGHT) {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 1");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 2");
					}
			} else if (currentDirection == Direction.LEFT) {
				if (isAtLeftSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 3");
					} else {
						currentDirection = Direction.RIGHT;
						this.logger.info("Formation now moving right 4");
					}
			} else {
				if (isAtRightSide)
					if (!isAtBottom) {
						previousDirection = currentDirection;
						currentDirection = Direction.DOWN;
						this.logger.info("Formation now moving down 5");
					} else {
						currentDirection = Direction.LEFT;
						this.logger.info("Formation now moving left 6");
					}
			}

			if (currentDirection == Direction.RIGHT)
				movementX = X_SPEED;
			else if (currentDirection == Direction.LEFT)
				movementX = -X_SPEED;
			else
				movementY = Y_SPEED;

			positionX += movementX;
			positionY += movementY;

			// Cleans explosions.
			for (int i = 0; i < this.enemyShips.size(); i++)
				for (int j = 0; j < this.enemyShips.get(i).size(); j++)
					if (this.enemyShips.get(i).get(j) != null && this.enemyShips.get(i).get(j).isDestroyed()) {
						this.logger.info("Removed enemy " + j + " from column " + i);
						this.enemyShips.get(i).set(j, null);
					}

			for (List<EnemyShip> column : this.enemyShips)
				for (EnemyShip enemyShip : column)
					if (enemyShip != null) {
						enemyShip.move(movementX, movementY);
						enemyShip.update();
					}
		}
	}

	/**
	 * Adjusts the width and height of the formation.
	 */
	private void adjustFormationBounds() {
		int maxColumn = 0;
		int minPositionY = Integer.MAX_VALUE;

		for (int i = 0; i < this.enemyShips.size(); i++) {
			List<EnemyShip> column = this.enemyShips.get(i);

			// Check whether every ship is null
			boolean allNull = column.stream().allMatch(Objects::isNull);

			if (!allNull) {
				// Find non-null elements only
				EnemyShip firstNonNullShip = null;
				EnemyShip lastNonNullShip = null;

				// Find the first and last non-null elements in the column
				for (EnemyShip ship : column) {
					if (ship != null) {
						if (firstNonNullShip == null) {
							firstNonNullShip = ship;
						}
						lastNonNullShip = ship;
					}
				}

				// Calculate the height of this column
				int columnSize = lastNonNullShip.getPositionY() - this.positionY + this.shipHeight;
				maxColumn = Math.max(maxColumn, columnSize);
				minPositionY = Math.min(minPositionY, firstNonNullShip.getPositionY());
			}
		}

		int leftMostPoint = 0;
		int rightMostPoint = 0;

		for (List<EnemyShip> column : this.enemyShips) {
			// Skip empty or all-null columns
			if (!column.isEmpty()) {
				EnemyShip firstNonNullShip = null;

				// Find the first non-null ship in the column
				for (EnemyShip ship : column) {
					if (ship != null) {
						firstNonNullShip = ship;
						break; // We only need the first non-null element
					}
				}

				// Perform calculations only if a non-null ship is found
				if (firstNonNullShip != null) {
					if (leftMostPoint == 0) {
						leftMostPoint = firstNonNullShip.getPositionX();
					}
					rightMostPoint = firstNonNullShip.getPositionX();
				}
			}
		}

		this.width = rightMostPoint - leftMostPoint + this.shipWidth;
		this.height = maxColumn;

		this.positionX = leftMostPoint;
		this.positionY = minPositionY;
	}

	/**
	 * Shoots a bullet downwards.
	 * 
	 * @param bullets
	 *            Bullets set to add the bullet being shot.
	 */
	public final void shoot(final Set<Bullet> bullets, int level, float balance) {
		// Increasing the number of projectiles per level 3 (levels 1 to 3, 4 to 6, 2, 7 to 9, etc.)
		int numberOfShooters = Math.min((level / 3) + 1, this.shooters.size());
		int numberOfBullets = (level / 3) + 1;

		// Randomly select enemy to fire in proportion to the level
		List<EnemyShip> selectedShooters = new ArrayList<>();
		for (int i = 0; i < numberOfShooters; i++) {
			int index = (int) (Math.random() * this.shooters.size());
			selectedShooters.add(this.shooters.get(index));
		}

		// Fire when the cool down is over
		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();

			// Each selected enemy fires a bullet
			for (EnemyShip shooter : selectedShooters) {
				// One shot at the base
				bullets.add(BulletPool.getBullet(shooter.getPositionX()
						+ shooter.width / 2 + 10, shooter.getPositionY(), BULLET_SPEED));

				// Additional launches based on levels (more launches based on each level)
				for (int i = 1; i < numberOfBullets; i++) {
					bullets.add(BulletPool.getBullet(shooter.getPositionX()
							+ shooter.width / 2 + (10 * (i + 1)), shooter.getPositionY(), BULLET_SPEED));
				}
				soundManager.playSound(Sound.ALIEN_LASER, balance);
			}
		}
	}

	/**
	 * Destroys a ship.
	 * 
	 * @param destroyedShip
	 *            Ship to be destroyed.
	 * @param balance
	 *            1p -1.0, 2p 1.0, both 0.0
	 */
	public final void destroy(final EnemyShip destroyedShip, final float balance) {
		for (List<EnemyShip> column : this.enemyShips)
			for (int i = 0; i < column.size(); i++)
				if (column.get(i) != null && column.get(i).equals(destroyedShip)) {
					column.get(i).destroy(balance);
					this.logger.info("Destroyed ship in ("
							+ this.enemyShips.indexOf(column) + "," + i + ")");
				}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}

		this.shipCount--;
	}

	public final void HealthManageDestroy(final EnemyShip destroyedShip, final float balance) {
		for (List<EnemyShip> column : this.enemyShips)
			for (int i = 0; i < column.size(); i++)
				if (column.get(i) != null && column.get(i).equals(destroyedShip)) {
					//If health is 0, number of remaining enemy ships--, score awarded, number of destroyed ships++
					if(destroyedShip.getHealth() <= 0){
						this.shipCount--;
						this.logger.info("Destroyed ship in ("
								+ this.enemyShips.indexOf(column) + "," + i + ")");
						point = destroyedShip.getPointValue();
						distroyedship = 1;
						destroyedShip.setHealth(destroyedShip.getHealth() - 1);
					}else{
						point = 0;
						distroyedship = 0;
					}
					column.get(i).HealthManageDestroy(balance);
				}

		// Updates the list of ships that can shoot the player.
		if (this.shooters.contains(destroyedShip)) {
			int destroyedShipIndex = this.shooters.indexOf(destroyedShip);
			int destroyedShipColumnIndex = -1;

			for (List<EnemyShip> column : this.enemyShips)
				if (column.contains(destroyedShip)) {
					destroyedShipColumnIndex = this.enemyShips.indexOf(column);
					break;
				}

			EnemyShip nextShooter = getNextShooter(this.enemyShips
					.get(destroyedShipColumnIndex));

			if (nextShooter != null)
				this.shooters.set(destroyedShipIndex, nextShooter);
			else {
				this.shooters.remove(destroyedShipIndex);
				this.logger.info("Shooters list reduced to "
						+ this.shooters.size() + " members.");
			}
		}
	}

	/**
	 * Gets the ship on a given column that will be in charge of shooting.
	 * 
	 * @param column
	 *            Column to search.
	 * @return New shooter ship.
	 */
	public final EnemyShip getNextShooter(final List<EnemyShip> column) {
		Iterator<EnemyShip> iterator = column.iterator();
		EnemyShip nextShooter = null;
		while (iterator.hasNext()) {
			EnemyShip checkShip = iterator.next();
			if (checkShip != null && !checkShip.isDestroyed())
				nextShooter = checkShip;
		}

		return nextShooter;
	}

	/**
	 * Returns an iterator over the ships in the formation.
	 * 
	 * @return Iterator over the enemy ships.
	 */
	@Override
	public final Iterator<EnemyShip> iterator() {
		Set<EnemyShip> enemyShipsList = new HashSet<EnemyShip>();

		for (List<EnemyShip> column : this.enemyShips)
			for (EnemyShip enemyShip : column)
				enemyShipsList.add(enemyShip);

		return enemyShipsList.iterator();
	}

	/**
	 * Checks if there are any ships remaining.
	 * 
	 * @return True when all ships have been destroyed.
	 */
	public final boolean isEmpty() {
		return this.shipCount <= 0;
	}


	public int getPoint(){return point; }

	public int getDistroyedship(){return distroyedship; }

	public List<List<EnemyShip>> getEnemyShips() {return enemyShips; }
}
