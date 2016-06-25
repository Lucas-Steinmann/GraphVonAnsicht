package edu.kit.student.objectproperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;

// Implementation aehnlich wie im Beispiel "Person" bei https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableView.html

/**
 * The GAnsProperty is a single property of a vertex or an edge in GAns. The
 * name and value of the property are being stored in JavaFX-Properties. A graph
 * consists out of vertices and edges which contain GAnsProperties, which are
 * linked to the GUI-elements.
 * 
 * @param <T>
 *            The type of element that is to be stored in the GAnsProperty.
 * 
 * @author Nicolas
 */
public class GAnsProperty<T extends Object> {

	/**
	 * A string with whom, factories or other elements from the GUI, can
	 * reference to the name/identifier of the value.
	 */
	public final static String name = "propertyName";

	/**
	 * A string with whom, factories or other elements from the GUI, can
	 * reference to the value.
	 */
	public final static String value = "propertyValue";

	/**
	 * A string with whom, factories or other elements from the GUI, can
	 * reference to the string-representation of the value.
	 */
	public final static String valueAsString = "propertyValueAsString";

	private StringProperty propertyName;
	private GAnsObjectProperty<T> propertyValue;
	private StringProperty propertyValueAsString;

	/**
	 * Constructor, setting the name and value of the property.
	 * 
	 * @param name
	 *            The string will be set as the name of the GAnsProperty.
	 * @param value
	 *            The value that will be set in the GAnsProperty.
	 */
	public GAnsProperty(String name, T value) {
		setName(name);
		setValue(value);
	}

	/**
	 * Ensures that the property which contains the name/identifier of the
	 * GAnsProperty is not null and always set with the right name.
	 * 
	 * @return The StringProperty which contains the name/identifier of the
	 *         GAnsProperty.
	 */
	public StringProperty propertyNameProperty() {
		if (propertyName == null)
			propertyName = new SimpleStringProperty(this, name);
		return propertyName;
	}

	/**
	 * Ensures that the property which contains the value in the GAnsProperty is
	 * not null and always set with the right name.
	 * 
	 * @return The property which contains the value in the GAnsProperty.
	 */
	public GAnsObjectProperty<T> propertyValue() {
		if (propertyValue == null)
			propertyValue = new GAnsObjectProperty<T>(this, value);
		return propertyValue;
	}

	/**
	 * Ensures that the property which contains the string-representation of the
	 * value in the GAnsProperty is not null and always set with the right name.
	 * 
	 * @return The StringProperty which contains the string-representation of
	 *         the value of the GAnsProperty.
	 */
	public StringProperty propertyValueAsString() {
		if (propertyValueAsString == null) {
			propertyValueAsString = new SimpleStringProperty(this, valueAsString);
		}
		return propertyValueAsString;
	}

	/**
	 * Sets the name/identifier of the GAnsProperty.
	 * 
	 * @param value
	 *            The string will be set as the name of the GAnsProperty.
	 */
	public void setName(String value) {
		propertyNameProperty().set(value);
	}

	/**
	 * Returns the name/identifier of the GAnsProperty.
	 * 
	 * @return The name/identifier of the GAnsProperty.
	 */
	public String getName() {
		return propertyNameProperty().get();
	}

	/**
	 * Sets the value and its string-representation of the GAnsProperty.
	 * 
	 * @param value
	 *            The value that will be set in the GAnsProperty.
	 */
	public void setValue(T value) {
		propertyValue().setValue(value);
		propertyValueAsString().set(value.toString());
	}

	/**
	 * Returns the value of the GAnsProperty.
	 * 
	 * @return The value of the GAnsProperty.
	 */
	public T getValue() {
		return propertyValue().getValue();
	}

	/**
	 * Returns the string-representation of the value from the GAnsProperty.
	 * 
	 * @return The string-representation of the value from the GAnsProperty.
	 */
	public String getValueAsString() {
		return propertyValueAsString().toString();
	}

	/**
	 * Adds a ChangeListener to the value of the property.
	 * 
	 * @param listener
	 *            The listener that will be added.
	 */
	public void addListenerToValue(ChangeListener<T> listener) {
		propertyValue().addListener(listener);
	}

	/**
	 * Removes a ChangeListener from the value of the property.
	 * 
	 * @param listener
	 *            The listener that will be removed.
	 */
	public void removeListenerFromValue(ChangeListener<T> listener) {
		propertyValue().removeListener(listener);
	}
}
