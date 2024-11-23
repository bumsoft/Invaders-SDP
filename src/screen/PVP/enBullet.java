package screen.PVP;

import engine.DrawManager.SpriteType;
import entity.Entity;

import java.awt.*;

/**
 * Implements a bullet that moves vertically up or down.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class enBullet extends Entity {

	public enBullet(final int positionX, final int positionY) {
		super(positionX, positionY, 3 * 2, 5 * 2, Color.RED);

		setSprite();
	}

	/**
	 * Sets correct sprite for the bullet, based on speed.
	 */
	public final void setSprite() {
		this.spriteType = SpriteType.PvpBullet;
	}
}
