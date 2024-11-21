package socket;

import engine.DrawManager;
import entity.Entity;

import java.awt.*;

public class PvpShip extends Entity {

    public PvpShip(int positionX, int positionY)
    {
        super(positionX, positionY, 13 * 2, 8 * 2, Color.GREEN);
        this.spriteType = DrawManager.SpriteType.Ship;
    }
}
