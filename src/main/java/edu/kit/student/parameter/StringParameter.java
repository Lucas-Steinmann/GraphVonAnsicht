package edu.kit.student.parameter;

/**
 * StringParameter are parameters with an freely set String value space.
 */
public class StringParameter extends Parameter<StringParameter, String> {
    private String value;

    /**
     * Constructs a new StringParameter, sets its name and its default value.
     * @param name The name of the parameter.
     * @param value The value of the parameter.
     */
    public StringParameter(String name, String value) {
        super(name, value);
    }

    @Override
    public int compareTo(StringParameter o) {
        return this.value.compareTo(o.getValue());
    }

	@Override
	public void accept(ParameterVisitor visitor) {
		visitor.visit(this);
	}
}
