package socket;

import engine.DrawManager;
import entity.Entity;

import java.awt.*;

public class PvpShip extends Entity {

    public PvpShip(int positionX, int positionY, boolean isRoomOwner)
    {
        super(positionX, positionY, 26, 16, Color.GREEN);
        this.spriteType = DrawManager.SpriteType.PvpEnemy;
        this.spriteType = isRoomOwner ? DrawManager.SpriteType.Ship : DrawManager.SpriteType.PvpEnemy;
    }
}
