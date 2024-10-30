package entity;

import engine.DrawManager.SpriteType;

import java.awt.*;

public class  Web extends Entity{
    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     */
    public Web(int positionX, int positionY) {
        super(positionX, positionY, 12 * 2, 8 * 2, Color.WHITE);
        this.spriteType = SpriteType.Web;
    }


}