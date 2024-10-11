package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;
import engine.Sound;
import engine.SoundManager;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public abstract class Ship extends Entity {

	/** Time between shots. */
	private static int SHOOTING_INTERVAL = 750;
	/** Speed of the bullets shot by the ship. */
	private static int BULLET_SPEED = -6;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;

    /** Play the sound every 0.5 second */
	private static final int SOUND_COOLDOWN_INTERVAL = 500;
    /** Cooldown for playing sound */
	private Cooldown soundCooldown;

	/** Multipliers for the ship's properties. */
	protected final ShipMultipliers multipliers;
	/** Name of the ship. */
	public final String name;
	/** Type of sprite to be drawn. */
	private final SpriteType baseSprite;

	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;
	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();


	private long lastShootTime;
	private boolean threadWeb = false;

	public void setThreadWeb(boolean threadWeb) {
		this.threadWeb = threadWeb;
	}

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 * @param name
	 * 		  	  Name of the ship.
	 * @param multipliers
	 * 		      Multipliers for the ship's properties.
	 * 		      @see ShipMultipliers
	 * @param spriteType
	 * 		      Type of sprite to be drawn.
	 * 		      @see SpriteType
	 */
	protected Ship(final int positionX, final int positionY,
				   final String name, final ShipMultipliers multipliers,
				   final SpriteType spriteType) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.name = name;
		this.multipliers = multipliers;
		this.baseSprite = spriteType;
		this.spriteType = spriteType;
		this.shootingCooldown = Core.getCooldown(this.getShootingInterval());
		this.destructionCooldown = Core.getCooldown(1000);
		this.lastShootTime = 0;
		this.soundCooldown = Core.getCooldown(SOUND_COOLDOWN_INTERVAL);
	}

	/**
	 * Types of ships available.
	 */
	public enum ShipType {
		StarDefender,
		VoidReaper,
		GalacticGuardian,
		CosmicCruiser,
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		if(threadWeb){
			this.positionX += this.getSpeed() / 2;
		}
		else{
			this.positionX += this.getSpeed();
		}
    if (soundCooldown.checkFinished()) {
		  soundManager.playSound(Sound.PLAYER_MOVE);
		  soundCooldown.reset();
    }
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		if(threadWeb){
			this.positionX -= this.getSpeed() / 2;
		}
		else{
			this.positionX -= this.getSpeed();
		}
    if (soundCooldown.checkFinished()) {
			soundManager.playSound(Sound.PLAYER_MOVE);
			soundCooldown.reset();
	  }
	}

	/**
	 * Shoots a bullet upwards.
	 * 
	 * @param bullets
	 *            List of bullets on screen, to add the new bullet.
	 * @return Checks if the bullet was shot correctly.
	 */
	public final boolean shoot(final Set<Bullet> bullets) {
		if (this.shootingCooldown.checkFinished()) {
			this.shootingCooldown.reset();
			bullets.add(BulletPool.getBullet(positionX + this.width / 2,
					positionY,  this.getBulletSpeed()));
			soundManager.playSound(Sound.PLAYER_LASER);
      this.lastShootTime = System.currentTimeMillis();
			return true;
		}
		return false;
	}

	/**
	 * Updates status of the ship.
	 */
	public final void update() {
		if (!this.destructionCooldown.checkFinished())
			this.spriteType = SpriteType.ShipDestroyed;
		else
			this.spriteType = this.baseSprite;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
		soundManager.playSound(Sound.PLAYER_HIT);
	}

	/**
	 * Checks if the ship is destroyed.
	 * 
	 * @return True if the ship is currently destroyed.
	 */
	public final boolean isDestroyed() {
		return !this.destructionCooldown.checkFinished();
	}

	/**
	 * Getter for the ship's speed.
	 * 
	 * @return Speed of the ship.
	 */
	public final int getSpeed() {
		return Math.round(SPEED * this.multipliers.speed());
	}

	/**
	 * Getter for the ship's bullet speed.
	 * @return Speed of the bullets.
	 */
	public final int getBulletSpeed() {
		return Math.round(BULLET_SPEED * this.multipliers.bulletSpeed());
	}

	/**
	 * Getter for the ship's shooting interval.
	 * @return Time between shots.
	 */
	public final int getShootingInterval() {
		return Math.round(SHOOTING_INTERVAL * this.multipliers.shootingInterval());
	}

	public long getRemainingReloadTime(){
		long currentTime = System.currentTimeMillis();
		long elapsedTime = currentTime - this.lastShootTime;
		long remainingTime = SHOOTING_INTERVAL - elapsedTime;
		return remainingTime > 0 ? remainingTime : 0;
	}


	public void applyItem(Wallet wallet){
		int bulletLv = wallet.getBullet_lv();
		switch (bulletLv){
			case 1:
				BULLET_SPEED = -6;
				break;
			case 2:
				BULLET_SPEED = -7;
				break;
			case 3:
				BULLET_SPEED = -9;
				break;
			case 4:
				BULLET_SPEED = -10;
				break;
			default:
				BULLET_SPEED = -6;
		}

		int intervalLv = wallet.getShot_lv();
		switch (intervalLv){
			case 1: //생성자에서 이미 초기화함
				break;
			case 2:
				SHOOTING_INTERVAL = 675;
				shootingCooldown = Core.getCooldown(this.getShootingInterval());
				break;
			case 3:
				SHOOTING_INTERVAL = 607;
				shootingCooldown = Core.getCooldown(this.getShootingInterval());
				break;
			case 4:
				SHOOTING_INTERVAL = 546;
				shootingCooldown = Core.getCooldown(this.getShootingInterval());
				break;
			default:
				SHOOTING_INTERVAL = 750;
				shootingCooldown = Core.getCooldown(this.getShootingInterval());
		}
	}
}
