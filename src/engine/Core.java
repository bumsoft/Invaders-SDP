package engine;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import entity.Ship;
import entity.Wallet;
import screen.*;
import screen.PVP.PvpGameScreen;
import screen.PVP.PvpLobbyScreen;
import screen.PVP.WaitingRoomScreen;
import socket.GameClient;

/**
 * Implements core game logic.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public final class Core {

	private static final String serverUrl = "http://192.168.219.105:8080/";
	private static final String serverWSUrl = "ws://192.168.219.105:8080/game";
	/** Width of current screen. */
	private static final int WIDTH = 600;
	/** Height of current screen. */
	private static final int HEIGHT = 650;
	/** Max fps of current screen. */
	private static final int FPS = 60;

	/** Base ship type. */
	public static Ship.ShipType BASE_SHIP = Ship.ShipType.StarDefender;
	/** Max lives. */
	public static int MAX_LIVES;
	/** Levels between extra life. */
	public static final int EXTRA_LIFE_FRECUENCY = 3;
	/** Frame to draw the screen on. */
	private static Frame frame;
	/** Screen currently shown. */
	private static Screen currentScreen;
	/** Application logger. */
	private static final Logger LOGGER = Logger.getLogger(Core.class
			.getSimpleName());
	/** Logger handler for printing to disk. */
	private static Handler fileHandler;
	/** Logger handler for printing to console. */
	private static ConsoleHandler consoleHandler;
	/** Initialize singleton instance of SoundManager and return that */
	private static final SoundManager soundManager = SoundManager.getInstance();

	private static long startTime, endTime;

	private static int DifficultySetting;// <- setting EASY(0), NORMAL(1), HARD(2);

	private static UserManager userManager;

	private static GameClient gameClient;
	/**
	 * Test implementation.
	 * 
	 * @param args
	 *            Program args, ignored.
	 */
	public static void main(final String[] args) throws IOException {
		try {
			LOGGER.setUseParentHandlers(false);

			fileHandler = new FileHandler("log");
			fileHandler.setFormatter(new MinimalFormatter());

			consoleHandler = new ConsoleHandler();
			consoleHandler.setFormatter(new MinimalFormatter());

			LOGGER.addHandler(fileHandler);
			LOGGER.addHandler(consoleHandler);
			LOGGER.setLevel(Level.ALL);

		} catch (Exception e) {
			// TODO handle exception
			e.printStackTrace();
		}

		frame = new Frame(WIDTH, HEIGHT);
		DrawManager.getInstance().setFrame(frame);
		int width = frame.getWidth();
		int height = frame.getHeight();

		GameState gameState;
		Wallet wallet = Wallet.getWallet();

		userManager = new UserManager();
		int returnCode = 10;
		do {
			MAX_LIVES = wallet.getLives_lv()+2;
			gameState = new GameState(1, 0, BASE_SHIP, MAX_LIVES, 0, 0, 0, "", 0, 0, 0 ,0, 0);

			GameSettings gameSetting = new GameSettings(4, 4, 60, 2500);

			switch (returnCode) {
				case 1 -> {
					// Main menu.
					currentScreen = new TitleScreen(width, height, FPS, wallet, userManager);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " title screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing title screen.");
				}

				case 2 -> {
					// Game & score.
					do {
						// One extra live every few levels.
						startTime = System.currentTimeMillis();
						boolean bonusLife = gameState.getLevel()
								% EXTRA_LIFE_FRECUENCY == 0
								&& gameState.getLivesRemaining() < MAX_LIVES;
						LOGGER.info("difficulty is " + DifficultySetting);
						//add variation
						gameSetting = gameSetting.LevelSettings(gameSetting.getFormationWidth(),
								gameSetting.getFormationHeight(),
								gameSetting.getBaseSpeed(),
								gameSetting.getShootingFrecuency(),
								gameState.getLevel(), DifficultySetting);

						currentScreen = new GameScreen(gameState,
								gameSetting,
								bonusLife, width, height, FPS, wallet);
						LOGGER.info("Starting " + WIDTH + "x" + HEIGHT

								+ " game screen at " + FPS + " fps.");
						frame.setScreen(currentScreen);
						LOGGER.info("Closing game screen.");

						gameState = ((GameScreen) currentScreen).getGameState();

						gameState = new GameState(gameState, gameState.getLevel() + 1);
						endTime = System.currentTimeMillis();
					} while (gameState.getLivesRemaining() > 0);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getShipType().toString() + " ship, "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					currentScreen = new ScoreScreen(GameSettingScreen.getName(0), width, height, FPS, gameState, wallet, false,userManager);

					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing score screen.");
				}

				case 3 -> {
					//Shop
					currentScreen = new ShopScreen(width, height, FPS, wallet);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " Shop screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing Shop screen.");
				}

				case 4 -> {
//					//rank
					currentScreen = new RankScreen(width,height,FPS,userManager);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
					+ " rank screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing Rank screen.");
				}

				case 5 -> {
					//Setting
					currentScreen = new SettingScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " setting screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing Setting screen.");
				}

				case 6 -> {
					//Game Setting
					currentScreen = new GameSettingScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " game setting screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing game setting screen.");
				}

				case 7 -> {
					//Credit Screen
					currentScreen = new CreditScreen(width, height, FPS);
					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " credit screen at " + FPS + " fps.");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing credit screen.");
				}

				case 8 -> {
					// TwoPlayerScreen
					frame.setSize(WIDTH * 2, HEIGHT);
					frame.moveToMiddle();

					currentScreen = new TwoPlayerScreen(gameState, gameSetting, width, height, FPS, wallet);
					LOGGER.info("Two player starting " + WIDTH + "x" + HEIGHT
							+ " game screen at " + FPS + " fps.");
					frame.setScreen(currentScreen);
					LOGGER.info("Closing game screen.");

					frame.setSize(WIDTH, HEIGHT);
					frame.moveToMiddle();

					gameState = ((TwoPlayerScreen) currentScreen).getWinnerGameState();
					int winnerNumber = ((TwoPlayerScreen) currentScreen).getWinnerNumber();

					LOGGER.info("Starting " + WIDTH + "x" + HEIGHT
							+ " score screen at " + FPS + " fps, with a score of "
							+ gameState.getScore() + ", "
							+ gameState.getLivesRemaining() + " lives remaining, "
							+ gameState.getBulletsShot() + " bullets shot and "
							+ gameState.getShipsDestroyed() + " ships destroyed.");
					DrawManager.getInstance().setFrame(frame);
					currentScreen = new ScoreScreen(GameSettingScreen.getName(winnerNumber), width, height, FPS, gameState, wallet, true,userManager);
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing score screen.");
				}
				case 9 -> { // 회원가입 화면으로 이동
					currentScreen = new RegisterScreen(width, height, FPS, userManager);
					LOGGER.info("Starting Register Screen");
					returnCode = frame.setScreen(currentScreen);
				}
				case 10 -> { // 회원가입 성공 -> 로그인 화면으로 복귀
					currentScreen = new LoginScreen(width, height, FPS, userManager);
					LOGGER.info("Returning to Login Screen");
					returnCode = frame.setScreen(currentScreen);
				}
				case 11 ->{
					currentScreen = new PvpLobbyScreen(width,height,FPS,userManager);
					LOGGER.info("Starting PVP Lobby Screen");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing PVP Lobby Screen.");
				}
				case 12 ->{
					currentScreen = new WaitingRoomScreen(width,height,FPS,userManager);
					LOGGER.info("Starting Waiting Room Screen");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing Waiting Room Screen.");
				}
				case 13 ->{
					currentScreen = new PvpGameScreen(width,height,FPS,userManager);
					LOGGER.info("Starting PVP Game Screen");
					returnCode = frame.setScreen(currentScreen);
					LOGGER.info("Closing PVP Game Screen.");
				}

				default -> { // Exit
					LOGGER.info("Exiting game.");
					returnCode = 0;
				}
			}

		} while (returnCode != 0);
		fileHandler.flush();
		fileHandler.close();
		soundManager.closeAllSounds();

		System.exit(0);
	}

	/**
	 * Constructor, not called.
	 */
	private Core() {

	}

	/**
	 * Controls access to the logger.
	 * 
	 * @return Application logger.
	 */
	public static Logger getLogger() {
		return LOGGER;
	}

	/**
	 * Controls access to the drawing manager.
	 * 
	 * @return Application draw manager.
	 */
	public static DrawManager getDrawManager() {
		return DrawManager.getInstance();
	}

	/**
	 * Controls access to the input manager.
	 * 
	 * @return Application input manager.
	 */
	public static InputManager getInputManager() {
		return InputManager.getInstance();
	}

	/**
	 * Controls access to the file manager.
	 * 
	 * @return Application file manager.
	 */
	public static FileManager getFileManager() {
		return FileManager.getInstance();
	}

	/**
	 * Controls creation of new cooldowns.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @return A new cooldown.
	 */
	public static Cooldown getCooldown(final int milliseconds) {
		return new Cooldown(milliseconds);
	}

	/**
	 * Controls creation of new cooldowns with variance.
	 * 
	 * @param milliseconds
	 *            Duration of the cooldown.
	 * @param variance
	 *            Variation in the cooldown duration.
	 * @return A new cooldown with variance.
	 */
	public static Cooldown getVariableCooldown(final int milliseconds,
			final int variance) {
		return new Cooldown(milliseconds, variance);
	}

	/**
	 * @param level set LevelSetting from GameSettingScreen
	 */
	public static void setLevelSetting(final int level) {
		DifficultySetting = level;
	}

	public static int getLevelSetting(){
		return DifficultySetting;
	}

	public static String getServerUrl()
	{
		return serverUrl;
	}
	public static String getServerWSUrl()
	{
		return serverWSUrl;
	}
	public static GameClient getGameClient() throws URISyntaxException, InterruptedException
    {
		if(gameClient == null)
		{
			gameClient = new GameClient();
			gameClient.connectBlocking();
		}
		return gameClient;
	}
	public static void removeGameClient()
	{
		gameClient.close();
		gameClient = null;
	}
}