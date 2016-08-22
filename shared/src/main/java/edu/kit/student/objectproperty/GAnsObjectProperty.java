package edu.kit.student.objectproperty;

import javafx.beans.property.SimpleObjectProperty;

/**
 * A JavaFX object property which in toString() returns the toString() of the
 * value.
 * 
 * @author Nicolas
 * @param <T> 
 */
public class GAnsObjectProperty<T> extends SimpleObjectProperty<T> {

	/**
	 * Constructor.
	 * 
	 * @param parent
	 *            object that contains the GAnsObjectProperty.
	 * @param name
	 *            name that will be given to the SimpleObjectProperty
	 *            constructor.
	 */
	public GAnsObjectProperty(Object parent, String name) {
		super(parent, name);
	}

	@Override
	public String toString() {
		return getValue().toString();
	}
}