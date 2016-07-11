package edu.kit.student.graphmodel.serialize;

import java.util.Map;

import edu.kit.student.graphmodel.Edge;

public abstract class SerializedGraphElement {
	private final Map<String, String> shapeProperties;
	private final Map<String, String> metaProperties;

	public SerializedGraphElement(Map<String, String> shapeProperties, Map<String, String> metaProperties) {
		this.shapeProperties = shapeProperties;
		this.metaProperties = metaProperties;
	}

	/**
	 * Gets all serialized Attributes from the element as a Map from String to String.
	 * This Map gets created when serializing a {@link Edge} and is returned on demand.
	 * This should only be used for exporting Edges since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Edge}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
	 */
	public Map<String, String> getShapeProperties() {
		return shapeProperties;
	}
	
	/**
	 * Gets all serialized metadata from the element as a Map from String to String.
	 * This Map gets created when serializing a {@link Edge} and is returned on demand.
	 * This should only be used for exporting Edges since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Edge}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
	 */
	public Map<String, String> getMetaProperties() {
		return metaProperties;
	}

}
