package engine;

/**
 * Imposes a cooldown period between two actions.
 * 
 * @author <a href="mailto:RobertoIA1987@gmail.com">Roberto Izquierdo Amo</a>
 * 
 */
public class Cooldown {

	/** Cooldown duration. */
	private int milliseconds;
	/** Maximum difference between durations. */
	private int variance;
	/** Duration of this run, varies between runs if variance > 0. */
	private int duration;
	/** Beginning time. */
	private long time;
	/** Special Enemy Alert. */
	private int alert = 3000;

	/**
	 * Constructor, established the time until the action can be performed
	 * again.
	 * 
	 * @param milliseconds
	 *            Time until cooldown period is finished.
	 */
	protected Cooldown(final int milliseconds) {
		this.milliseconds = milliseconds;
		this.variance = 0;
		this.duration = milliseconds;
		this.time = 0;
	}

	/**
	 * Constructor, established the time until the action can be performed
	 * again, with a variation of +/- variance.
	 * 
	 * @param milliseconds
	 *            Time until cooldown period is finished.
	 * @param variance
	 *            Variance in the cooldown period.
	 */
	protected Cooldown(final int milliseconds, final int variance) {
		this.milliseconds = milliseconds;
		this.variance = variance;
		this.time = 0;
	}

	/**
	 * Checks if the cooldown is finished.
	 * 
	 * @return Cooldown state.
	 */

	public final boolean checkFinished() {
		if ((this.time == 0)
				|| this.time + this.duration < System.currentTimeMillis())
			return true;
		return false;
	}

	/**
	 * Checks if the cooldown has as much time left as 'alert'.
	 *
	 * @return Alert state.
	 */

	public final boolean checkAlert() {
		if ((this.time > 0)
				&& this.time + this.duration - this.alert <= System.currentTimeMillis())
			return true;
		return false;
	}
 
  // 경고등 애니메이션 //
	public final int checkAlertAnimation() {
		if ((this.time > 0)
				&& this.time + this.duration - (this.alert / 3) <= System.currentTimeMillis())
			return 3;
		else if ((this.time > 0)
				&& this.time + this.duration - 2 * (this.alert / 3) <= System.currentTimeMillis())
			return 2;
		else if ((this.time > 0)
				&& this.time + this.duration - this.alert <= System.currentTimeMillis())
			return 1;
		return 0;
	}

	/**
	 * Restarts the cooldown.
	 */
	public final void reset() {
		this.time = System.currentTimeMillis();
		if (this.variance != 0)
			this.duration = (this.milliseconds - this.variance)
					+ (int) (Math.random()
							* (this.milliseconds + this.variance));
	}
}
