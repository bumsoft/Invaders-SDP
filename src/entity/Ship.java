package entity;

import java.awt.Color;
import java.util.Set;

import engine.Cooldown;
import engine.Core;
import engine.DrawManager.SpriteType;

/**
 * Implements a ship, to be controlled by the player.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Ship extends Entity {
	// 게임 시작 시 플레이어가 함선을 선택하고 시작하는 방향으로 갈 듯. 
	// 함선별로 초기 값이 달라서 다른 팀에서 제공해주면 shooting interval, bullet speed 우리가 재설정
	// 단계별 업그레이드 수치를 얼마로 할지. - 몇 단계를 max로 할 지
	//(ship 엔티티의 SHOOTING_INTERVAL & BULLET_SPEED 계속 수정 및 플레이 해보면서 값 정하기)

	// 함선 종류는 여러개, 코인으로 함선의 총알 속도, 슈팅 인터벌 업그레이드
	// 함선 : A, B, C
	// A: bullet speed; -6 / shooting interval; 750
	// B: bullet speed; -4 / shooting interval; 700
	// C: bullet speed; -12 / shooting interval; 800
	// ex. 코인으로 한 번 압그레이드 시킬 때
	// bulletspeed * 1.2 / shooting interval * 0.9 - 따로따로 업그레이드

	/** Time between shots. */
	// Shooting interval, bullet speed 값 보면서 변경
	private static final int SHOOTING_INTERVAL = 750;
	/** Speed of the bullets shot by the ship. */
	private static final int BULLET_SPEED = -6;
	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;
	
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	/**
	 * Constructor, establishes the ship's properties.
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 */
	public Ship(final int positionX, final int positionY) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(SHOOTING_INTERVAL);
		this.destructionCooldown = Core.getCooldown(1000);
	}

	/**
	 * Moves the ship speed uni ts right, or until the right screen border is
	 * reached.
	 */
	public final void moveRight() {
		this.positionX += SPEED;
	}

	/**
	 * Moves the ship speed units left, or until the left screen border is
	 * reached.
	 */
	public final void moveLeft() {
		this.positionX -= SPEED;
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
					positionY, BULLET_SPEED));
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
			this.spriteType = SpriteType.Ship;
	}

	/**
	 * Switches the ship to its destroyed state.
	 */
	public final void destroy() {
		this.destructionCooldown.reset();
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
		return SPEED;
	}
}
