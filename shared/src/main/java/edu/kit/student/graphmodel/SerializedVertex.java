package edu.kit.student.graphmodel;

import edu.kit.student.plugin.Exporter;

import java.util.List;
import java.util.Map;

/**
 * A serialized version of a {@link Vertex}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Vertex}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedVertex are not synchronized with the origin {@link Vertex} attributes.
 */
public class SerializedVertex implements Vertex {
	private final List<String[]> attributes;
	private String name;
	private int id;
	private String label;

	public SerializedVertex(List<String[]> attributes, String name, int id, String label) {
		this.attributes = attributes;
		this.name = name;
		this.id = id;
		this.label = label;
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
	public List<String[]> getAttributes() {
		return this.attributes;
	}

	@Override
	public SerializedVertex serialize() {
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
