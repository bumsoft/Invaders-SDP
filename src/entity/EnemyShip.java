package entity;

import java.awt.Color;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import engine.GameState;
import engine.Sound;
import engine.SoundManager;

/**
 * Implements a enemy ship, to be destroyed by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class EnemyShip extends Entity {
	/** Point value of a type A enemy. */
	private static final int A_TYPE_POINTS = 10;
	/** Point value of a type B enemy. */
	private static final int B_TYPE_POINTS = 20;
	/** Point value of a type C enemy. */
	private static final int C_TYPE_POINTS = 30;
	/** Point value of a type D enemy. */
	private static final int D_TYPE_POINTS = 40;
	/** Point value of a type E enemy. */
	private static final int E_TYPE_POINTS = 50;
	/** Point value of a type F enemy*/
	private static final int F_TYPE_POINTS = 60;
	/** Point value of a bonus enemy. */
	private static final int BONUS_TYPE_POINTS = 100;

	/** Cooldown between sprite changes. */
	private Cooldown animationCooldown;
	/** Checks if the ship has been hit by a bullet. */
	private boolean isDestroyed;
	/** Values of the ship, in points, when destroyed. */
	private int pointValue;

	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private int health;
	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param spriteType
	 *            Sprite type, image corresponding to the ship.
	 */
	public EnemyShip(final int positionX, final int positionY,
			final SpriteType spriteType, final GameState gameState) {
		super(positionX, positionY, 12 * 2, 8 * 2, getDefaultColor(spriteType));

		this.spriteType = spriteType;
		this.animationCooldown = Core.getCooldown(500);
		this.isDestroyed = false;
        //Determine enemy health based on game level
		this.health = 0;
		for(int i =1; i<=gameState.getLevel()/3;i++){
			this.health++;
		}

		switch (this.spriteType) {
		case EnemyShipA1:
		case EnemyShipA2:
			this.pointValue = (int) (A_TYPE_POINTS+(gameState.getLevel()*0.1)+Core.getLevelSetting());
			break;
		case EnemyShipB1:
		case EnemyShipB2:
			this.pointValue = (int) (B_TYPE_POINTS+(gameState.getLevel()*0.1)+Core.getLevelSetting());
			break;
		case EnemyShipC1:
		case EnemyShipC2:
			this.pointValue = (int) (C_TYPE_POINTS+(gameState.getLevel()*0.1)+Core.getLevelSetting());
			break;
		case EnemyShipD1:
		case EnemyShipD2:
			this.pointValue = D_TYPE_POINTS;
			break;
		case EnemyShipE1:
		case EnemyShipE2:
			this.pointValue = E_TYPE_POINTS;
			break;
		case EnemyShipF1:
			this.pointValue = F_TYPE_POINTS;
			break;
		default:
			this.pointValue = 0;
			break;
		}
	}

	/**
	 * Giving color for each enemy ship
	 */
		public static Color getDefaultColor(SpriteType spriteType) {
			switch (spriteType) {
				case EnemyShipA1:
				case EnemyShipA2:
					return Color.RED; // Цвет для типа A
				case EnemyShipB1:
				case EnemyShipB2:
					return Color.GREEN; // Цвет для типа B
				case EnemyShipC1:
				case EnemyShipC2:
					return Color.BLUE; // Цвет для типа C
				case EnemyShipD1:
				case EnemyShipD2:
					return Color.YELLOW; // Цвет для типа D
				case EnemyShipE1:
				case EnemyShipE2:
					return Color.ORANGE; // Цвет для типа E
				default:
					return Color.WHITE; // Цвет по умолчанию
			}
		}

	/**
	 * Constructor, establishes the ship's properties for a special ship, with
	 * known starting properties.
	 */
	public EnemyShip() {
		super(-32, 60, 16 * 2, 7 * 2, Color.RED);

		this.spriteType = SpriteType.EnemyShipSpecial;
		this.isDestroyed = false;
		this.pointValue = BONUS_TYPE_POINTS;
	}

	/**
	 * Getter for the score bonus if this ship is destroyed.
	 * 
	 * @return Value of the ship.
	 */
	public final int getPointValue() {
		return this.pointValue;
	}

	/**
	 * Moves the ship the specified distance.
	 * 
	 * @param distanceX
	 *            Distance to move in the X axis.
	 * @param distanceY
	 *            Distance to move in the Y axis.
	 */
	public final void move(final int distanceX, final int distanceY) {
		this.positionX += distanceX;
		this.positionY += distanceY;
	}

	/**
	 * Updates attributes, mainly used for animation purposes.
	 */
	public final void update() {
		if (this.animationCooldown.checkFinished()) {
			this.animationCooldown.reset();

			switch (this.spriteType) {
			case EnemyShipA1:
				this.spriteType = SpriteType.EnemyShipA2;
				break;
			case EnemyShipA2:
				this.spriteType = SpriteType.EnemyShipA1;
				break;
			case EnemyShipB1:
				this.spriteType = SpriteType.EnemyShipB2;
				break;
			case EnemyShipB2:
				this.spriteType = SpriteType.EnemyShipB1;
				break;
			case EnemyShipC1:
				this.spriteType = SpriteType.EnemyShipC2;
				break;
			case EnemyShipC2:
				this.spriteType = SpriteType.EnemyShipC1;
				break;
			case EnemyShipD1:
				this.spriteType = SpriteType.EnemyShipD2;
				break;
			case EnemyShipD2:
				this.spriteType = SpriteType.EnemyShipD1;
				break;
			case EnemyShipE1:
				this.spriteType = SpriteType.EnemyShipE2;
				break;
			case EnemyShipE2:
				this.spriteType = SpriteType.EnemyShipE1;
				break;
			default:
				break;
			}
		}
	}

	/**
	 * Destroys the ship, causing an explosion.
	 *
	 * @param balance 1p -1.0, 2p 1.0, both 0.0
	 */
	public final void destroy(final float balance) {
		this.isDestroyed = true;
		this.spriteType = SpriteType.Explosion;
        soundManager.playSound(Sound.ALIEN_HIT, balance);
	}

    public final void HealthManageDestroy(final float balance) { //Determine whether to destroy the enemy ship based on its health
        if(this.health <= 0){
            this.isDestroyed = true;
            this.spriteType = SpriteType.Explosion;
        }else{
            this.health--;
        }
        soundManager.playSound(Sound.ALIEN_HIT, balance);
    }

	public int getHealth(){return this.health; }  //Receive enemy ship health

	public void setHealth(int health) {
		this.health = health;
	}

	/**
	 * Checks if the ship has been destroyed.
	 * 
	 * @return True if the ship has been destroyed.
	 */
	public final boolean isDestroyed() {
		return this.isDestroyed;
	}
}
