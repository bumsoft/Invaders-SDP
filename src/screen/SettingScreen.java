package screen;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;

public class SettingScreen extends Screen {

    /** Width of Volume bar */
    private static final int VOLUME_BAR_WIDTH = 200;
    /** Volume adjustment units */
    private static final int VOLUME_ADJUST_STEP = 10;
    /** Between menu items */
    private static final int MENU_ITEM_GAP = 120;
    /** Spacing between soundbar and text (closer to sound) */
    private static final int VOLUME_BAR_GAP = 20;
    /** Spacing between soundbar and volume numbers */
    private static final int VOLUME_PERCENTAGE_GAP = 40;
    /** Initial volume value */
    private static final int INITIAL_VOLUME = 50;
    /** Milliseconds between changes in user selection. */
    private static final int COOLDOWN_TIME = 200;

    /** Menu item list */
    private String[] menuItems = { "Sound", "Ending Credit" };
    /** Default selected menu item */
    private int selectedItem = 0;
    /** Default volume value */
    private int volumeLevel = INITIAL_VOLUME;
    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     */
    public SettingScreen(int width, int height, int fps) {
        super(width, height, fps);
        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(COOLDOWN_TIME);
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected void update() {
        super.update();

        if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
            this.isRunning = false;
            this.returnCode = 1;
            return;
        }

        if (this.selectionCooldown.checkFinished()) {

            if (selectedItem == 0) {
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                    volumeLevel = Math.max(0, volumeLevel - VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    volumeLevel = Math.min(100, volumeLevel + VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
                this.selectionCooldown.reset();
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                selectedItem = (selectedItem + 1) % menuItems.length;
                this.selectionCooldown.reset();
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && selectedItem == 1) {
                this.returnCode = 7;
                this.isRunning = false;
            }
        }

        draw();
    }

    /**
     * Starts the action.
     *
     * @return Next screen code.
     */
    public int run() {
        super.run();
        return this.returnCode;
    }

    /**
     * Draws the elements associated with the screen.
     */
    private void draw() {
        drawManager.initDrawing(this);

        drawManager.drawSettingsScreen(this);

        for (int i = 0; i < menuItems.length; i++) {
            boolean isSelected = (i == selectedItem);
            drawManager.drawCenteredRegularString(this, menuItems[i], this.getHeight() / 3 + i * MENU_ITEM_GAP, isSelected);
        }

        int filledWidth = (volumeLevel * VOLUME_BAR_WIDTH) / 100;
        boolean isVolumeSelected = (selectedItem == 0);

        drawManager.drawVolumeBar(this, this.getWidth() / 2 - VOLUME_BAR_WIDTH / 2, this.getHeight() / 3 + VOLUME_BAR_GAP, VOLUME_BAR_WIDTH, filledWidth, isVolumeSelected);

        drawManager.drawVolumePercentage(this, this.getWidth() / 2, this.getHeight() / 3 + VOLUME_BAR_GAP + VOLUME_PERCENTAGE_GAP, volumeLevel, isVolumeSelected);

        drawManager.completeDrawing(this);
    }
}
