package screen;

import java.awt.event.KeyEvent;

import engine.*;
import entity.Wallet;


/**
 * Implements the title screen.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public class TitleScreen extends Screen {

	/** Milliseconds between changes in user selection. */
	private static final int SELECTION_TIME = 200;

	/** Time between changes in user selection. */
	private Cooldown selectionCooldown;

	/** Singleton instance of SoundManager */
	private final SoundManager soundManager = SoundManager.getInstance();

	private final Wallet wallet;

	private final String username;
	/**
     * Constructor, establishes the properties of the screen.
     *
     * @param width       Screen width.
     * @param height      Screen height.
     * @param fps         Frames per second, frame rate at which the game is run.
     * @param wallet      Player's wallet
     * @param userManager
     */
	public TitleScreen(final int width, final int height, final int fps, final Wallet wallet, UserManager userManager) {
		super(width, height, fps);
		this.username = userManager.getUsername();

		// Defaults to play.
		if (!soundManager.isSoundPlaying(Sound.BGM_MAIN))
			soundManager.loopSound(Sound.BGM_MAIN);

		this.returnCode = 6;
		this.selectionCooldown = Core.getCooldown(SELECTION_TIME);
		this.selectionCooldown.reset();
		this.wallet = wallet;
	}

	/**
	 * Starts the action.
	 *
	 * @return Next screen code.
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
				&& this.inputDelay.checkFinished()) {
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
			if (inputManager.isKeyDown(KeyEvent.VK_SPACE)){
				this.isRunning = false;
				soundManager.playSound(Sound.MENU_CLICK);
			}
		}
	}

	/**
	 * Shifts the focus to the next menu item.
	 */
	private void nextMenuItem() { //11multi-6play-3shop-4ach-5settings-0exit
	/*
	  TODO: Refactor returnCode & Core Logic
	 */
		if (this.returnCode == 5)
			this.returnCode = 0;
		else if (this.returnCode == 0)
			this.returnCode = 11;
		else if(this.returnCode == 11)
			this.returnCode = 6;
		else if (this.returnCode == 6)
			this.returnCode = 3;
		else
			this.returnCode++;
	}

	/**
	 * Shifts the focus to the previous menu item.
	 */
	private void previousMenuItem() { //11multi-6play-3shop-4ach-5settings-0exit
	/*
	  TODO: Refactor returnCode & Core Logic
	 */
		if (this.returnCode == 0)
			this.returnCode = 5;
		else if (this.returnCode == 6)
			this.returnCode = 11;
		else if(this.returnCode == 11)
			this.returnCode = 0;
		else if (this.returnCode == 3)
			this.returnCode = 6;
		else
			this.returnCode--;
	}

	/**
	 * Draws the elements associated with the screen.
	 */
	private void draw() {
		drawManager.initDrawing(this);

		drawManager.drawTitle(this);
		drawManager.drawMenu(this, this.returnCode, wallet.getCoin());
		drawManager.drawCenteredText(this, "Welcome, "+ username, 150);
		drawManager.completeDrawing(this);
	}
}