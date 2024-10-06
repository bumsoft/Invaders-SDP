package entity;

import java.awt.Color;
import java.util.Random;
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
	// 게임 시작 시 함선3 종류 중 랜덤하게 하나가 선택되도록.
	// 단계별 업그레이드 수치를 얼마로 할지. - 몇 단계를 max로 할 지
	// (ship 엔티티의 SHOOTING_INTERVAL & BULLET_SPEED 계속 수정 및 플레이 해보면서 값 정하기)

	// 함선 종류는 여러개, 코인으로 함선의 총알 속도, 슈팅 인터벌 업그레이드
	// 함선 : A, B, C - 디자인만 다름
	// A: bullet speed; -6 / shooting interval; 750
	// bulletspeed  / shooting interval 따로따로 업그레이드 가능하게 만들 것
	// 업그레이드는 최대 3번 까지만 허용 - max lv.4까지
	// upgrade 비용: 2000c -> 4000C -> 8000C


	/** Time between shots. */
	// Shooting interval, bullet speed 값 보면서 변경
	//private static final int SHOOTING_INTERVAL = 750;
	// shooting_interval이 레벨에 따라서 변하므로 배열에 값을 넣어서 레벨 별로 지정할 수 있게 함
	private static final int[] SHOOTING_INTERVAL_LV = {750, 675, 607, 546};
	private int shootingInterval;

	/** Speed of the bullets shot by the ship. */
	//private static final int BULLET_SPEED = -6;
	// bullet_speed가 레벨에 따라서 변하므로 배열에 값을 넣어서 레벨 별로 지정할 수 있도록 함.
	private static final double[] BULLET_SPEED_LV = {-6, -7.2, -8.7, -10.4};
	private double bulletSpeed;

	// 업그레이드 단계 별로 필요한 코인 수 정의 - lv1->lv2; 2000C / lv2->lv3; 3000c / lv3->lv4; 4000c
	public static final int[] UPGRADE_COST = {2000, 4000, 8000};

	/** Movement of the ship for each unit of time. */
	private static final int SPEED = 2;
	
	/** Minimum time between shots. */
	private Cooldown shootingCooldown;
	/** Time spent inactive between hits. */
	private Cooldown destructionCooldown;

	// wallet 객체 필드 추가
	private Wallet wallet;
	/**
	 * Constructor, establishes the ship's properties.
	 *
	 * 
	 * @param positionX
	 *            Initial position of the ship in the X axis.
	 * @param positionY
	 *            Initial position of the ship in the Y axis.
	 */
	// Ship 생성자에서 wallet 객체를 받을 수 있도록 수정함
	public Ship(final int positionX, final int positionY, Wallet wallet) {
		super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);

		this.wallet = wallet;
		this.spriteType = SpriteType.Ship;
		this.shootingCooldown = Core.getCooldown(shootingInterval);
		this.destructionCooldown = Core.getCooldown(1000);
	}

	// upgrade_bullet_speed 메소드 (총알 속도 업그레이드 메소드)
	public void upgradeBulletSpeed(){
		int bullet_lv = wallet.getBullet_lv();
		// 만약, 범위 밖의 값이 설정된다면 기본 값으로 재설정
		if(bullet_lv < 1 || bullet_lv > 4){
			bullet_lv = 1;
		}
		// 최대 lv.4까지 업그레이드 가능
		if(bullet_lv == 4){
			System.out.println("Bullet speed is already at the MAX level!");
			return;
		}
		// 업그레이드 비용 확인
		int upgradeCost = UPGRADE_COST[bullet_lv-1];
		if(wallet.getCoin() >= upgradeCost){
			// 코인 차감
			wallet.withdraw(upgradeCost);
			wallet.setBullet_lv(bullet_lv+1);
			// 레벨 +1
			this.bulletSpeed = BULLET_SPEED_LV[bullet_lv];
			System.out.println("Bullet speed updated to: " + this.bulletSpeed);
		}
		else{
			System.out.println("Not enough coin to upgrade bullet speed!");
		}

	}

	// 총알 속도 getter
	public double getBulletSpeed(){
		return bulletSpeed;
	}

	// upgrade_shooting_interval 메소드 (총알 발사 빈도 업그레이드 메소드)
	public void upgradeShootingInterval(){
		int shot_lv = wallet.getShot_lv();
		// 만약, 범위 밖의 값이 설정된다면 기본 값으로 재설정
		if(shot_lv < 1 || shot_lv > 4){
			shot_lv = 1;
		}
		// 최대 lv.4까지 업그레이드 가능
		if(shot_lv == 4){
			System.out.println("Shot speed is already at the MAX level!");
			return;
		}
		// 업그레이드 비용 확인
		int upgradeCost = UPGRADE_COST[shot_lv-1];
		if (wallet.getCoin() >= upgradeCost){
			// 코인 차감
			wallet.withdraw(upgradeCost);
			// 레벨 +1
			wallet.setShot_lv(shot_lv+1);
			this.shootingInterval = SHOOTING_INTERVAL_LV[shot_lv];
			System.out.println("Shooting interval updated to: " + this.shootingInterval);
		}
		else{
			System.out.println("Not enough coin to upgrade shooting interval!");
		}

	}

	// 발사 빈도 getter
	public int getShootingInterval(){
		return shootingInterval;
	}

	// ship random select 메소드
	public static Ship randomSelectShip(int positionX, int positionY, Wallet wallet) {
		Random random = new Random();
		int shipType = random.nextInt(3); // 0-A / 1-B / 2-C 중 하나

		Ship selectedShip;

		switch(shipType){
			case 0:
				selectedShip = new Ship(positionX, positionY, wallet);
				selectedShip.setBulletSpeed(-6);
				selectedShip.setShootingInterval(750);
				selectedShip.spriteType = SpriteType.ShipA; // A의 스프라이트 설정
				System.out.println("Ship A selected");
				break;
			case 1:
				selectedShip = new Ship(positionX, positionY, wallet);
				selectedShip.setBulletSpeed(-6);
				selectedShip.setShootingInterval(750);
				selectedShip.spriteType = SpriteType.ShipB; // B의 스프라이트 설정
				System.out.println("Ship B selected");
				break;
			case 2:
				selectedShip = new Ship(positionX, positionY, wallet);
				selectedShip.setBulletSpeed(-6);
				selectedShip.setShootingInterval(750);
				selectedShip.spriteType = SpriteType.ShipC; // C의 스프라이트 설정
				System.out.println("Ship C selected");
				break;
		}
		return selectedShip;
	}

	private void setShootingInterval(int i) {
		shootingInterval = SHOOTING_INTERVAL_LV[i];
	}

	// bullet speed setter
	private void setBulletSpeed(int i) {
		bulletSpeed = BULLET_SPEED_LV[i];
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
					positionY, (int)bulletSpeed));
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
