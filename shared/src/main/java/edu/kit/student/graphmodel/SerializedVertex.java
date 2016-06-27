package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.student.plugin.Exporter;

/**
 * A serialized version of a {@link Vertex}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Vertex}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedVertex are not synchronized with the origin {@link Vertex} attributes.
 */
public class SerializedVertex implements Vertex {
	private String name;
	private int id;
	private String label;
	private final List<Entry<String, String>> attributes;

	/**
	 * creates
	 * @param attributes
     */
	public SerializedVertex(List<Entry<String, String>> attributes) {
		this.attributes = attributes;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Integer getID() {
		return this.id;
	}
	
	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {

	}

	/**
	 * Gets all serialized Attributes as a Map from String to String.
	 * This Map gets created when serializing a {@link Vertex} and is returned on demand.
	 * This should only be used for exporting Vertices since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Vertex}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
	 */
	public Map<String, String> getAttributes() {
		return new HashMap<String, String>();
	}

	@Override
	public SerializedVertex serialize(List<Entry<String, String>> attributes) {
		return this;
	}

	@Override
	public int getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return 0;
	}

    @Override
    public void setX(int x) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setY(int y) {
        // TODO Auto-generated method stub
        
    }
}
