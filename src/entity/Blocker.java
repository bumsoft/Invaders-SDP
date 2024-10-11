package entity;

import engine.DrawManager.SpriteType;
import java.util.Random;
import java.awt.*;

public class Blocker extends Entity {

    private Random random;
    /* angle */
    private double angle;
    /* Move left? */
    private boolean moveLeft;

    public Blocker(int positionX, int positionY, final SpriteType spriteType, boolean moveLeft) {
        //super(positionX, positionY, 182 * 2, 93 * 2, Color.GREEN);
        super(positionX, positionY, 82 * 2, 81 * 2, Color.GREEN);
        this.spriteType = spriteType;
        this.random = new Random();
        angle = 180 * random.nextDouble();
        this.moveLeft = moveLeft;
    }

    public final void move(final double distanceX, final double distanceY) {
        this.positionX += distanceX;
        this.positionY += distanceY;
    }

    public final void rotate(final double degree) { angle += degree; }

    public double getAngle() { return angle; }

    public boolean getMoveLeft() { return moveLeft; }
}
