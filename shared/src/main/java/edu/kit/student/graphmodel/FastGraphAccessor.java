package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This class provides a fast lookup of {@link Vertex} and {@link Edge} for a given Attribute valueId pair without traversing a {@link Graph}.
 * FastGraphAccessor is a helper class for looking up all {@link Vertex} and {@link Edge} that have a specific valueId
 * for a specific attribute. To achieve this all elements of a Graph need to add their attributes and values to a
 * FastGraphAccessor. These values are not linked to the origin values so the fastGraphAccessor needs to be updated
 * after changes when needed for following steps. This should be done by reverting and adding the values again.
 */
public class FastGraphAccessor {
	private Map<String, Map<String, List<Edge>>> attributeValueEdgeData;
	private Map<String, Map<String, List<Vertex>>> attributeValueVertexData;

	public FastGraphAccessor() {
		this.attributeValueEdgeData = new HashMap<>();
		this.attributeValueVertexData = new HashMap<>();
	}

	/**
	 * Adds an {@link Edge} for a given attribute with a given valueId.
	 * @param edge edge that has this valueId for the given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 */
	public void addEdgeForAttribute(Edge edge, String name, String value) {
		if (!this.attributeValueEdgeData.containsKey(name)) {
			this.attributeValueEdgeData.put(name, new HashMap<>());
		}

		if (!this.attributeValueEdgeData.get(name).containsKey(value)) {
			this.attributeValueEdgeData.get(name).put(value, new LinkedList<Edge>());
		}

		this.attributeValueEdgeData.get(name).get(value).add(edge);
	}

	/**
	 * Adds an {@link Edge} for a given attribute with a given valueId.
	 * @param edge edge that has this valueId for the given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 */
	public void addEdgeForAttribute(Edge edge, String name, int value) {
		//TODO should concider different maps
		this.addEdgeForAttribute(edge, name, Integer.toString(value));
	}

	/**
	 * gets a List of {@link Edge} that contains all {@link Edge} that have the valueId for given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 * @return a {@link List} of {@link Edge} that has the valueId for given attribute.
	 */
	public List<Edge> getEdgesByAttribute(String name, String value) {
		if (!this.attributeValueEdgeData.containsKey(name)) {
			return null;
		}

		return this.attributeValueEdgeData.get(name).get(value);
	}

	/**
	 * gets a {@link List} of {@link Edge} that contains all {@link Edge} that have the valueId for given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 * @return a {@link List} of {@link Edge} that has the valueId for given attribute
	 */
	public List<Edge> getEdgesByAttribute(String name, int value) {
		return this.getEdgesByAttribute(name, Integer.toString(value));
	}

	/**
	 * adds an {@link Vertex} for a given attribute with a given valueId
	 * @param vertex vertex that has this valueId for the given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 */
	public void addVertexForAttribute(Vertex vertex, String value, String name) {
		if (!this.attributeValueVertexData.containsKey(name)) {
			this.attributeValueVertexData.put(name, new HashMap<>());
		}

		if (!this.attributeValueVertexData.get(name).containsKey(value)) {
			this.attributeValueVertexData.get(name).put(value, new LinkedList<Vertex>());
		}

		this.attributeValueVertexData.get(name).get(value).add(vertex);
	}

	/**
	 * adds an {@link Vertex} for a given attribute with a given valueId
	 * @param vertex vertex that has this valueId for the given attribute
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 */
	public void addVertexForAttribute(Vertex vertex, String name, int value) {
		this.addVertexForAttribute(vertex, name, Integer.toString(value));
	}

	/**
	 * gets a {@link List} of {@link Vertex} that contains all {@link Vertex} that have the valueId for given attribut
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 * @return a {@link List} of {@link Vertex} that has the valueId for given attribute
	 */
	public List<Vertex> getVerticesByAttribute(String name, String value) {
		if (!this.attributeValueVertexData.containsKey(name)) {
			return null;
		}

		return this.attributeValueVertexData.get(name).get(value);
	}

	/**
	 * gets a {@link List} of {@link Vertex} that contains all {@link Vertex} that have the valueId for given attribut
	 * @param name name of the attribute
	 * @param value valueId of the attribute
	 * @return a {@link List} of {@link Vertex} that has the valueId for given attribute
	 */
	public List<Vertex> getVerticesByAttribute(String name, int value) {
		return this.getVerticesByAttribute(name, Integer.toString(value));
	}

	/**
	 * Deletes all data in this FastGraphAccessor.
	 * After this step all the information needs to be readded to this.
	 * This is necessary when updating the FastGraphAccessor
	 */
	public void reset() {
		this.attributeValueEdgeData = new HashMap<>();
		this.attributeValueVertexData = new HashMap<>();
	}

}
