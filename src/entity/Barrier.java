package entity;
import java.awt.Color;

import engine.DrawManager.SpriteType;
import engine.Sound;
import engine.SoundManager;

public class Barrier extends Entity{

    private int health;

    /** Initialize singleton instance of SoundManager and return that */
    private final SoundManager soundManager = SoundManager.getInstance();

    public Barrier(final int positionX, final int positionY) {
        super(positionX, positionY, 39 * 2, 11 * 2, Color.GREEN);
        this.spriteType = SpriteType.Barrier;
        this.health = 1;
    }

    public void reduceHealth() {
        this.health--;
        soundManager.playSound(Sound.ITEM_BARRIER_OFF);
    }

    public boolean isDestroyed() {
        return this.health <= 0;
    }

    public int getHealth() {
        return this.health;
    }


}
