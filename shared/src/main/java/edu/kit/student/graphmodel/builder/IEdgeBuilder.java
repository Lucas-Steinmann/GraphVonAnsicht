package edu.kit.student.graphmodel.builder;

import edu.kit.student.graphmodel.Edge;

/**
 * An abstract interface, which is used to build one edge.
 */
public interface IEdgeBuilder {

	/**
	 * Sets the ID of the edge build by this.
	 * 
	 * @param id
	 *            value to which the id is set
	 */
	public abstract void setID(String id);


	/**
	 * Sets source and target vertices of the edge build by this.
	 *
	 * @param source
	 *            String representation of the source vertex as ID
	 * @param target
	 *            String representation of the target vertex as ID
	 */
	public abstract void newEdge(String source, String target);

	/**
	 * Adds additional data to this edge. The specific EdgeBuilder
	 * implementation needs to decide how to save the value for given edge type.
	 * 
	 * @param keyname
	 * 				Name of the attribute
	 * @param value
	 * 				Value of the attribute
	 */
	public abstract void addData(String keyname, String value);
}
