package edu.kit.student.parameter;

/**
 * DoubleParameters are parameters with an double value space.
 */
public class DoubleParameter extends Parameter<DoubleParameter, Double> {

	private Double min;
	private Double max;
	
    /**
     * Constructs a new DoubleParameter, sets its name, its default value and boundaries.
     * @param name The name of the parameter.
     * @param value The value of the parameter.
     * @param min The minimum boundary of the parameter.
     * @param max The maximum boundary of the parameter.
     */
	public DoubleParameter(String name, Double value, Double min, Double max) {
		super(name, value);
		this.min = min;
		this.max = max;
	}

	@Override
	public void accept(ParameterVisitor visitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public int compareTo(DoubleParameter o) {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * Returns the minimum boundary.
	 * @return The minimum boundary.
	 */
	public double getMin() {
		return min;
	}

	/**
	 * Sets the minimum boundary.
	 * @param min The minimum boundary.
	 */
	public void setMin(double min) {
		this.min = min;
	}

	/**
	 * Returns the maximum boundary.
	 * @return The maximum boundary.
	 */
	public double getMax() {
		return max;
	}

	/**
	 * Sets the maximum boundary.
	 * @param min The maximum boundary.
	 */
	public void setMax(double max) {
		this.max = max;
	}
}
