package engine;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.*;
import java.util.logging.Logger;
import java.util.Properties;

import engine.DrawManager.SpriteType;
import entity.Wallet;
import entity.Achievement;

/**
 * Manages files used in the application.
 *
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 *
 */
public final class FileManager {

	/** Singleton instance of the class. */
	private static FileManager instance;
	/** Application logger. */
	private static Logger logger;
	/** Max number of high scores. */
	private static final int MAX_SCORES = 3;

	/**
	 * private constructor.
	 */
	private FileManager() {
		logger = Core.getLogger();
	}

	/**
	 * Returns shared instance of FileManager.
	 *
	 * @return Shared instance of FileManager.
	 */
	protected static FileManager getInstance() {
		if (instance == null)
			instance = new FileManager();
		return instance;
	}

	/**
	 * Loads sprites from disk.
	 *
	 * @param spriteMap
	 *            Mapping of sprite type and empty boolean matrix that will
	 *            contain the image.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void loadSprite(final Map<SpriteType, boolean[][]> spriteMap) throws IOException {
        try (InputStream inputStream = DrawManager.class.getClassLoader().getResourceAsStream("graphics");
			 BufferedReader reader = inputStream != null ? new BufferedReader(new InputStreamReader(inputStream)) : null) {

			if (reader == null)
				throw new IOException("Graphics file not found.");

            String line;

            // Sprite loading.
            for (Map.Entry<SpriteType, boolean[][]> sprite : spriteMap.entrySet()) {

                int idx = 0;
                do {
					line = reader.readLine();

					if (line == null)
						throw new IOException("Sprite data not found.");

				} while (line.trim().isEmpty() || line.trim().startsWith("#"));

                for (int i = 0; i < sprite.getValue().length; i++) {
                    for (int j = 0; j < sprite.getValue()[i].length; j++) {
                        char c = line.charAt(idx++);
                        sprite.getValue()[i][j] = c == '1';
                    }
                }

                logger.fine("Sprite " + sprite.getKey() + " loaded.");
            }
        }
	}

	/**
	 * Loads a font of a given size.
	 *
	 * @param size
	 *            Point size of the font.
	 * @return New font.
	 * @throws IOException
	 *             In case of loading problems.
	 * @throws FontFormatException
	 *             In case of incorrect font format.
	 */
	public Font loadFont(final float size) throws IOException,
			FontFormatException {
		InputStream inputStream = null;
		Font font;

		try {
			// Font loading.
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("space_invaders.ttf");
			font = Font.createFont(Font.TRUETYPE_FONT, inputStream).deriveFont(
					size);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return font;
	}

	/**
	 * Returns the application default scores if there is no user high scores
	 * file.
	 *
	 * @return Default high scores.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	private List<Score> loadDefaultHighScores() throws IOException {
		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader reader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("scores");
			reader = new BufferedReader(new InputStreamReader(inputStream));

			Score highScore = null;
			String name = reader.readLine();
			String score = reader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score));
				highScores.add(highScore);
				name = reader.readLine();
				score = reader.readLine();
			}
		} finally {
			if (inputStream != null)
				inputStream.close();
		}

		return highScores;
	}

	/**
	 * Loads high scores from file, and returns a sorted list of pairs score -
	 * value.
	 *
	 * @return Sorted list of scores - players.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public List<Score> loadHighScores() throws IOException {

		List<Score> highScores = new ArrayList<Score>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);
			inputStream = new FileInputStream(scoresFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			logger.info("Loading user high scores.");

			Score highScore = null;
			String name = bufferedReader.readLine();
			String score = bufferedReader.readLine();

			while ((name != null) && (score != null)) {
				highScore = new Score(name, Integer.parseInt(score));
				highScores.add(highScore);
				name = bufferedReader.readLine();
				score = bufferedReader.readLine();
			}

		} catch (FileNotFoundException e) {
			// loads default if there's no user scores.
			logger.info("Loading default high scores.");
			highScores = loadDefaultHighScores();
		} finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		Collections.sort(highScores);
		return highScores;
	}

	public Achievement loadAchievement() throws IOException {
		Achievement achievement = null;
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;
		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String achievementPath = new File(jarPath).getParent();
			achievementPath += File.separator;
			achievementPath += "achievement";

			File achievementFile = new File(achievementPath);
			if (!achievementFile.exists())
				achievementFile.createNewFile();

			inputStream = new FileInputStream(achievementFile);
			bufferedReader = new BufferedReader(new InputStreamReader(
					inputStream, Charset.forName("UTF-8")));

			// Load properties from the file
			Properties properties = new Properties();
			properties.load(bufferedReader);

			logger.info("Loading user total score.");

			int totalPlay = Integer.parseInt(properties.getProperty("total_play", "0"));
			int totalScore = Integer.parseInt(properties.getProperty("total_score", "0"));
			double highAccuracy = Double.parseDouble(properties.getProperty("high_accuracy", "0"));
			int perfectStage = Integer.parseInt(properties.getProperty("perfect_stage", "0"));
			boolean flawlessFailure = properties.getProperty("flawless_failure", "0").equals("true");
			boolean bestFriends = properties.getProperty("best_friends", "false").equals("true");

			achievement = new Achievement(totalPlay, totalScore, highAccuracy, perfectStage, flawlessFailure, bestFriends);

		} catch (FileNotFoundException e) {
			// loads default if there's no user scores.
			logger.info("File not found.");
		} catch (NumberFormatException e) {
			logger.warning("Invalid format for total score. Defaulting to 0.");
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
		}
		return achievement;
	}


	public List<String> loadCreditList() throws IOException {  // 사용자의 크레딧 파일을 로드

		List<String> creditname = new ArrayList<String>();
		InputStream inputStream = null;
		BufferedReader bufferedReader = null;

		try {
			inputStream = FileManager.class.getClassLoader()
					.getResourceAsStream("creditlist");
			bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

			logger.info("Loading credit list.");

			String name = bufferedReader.readLine();

			while (name != null) {
				creditname.add(name);
				name = bufferedReader.readLine();
			}

		}finally {
			if (bufferedReader != null)
				bufferedReader.close();
		}

		return creditname;
	}


	/**
	 * Saves user high scores to disk.
	 *
	 * @param highScores
	 *            High scores to save.
	 * @throws IOException
	 *             In case of loading problems.
	 */
	public void saveHighScores(final List<Score> highScores)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8");

			String scoresPath = new File(jarPath).getParent();
			scoresPath += File.separator;
			scoresPath += "scores";

			File scoresFile = new File(scoresPath);

			if (!scoresFile.exists())
				scoresFile.createNewFile();

			outputStream = new FileOutputStream(scoresFile);
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user high scores.");

			for (Score score : highScores) {
				bufferedWriter.write(score.getName());
				bufferedWriter.newLine();
				bufferedWriter.write(Integer.toString(score.getScore()));
				bufferedWriter.newLine();
			}

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	public void saveWallet(final Wallet newWallet)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8"); // 현재 파일 실행 경로. Current file execution path

			String walletPath = new File(jarPath).getParent(); // 상위 파일 경로. Parent file path
			walletPath += File.separator; // 파일 경로에 '/' 또는 '\' 추가(환경마다 다름). Add '/' or '\' to the file path (depends on the environment)
			walletPath += "wallet";

			File walletFile = new File(walletPath);

			if (!walletFile.exists())
				walletFile.createNewFile(); //파일이 없으면 새로 만듦. If the file does not exist, create a new one.

			outputStream = new FileOutputStream(walletFile); //덮어쓰기. Overwrite
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving user wallet.");

			bufferedWriter.write(newWallet.getCoin() + "");
			bufferedWriter.newLine();
			bufferedWriter.write(newWallet.getBullet_lv() + "");
			bufferedWriter.newLine();
			bufferedWriter.write(newWallet.getShot_lv() + "");
			bufferedWriter.newLine();
			bufferedWriter.write(newWallet.getLives_lv() + "");
			bufferedWriter.newLine();
			bufferedWriter.write(newWallet.getCoin_lv() + "");
			bufferedWriter.newLine();

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

	public BufferedReader loadWallet() throws IOException {
		String jarPath = FileManager.class.getProtectionDomain()
				.getCodeSource().getLocation().getPath();
		jarPath = URLDecoder.decode(jarPath, "UTF-8");

		String walletPath = new File(jarPath).getParent();
		walletPath += File.separator;
		walletPath += "wallet"; // 지갑 파일 경로. Wallet file path

		File walletFile = new File(walletPath);
		if (!walletFile.exists()) {
			Core.getLogger().warning("Wallet file not found at " + walletPath);
			return null; // 파일이 없으면 null 반환. If the file does not exist, return null.
		}

		InputStream inputStream = new FileInputStream(walletFile);
		return new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	}

	public void saveAchievement(final Achievement achievement)
			throws IOException {
		OutputStream outputStream = null;
		BufferedWriter bufferedWriter = null;

		try {
			String jarPath = FileManager.class.getProtectionDomain()
					.getCodeSource().getLocation().getPath();
			jarPath = URLDecoder.decode(jarPath, "UTF-8"); // 현재 파일 실행 경로. Current file execution path

			String achievementPath = new File(jarPath).getParent(); // 상위 파일 경로. Parent file path
			achievementPath += File.separator; // 파일 경로에 '/' 또는 '\' 추가(환경마다 다름). Add '/' or '\' to the file path (depends on the environment)
			achievementPath += "achievement";

			File achievementFile = new File(achievementPath);

			if (!achievementFile.exists())
				achievementFile.createNewFile(); //파일이 없으면 새로 만듦. If the file does not exist, create a new one.

			outputStream = new FileOutputStream(achievementFile); //덮어쓰기. Overwrite
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(
					outputStream, Charset.forName("UTF-8")));

			logger.info("Saving achievement.");
			bufferedWriter.write("total_play=" + achievement.getTotalPlayTime());
			bufferedWriter.newLine();
			bufferedWriter.write("total_score=" + achievement.getTotalScore());
			bufferedWriter.newLine();
			bufferedWriter.write("high_accuracy=" + achievement.getHighAccuracy());
			bufferedWriter.newLine();
			bufferedWriter.write("perfect_stage=" + achievement.getPerfectStage());
			bufferedWriter.newLine();
			bufferedWriter.write("flawless_failure=" + achievement.getFlawlessFailure());
			bufferedWriter.newLine();
			bufferedWriter.write("best_friends=" + achievement.getBestFriends());
			bufferedWriter.newLine();

		} finally {
			if (bufferedWriter != null)
				bufferedWriter.close();
		}
	}

}
