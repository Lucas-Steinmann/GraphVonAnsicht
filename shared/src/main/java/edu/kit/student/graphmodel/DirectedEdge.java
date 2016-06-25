package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A {@link DirectedEdge} is an edge that has one source and one target vertex.
 * The direction of the edge is specified.
 */
public class DirectedEdge<V extends Vertex> implements Edge<V> {

	private List<V> vertices;
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private GAnsProperty<String> label;

	/**
	 * Returns the source vertex of this directed edge.
	 * 
	 * @return The vertex the edge is coming from.
	 */
	public V getSource() {
		return null;
	}

	/**
	 * Returns the target vertex of this edge.
	 * 
	 * @return The vertex the edge is pointing at/going to.
	 */
	public V getTarget() {
		return null;
	}

	@Override
	public List<V> getVertices() {
		return null;
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id.getValue();
	}

	@Override
	public String getLabel() {
		return label.getValue();
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {

	}

	@Override
	public SerializedEdge<V> serialize(List<Entry<String, String>> attributes) {
		return null;
	}

	@Override
	public OrthogonalEdgePath getPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
