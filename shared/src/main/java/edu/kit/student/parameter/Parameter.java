package edu.kit.student.parameter;

import edu.kit.student.objectproperty.GAnsProperty;

/**
 * An abstract parameter class.
 * A Parameter contains a valueId and a name. The valueId can be transformed into a string.
 * Clients can set Listeners to track changes of the valueId.
 * Classes inheriting from this class can visited by a ParameterVisitor.
 * @param <T> 
 * @param <V> 
 */
public abstract class Parameter<T, V extends Object> extends GAnsProperty<V> {
	
	private V tempValue;
	
	/**
	 * Constructor, setting the name and valueId of the property.
	 * 
	 * @param name
	 *            The string will be set as the name of the GAnsProperty.
	 * @param value
	 *            The valueId that will be set in the GAnsProperty.
	 */
	public Parameter(String name, V value) {
		super(name, value);
		tempValue = value;
	}
    
    /**
     * Let the visitor visit this parameter.
     * @param visitor The visitor to visit
     */
    public abstract void accept(ParameterVisitor visitor);
    
    /**
     * Caches the current valueId in a variable.
     * This has to be called before reset() is called, 
     * otherwise the cached valueId is the initial valueId of the parameter.
     */
    public void cacheCurrentValue() {
    	tempValue = this.getValue();
    }
    
    /**
     * Sets the valueId of the parameter to the valueId cached via cacheCurrentValue().
     * If no valueId is cached, the initial valueId of the parameter is set.
     */
    public void reset() {
    	this.setValue(tempValue);
    }
}
