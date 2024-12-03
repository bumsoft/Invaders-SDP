package engine;

/**
 * Implements an object that stores a single game's difficulty settings.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class GameSettings {
	private int difficulty;
	/** Width of the level's enemy formation. */
	private int formationWidth;
	/** Height of the level's enemy formation. */
	private int formationHeight;
	/** Speed of the enemies, function of the remaining number. */
	private int baseSpeed;
	/** Frequency of enemy shootings, +/- 30%. */
	private int shootingFrecuency;

	/**
	 * Constructor.
	 * 
	 * @param formationWidth
	 *            Width of the level's enemy formation.
	 * @param formationHeight
	 *            Height of the level's enemy formation.
	 * @param baseSpeed
	 *            Speed of the enemies.
	 * @param shootingFrequency
	 *            Frecuen
	 *            cy of enemy shootings, +/- 30%.
	 */
	public GameSettings(final int formationWidth, final int formationHeight,
			final int baseSpeed, final int shootingFrequency) { // fix typo
		this.formationWidth = formationWidth;
		this.formationHeight = formationHeight;
		this.baseSpeed = baseSpeed;
		this.shootingFrecuency = shootingFrequency;
	}

	public GameSettings(GameSettings gameSettings) { // fix typo
		this.formationWidth = gameSettings.formationWidth;
		this.formationHeight = gameSettings.formationHeight;
		this.baseSpeed = gameSettings.baseSpeed;
		this.shootingFrecuency = gameSettings.shootingFrecuency;
	}

	/**
	 * @return the formationWidth
	 */
	public final int getFormationWidth() {
		return formationWidth;
	}

	/**
	 * @return the formationHeight
	 */
	public final int getFormationHeight() {
		return formationHeight;
	}

	/**
	 * @return the baseSpeed
	 */
	public final int getBaseSpeed() {
		return baseSpeed;
	}

	/**
	 * @return the shootingFrecuency
	 */
	public final int getShootingFrecuency() {
		return shootingFrecuency;
	}

	public void setFormation(int n) {
		if (this.formationWidth == this.formationHeight) {
			if (this.formationWidth < 14) this.formationWidth += n;
		} else {
			if (this.formationHeight < 10) this.formationHeight += n;
		}
	}
	public void setBaseSpeed(int n) {
		if (this.baseSpeed - n > 0) this.baseSpeed -= n;
		else this.baseSpeed = 0;
	}
	public void setShootingFrecuency(int n) {
		if(this.shootingFrecuency-n > 100) this.shootingFrecuency -= n;
		else this.shootingFrecuency = 100;

	}
	/**
	 *
	 * @param level Level
	 * @param difficulty set difficulty
	 */
	public void LevelSettings(int level, int difficulty) {
		this.difficulty = difficulty;
		switch (difficulty) {
			case 0 -> {
				if(level < 5){
					if(level%3 == 0)
						setFormation(1);
                }else if(level < 10){
					if(level % 2 == 0) {
						setFormation(1);
						setBaseSpeed(10);
					}
				}else {
					setFormation(1);
					setBaseSpeed(10);
				}
				setShootingFrecuency(100);
			}
			case 1 -> {
				if(level < 5){
					setShootingFrecuency(100);
					if(level%2 == 0) {
						setFormation(1);
						setBaseSpeed(10);
					}
				}else if(level < 10){
					setShootingFrecuency(200);
					if(level % 2 == 0) {
						setFormation(1);
						setBaseSpeed(10);
					}
				}else{
					setFormation(1);
					setBaseSpeed(10);
					setShootingFrecuency(300);
				}
			}
			case 2 -> {
				if(level < 5){
					setShootingFrecuency(300);
					if(level%2 == 0) {
						setFormation(1);
						setBaseSpeed(20);
					}
				}else{
					setFormation(2);
					setBaseSpeed(20);
					setShootingFrecuency(300);
				}
			}
			default -> {
				break;
			}
		}
	}

	/**
	 * @return difficulty
	 */
	public int getDifficulty() {
		return difficulty;
	}

}
