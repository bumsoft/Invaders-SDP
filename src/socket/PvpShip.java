package socket;

import engine.DrawManager;
import entity.Entity;

import java.awt.*;

public class PvpShip extends Entity {

    public PvpShip(int positionX, int positionY, DrawManager.SpriteType spriteType, Color color)
    {
        super(positionX, positionY, 26, 16, color);
        this.spriteType = spriteType;
    }
}
