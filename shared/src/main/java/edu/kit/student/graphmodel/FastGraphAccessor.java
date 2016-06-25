package edu.kit.student.graphmodel;

import java.util.List;

/**
 * This class provides a fast lookup of {@link Vertex} and {@link Edge} for a given Attribute value pair without traversing a {@link Graph}.
 * FastGraphAccessor is a helper class for looking up all {@link Vertex} and {@link Edge} that have a specific value
 * for a specific attribute. To achieve this all elements of a Graph need to add their attributes and values to a
 * FastGraphAccessor. These values are not linked to the origin values so the fastGraphAccessor needs to be updated
 * after changes when needed for following steps. This should be done by reverting and adding the values again.
 */
public class FastGraphAccessor {
	/**
	 * Adds an {@link Edge} for a given attribute with a given value.
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param edge edge that has this value for the given attribute
	 */
	public void addEdgeForAttribute(String name, String value, Edge edge) {
		// TODO Auto-generated method
	}

	/**
	 * Adds an {@link Edge} for a given attribute with a given value.
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param edge edge that has this value for the given attribute
	 */
	public void addEdgeForAttribute(String name, Edge edge, int value) {
		// TODO Auto-generated method
	}

	/**
	 * gets a List of {@link Edge} that contains all {@link Edge} that have the value for given attribute
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return a {@link List} of {@link Edge} that has tthe value for given attribute
	 */
	public List<Edge> getEdgesByAttribute(String name, String value) {
		// TODO Auto-generated method
		return null;
	}

	/**
	 * gets a {@link List} of {@link Edge} that contains all {@link Edge} that have the value for given attribute
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return a {@link List} of {@link Edge} that has the value for given attribute
	 */
	public List<Edge> getEdgesByAttribute(String name, int value) {
		// TODO Auto-generated method
		return null;
	}

	/**
	 * adds an {@link Vertex} for a given attribute with a given value
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param vertex vertex that has this value for the given attribute
	 */
	public void addVertexForAttribute(Vertex vertex, String value, String name) {
		// TODO Auto-generated method
	}

	/**
	 * adds an {@link Vertex} for a given attribute with a given value
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param vertex vertex that has this value for the given attribute
	 */
	public void addVertexForAttribute(Vertex vertex, String value, int name) {
		// TODO Auto-generated method
	}

	/**
	 * gets a {@link List} of {@link Vertex} that contains all {@link Vertex} that have the value for given attribut
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return a {@link List} of {@link Vertex} that has the value for given attribute
	 */
	public List<Vertex> getVerticesByAttribute(String name, String value) {
		// TODO Auto-generated method
		return null;
	}

	/**
	 * gets a {@link List} of {@link Vertex} that contains all {@link Vertex} that have the value for given attribut
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return a {@link List} of {@link Vertex} that has the value for given attribute
	 */
	public List<Vertex> getVerticesByAttribute(String name, int value) {
		// TODO Auto-generated method
		return null;
	}

	/**
	 * Deletes all data in this FastGraphAccessor.
	 * After this step all the information needs to be readded to this.
	 * This is necessary when updating the FastGraphAccessor
	 */
	public void reset() {
		// TODO Auto-generated method
	}

}
