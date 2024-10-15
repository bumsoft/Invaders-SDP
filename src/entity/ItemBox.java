package entity;

import engine.DrawManager.SpriteType;
import engine.Sound;
import engine.SoundManager;

import java.awt.*;

/**
 * Implement an item box that activates an item when destroyed.
 *
 * @author Seochan Moon
 * @author noturavrigk
 * @author specture258
 * @author javadocq
 * @author bamcasa
 * @author D0hunLee
 *
 */
public class ItemBox extends Entity {
    /** Check if it was dropped right now. */
    private boolean droppedRightNow;

    /** Initialize singleton instance of SoundManager and return that */
    private static final SoundManager soundManager = SoundManager.getInstance();

    /**
     * Constructor, establishes the entity's generic properties.
     *
     * @param positionX Initial position of the entity in the X axis.
     * @param positionY Initial position of the entity in the Y axis.
     */
    public ItemBox(int positionX, int positionY) {
        super(positionX, positionY, 7 * 2, 7 * 2, Color.YELLOW);
        this.spriteType = SpriteType.ItemBox;
        this.droppedRightNow = true;
        soundManager.playSound(Sound.ITEM_SPAWN);

        // Prevents being hit by bullets immediately after being created.
        new Thread(() -> {
            try {
                Thread.sleep(100);
                this.droppedRightNow = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Checks if it was dropped right now.
     *
     * @return True when it was dropped right now.
     */
    public boolean isDroppedRightNow() {
        return this.droppedRightNow;
    }
}
