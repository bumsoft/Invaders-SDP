package screen;

import java.awt.event.KeyEvent;
import engine.Cooldown;
import engine.Core;
import engine.Sound;
import engine.SoundManager;
import entity.Ship;

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
    /** Milliseconds between changes in user selection. */
    private static final int COOLDOWN_TIME = 200;

    /** Menu item list */
    private String[] menuItems = { "Sound", "Ending Credit" , "Ship Selection"};
    /** Default selected menu item */
    private int selectedItem = 0;
    /** Default volume value */
    private int volumeLevel;
    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;
    /** Ship enumeration index*/
    private int currentShip;
    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

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
        this.volumeLevel = soundManager.getVolume()*10;
        this.currentShip = 0;
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
            soundManager.playSound(Sound.MENU_BACK);
            return;
        }

        if (this.selectionCooldown.checkFinished()) {

            if (selectedItem == 0) {
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                    volumeLevel = Math.max(0, volumeLevel - VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                    soundManager.volumeDown();
                    soundManager.playSound(Sound.MENU_MOVE);
                } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    volumeLevel = Math.min(100, volumeLevel + VOLUME_ADJUST_STEP);
                    this.selectionCooldown.reset();
                    soundManager.volumeUp();
                    soundManager.playSound(Sound.MENU_MOVE);
                }
            }

            if(selectedItem == 2){
                if (inputManager.isKeyDown(KeyEvent.VK_LEFT)) {
                        this.currentShip = Math.max(0 , currentShip - 1);
                    Core.BASE_SHIP = Ship.ShipType.values()[currentShip];
                    this.selectionCooldown.reset();
                } else if (inputManager.isKeyDown(KeyEvent.VK_RIGHT)) {
                    this.currentShip = Math.min(Ship.ShipType.values().length - 1 , currentShip + 1);
                    Core.BASE_SHIP = Ship.ShipType.values()[currentShip];
                    this.selectionCooldown.reset();
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_UP)) {
                selectedItem = (selectedItem - 1 + menuItems.length) % menuItems.length;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            } else if (inputManager.isKeyDown(KeyEvent.VK_DOWN)) {
                selectedItem = (selectedItem + 1) % menuItems.length;
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }

            if (inputManager.isKeyDown(KeyEvent.VK_SPACE) && selectedItem == 1) {
                this.returnCode = 7;
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_CLICK);
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
        boolean isShipChoiceSelected = (selectedItem == 2);

        drawManager.drawVolumeBar(this, this.getWidth() / 2 - VOLUME_BAR_WIDTH / 2, this.getHeight() / 3 + VOLUME_BAR_GAP, VOLUME_BAR_WIDTH, filledWidth, isVolumeSelected);

        drawManager.drawVolumePercentage(this, this.getWidth() / 2, this.getHeight() / 3 + VOLUME_BAR_GAP + VOLUME_PERCENTAGE_GAP, volumeLevel, isVolumeSelected);

        int NumberOfShips = Ship.ShipType.values().length;
        for (int j = 0; j < NumberOfShips; j++){
            drawManager.drawShipBoxes(this, this.getWidth() / 2 - 30*NumberOfShips, this.getHeight() - 150, isShipChoiceSelected, j, j==this.currentShip);
        }

        drawManager.completeDrawing(this);
    }
}
