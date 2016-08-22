package edu.kit.student.graphmodel.builder;

/**
 * An abstract interface, which is used to build one vertex.
 */
public interface IVertexBuilder {

	/**
	 * Sets the ID of the vertex build by this.
	 * 
	 * @param id
	 *           value to which the id is set
	 */
	public abstract void setID(String id);
	
	/**
	 * Add Data to this Vertex. The IVertexBuilder needs to parse the data and add it to the edge
	 * 
	 * @param keyname
	 * 			Name of the attribute which is added
	 * @param value
	 * 			Value of the attribute
	 */
	public abstract void addData(String keyname, String value) throws IllegalArgumentException;
}
