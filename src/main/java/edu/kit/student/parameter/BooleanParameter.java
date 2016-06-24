package edu.kit.student.parameter;

/**
 * BooleanParameter are parameters with an boolean value space.
 */
public class BooleanParameter extends Parameter<BooleanParameter, Boolean> {

	/**
     * Constructs a new BooleanParameter, sets its name and its default value.
	 * @param name 	The name of the parameter.
	 * @param value The value of the parameter.
	 */
	public BooleanParameter(String name, boolean value) {
		super(name, value);
	}

	@Override
	public void accept(ParameterVisitor visitor) {
		// TODO Auto-generated method stub
	}

	@Override
	public int compareTo(BooleanParameter o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
