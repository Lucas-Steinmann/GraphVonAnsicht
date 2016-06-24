package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A serialized version of a {@link Edge}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Edge}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedEdge are not synchronized with the origin {@link Edge} attributes.
 */
public class SerializedEdge<V extends Vertex> implements Edge<V> {
	private String name;
	private int id;
	private String label;
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Integer getID() {
		return id;
	}
	
	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Gets all serialized Attributes as a Map from String to String.
	 * This Map gets created when serializing a {@link Edge} and is returned on demand.
	 * This should only be used for exporting Edges since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Edge}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
	 */
	public Map<String, String> getAttributes() {
		return null;
	}

	@Override
	public SerializedEdge serialize(List<Entry<String, String>> attributes) {
		return this;
	}

	@Override
	public List<V> getVertices() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public EdgePath getPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
