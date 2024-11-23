package socket;

import engine.DrawManager;
import entity.Entity;

import java.awt.*;

public class PvpShip extends Entity {

    public PvpShip(int positionX, int positionY, DrawManager.SpriteType spriteType)
    {
        super(positionX, positionY, 26, 16, Color.GREEN);
        this.spriteType = spriteType;
    }
}
