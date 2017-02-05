package edu.kit.student.graphmodel.builder;

/**
 * An abstract interface, which is used to build an edge.
 */
public interface IEdgeBuilder {

	/**
	 * Sets the ID of the edge build by this.
	 * 
	 * @param id valueId to which the ID is set
	 */
	void setID(String id);

	/**
	 * Adds additional data to this edge.
	 * The specific EdgeBuilder implementation needs to decide
	 * how to save the valueId for given edge type.
	 * 
	 * @param key name of the attribute
	 * @param value      valueId of the attribute
	 */
	void addData(String key, String value);
}
