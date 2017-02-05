package edu.kit.student.parameter;

/**
 * BooleanParameter are parameters with an boolean valueId space.
 */
public class BooleanParameter extends Parameter<BooleanParameter, Boolean> {

	/**
     * Constructs a new BooleanParameter, sets its name and its default valueId.
	 * @param name 	The name of the parameter.
	 * @param value The valueId of the parameter.
	 */
	public BooleanParameter(String name, boolean value) {
		super(name, value);
	}

	@Override
	public void accept(ParameterVisitor visitor) {
		visitor.visit(this);
	}
}
