package edu.kit.student.parameter;

/**
 * StringParameter are parameters with an freely set String valueId space.
 */
public class StringParameter extends Parameter<StringParameter, String> {

    /**
     * Constructs a new StringParameter, sets its name and its default valueId.
     * @param name The name of the parameter.
     * @param value The valueId of the parameter.
     */
    public StringParameter(String name, String value) {
        super(name, value);
    }

	@Override
	public void accept(ParameterVisitor visitor) {
		visitor.visit(this);
	}
}
