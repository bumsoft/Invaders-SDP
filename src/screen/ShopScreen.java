package screen;

import java.awt.event.KeyEvent;

import engine.Cooldown;
import engine.Core;
import engine.Sound;
import engine.SoundManager;
import entity.Wallet;

public class ShopScreen extends Screen {

    /** Milliseconds between changes in user selection. */
    private static final int SELECTION_TIME = 200;
    private static final int ALERT_TIME = 1500;

    /** Singleton instance of SoundManager */
    private final SoundManager soundManager = SoundManager.getInstance();

    /** Time between changes in user selection. */
    private Cooldown selectionCooldown;

    /** Time until not enough coin alert disappear */
    private Cooldown money_alertCooldown;

    /** Time until max_lv alert disappear */
    private Cooldown max_alertCooldown;

    /** Player's wallet */
    private Wallet wallet;

    /** 1-bullet speed 2-shot frequency 3-additional lives 4-gain coin upgrade */
    private int selected_item;

    /** price per upgrade level */
    private int lv1cost = 2000;
    private int lv2cost = 4000;
    private int lv3cost = 8000;

    /**
     * Constructor, establishes the properties of the screen.
     *
     * @param width
     *            Screen width.
     * @param height
     *            Screen height.
     * @param fps
     *            Frames per second, frame rate at which the game is run.
     * @param wallet
     *            Player's wallet
     */
    public ShopScreen(final int width, final int height, final int fps, final Wallet wallet) {
        super(width, height, fps);

        this.returnCode = 1;
        this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
        this.selectionCooldown.reset();
        this.money_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.max_alertCooldown = Core.getCooldown(ALERT_TIME);
        this.wallet = wallet;
        selected_item = 1;

        soundManager.stopSound(Sound.BGM_MAIN);
        soundManager.loopSound(Sound.BGM_SHOP);
    }

    /**
     * Starts the action.
     *
     * @return Next screen code(MainMenu) 1.
     */
    public final int run() {
        super.run();

        return this.returnCode;
    }

    /**
     * Updates the elements on screen and checks for events.
     */
    protected final void update() {
        super.update();

        draw();
        if (this.selectionCooldown.checkFinished()
                && this.inputDelay.checkFinished()
                && this.money_alertCooldown.checkFinished()
                && this.max_alertCooldown.checkFinished()) {

            if (inputManager.isKeyDown(KeyEvent.VK_UP)
                    || inputManager.isKeyDown(KeyEvent.VK_W)) {
                previousMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_DOWN)
                    || inputManager.isKeyDown(KeyEvent.VK_S)) {
                nextMenuItem();
                this.selectionCooldown.reset();
                soundManager.playSound(Sound.MENU_MOVE);
            }
            if (inputManager.isKeyDown(KeyEvent.VK_SPACE))
            {
                switch (selected_item) {
                    case 1:
                        if (upgrade(wallet.getBullet_lv())) {
                            wallet.setBullet_lv(wallet.getBullet_lv() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 2:
                        if (upgrade(wallet.getShot_lv())) {
                            wallet.setShot_lv(wallet.getShot_lv() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 3:
                        if (upgrade(wallet.getLives_lv())) {
                            wallet.setLives_lv(wallet.getLives_lv() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    case 4:
                        if (upgrade(wallet.getCoin_lv())) {
                            wallet.setCoin_lv(wallet.getCoin_lv() + 1);
                            this.selectionCooldown.reset();
                        }
                        break;
                    default:
                        break;
                }
            }

            if (inputManager.isKeyDown(KeyEvent.VK_ESCAPE)) {
                this.isRunning = false;
                soundManager.playSound(Sound.MENU_BACK);
                soundManager.stopSound(Sound.BGM_SHOP);
            }
        }
    }

    /**
     * Shifts the focus to the next shop item.
     */
    private void nextMenuItem() {
        if (this.selected_item == 4)
            this.selected_item = 1;
        else
            this.selected_item++;
    }

    /**
     * Shifts the focus to the previous shop item.
     */
    private void previousMenuItem() {
        if (this.selected_item == 1)
            this.selected_item = 4;
        else
            this.selected_item--;
    }

    private void draw() {
        drawManager.initDrawing(this);


        drawManager.drawShop(this,selected_item,wallet,money_alertCooldown,max_alertCooldown);

        drawManager.completeDrawing(this);
    }

    public boolean upgrade(int level)
    {
        if(level == 1)
        {
            if(wallet.withdraw(lv1cost))
            {
                soundManager.playSound(Sound.COIN_USE);
                return true;
            }
            else
            {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                money_alertCooldown.reset();
                return false;
            }
        }
        else if(level == 2)
        {
            if(wallet.withdraw(lv2cost))
            {
                soundManager.playSound(Sound.COIN_USE);
                return true;
            }
            else
            {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                money_alertCooldown.reset();
                return false;
            }
        }
        else if(level == 3)
        {
            if(wallet.withdraw(lv3cost))
            {
                soundManager.playSound(Sound.COIN_USE);
                return true;
            }
            else
            {
                soundManager.playSound(Sound.COIN_INSUFFICIENT);
                money_alertCooldown.reset();
                return false;
            }
        }
        else if(level==4)
        {
            soundManager.playSound(Sound.COIN_INSUFFICIENT);
            max_alertCooldown.reset();
            return false;
        }
        return false;
    }
}
