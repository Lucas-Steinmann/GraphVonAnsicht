package edu.kit.student.objectproperty;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;


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
	public final static String name = "name";

	/**
	 * A string with whom, factories or other elements from the GUI, can
	 * reference to the value.
	 */
	public final static String value = "value";

	/**
	 * A string with whom, factories or other elements from the GUI, can
	 * reference to the string-representation of the value.
	 */
	public final static String valueAsString = "valueAsString";

	private StringProperty nameProperty;
	private GAnsObjectProperty<T> valueProperty;
	private StringProperty valueAsStringProperty;

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
		
		valueAsStringProperty.bind(valueProperty.asString());
	}

	/**
	 * Ensures that the property which contains the name/identifier of the
	 * GAnsProperty is not null and always set with the right name.
	 * 
	 * @return The StringProperty which contains the name/identifier of the
	 *         GAnsProperty.
	 */
	public StringProperty propertyNameProperty() {
		if (nameProperty == null)
			nameProperty = new SimpleStringProperty(this, name);
		return nameProperty;
	}

	/**
	 * Ensures that the property which contains the value in the GAnsProperty is
	 * not null and always set with the right name.
	 * 
	 * @return The property which contains the value in the GAnsProperty.
	 */
	public GAnsObjectProperty<T> propertyValue() {
		if (valueProperty == null)
			valueProperty = new GAnsObjectProperty<T>(this, value);
		return valueProperty;
	}

	/**
	 * Ensures that the property which contains the string-representation of the
	 * value in the GAnsProperty is not null and always set with the right name.
	 * 
	 * @return The StringProperty which contains the string-representation of
	 *         the value of the GAnsProperty.
	 */
	public StringProperty propertyValueAsString() {
		if (valueAsStringProperty == null)
			valueAsStringProperty = new SimpleStringProperty(this, valueAsString);
		return valueAsStringProperty;
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
		//maybe not needed anymore, because of its binding with value in the constructor.
		propertyValueAsString().set(value.toString());
	}
	
	public void setValueAsString(String value) {
		propertyValueAsString().set(value);
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
		return propertyValueAsString().get();
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
	
	@Override
	public String toString() {
		return "Property: " + this.getName() + ", " + this.getValueAsString(); 
	}
}