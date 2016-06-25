package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.Exporter;

/**
 * A serialized version of a {@link Graph}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Graph}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedGraph are not synchronized with the origin {@link Graph} attributes.
 */
public class SerializedGraph<V extends SerializedVertex, E extends SerializedEdge<V>>
		implements Graph<V, E> {
	
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private FastGraphAccessor fga;

	/**
	 * Gets all serialized Attributes as a Map from String to String.
	 * This Map gets created when serializing a {@link Graph} and is returned on demand.
	 * This should only be used for exporting Graphs since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Graph}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
     */
	public Map<String, String> getAttributes() {
		return null;
	}

	@Override
	public Set<V> getVertexSet() { 
		// TODO Auto-generated method
		return null;
	 }

	@Override
	public Set<E> getEdgeSet() { 
		// TODO Auto-generated method
		return null;
	 }

	@Override
	public Set<E> edgesOf(V vertex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FastGraphAccessor getFastGraphAccessor() {
		return null;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id.getValue();
	}
}
