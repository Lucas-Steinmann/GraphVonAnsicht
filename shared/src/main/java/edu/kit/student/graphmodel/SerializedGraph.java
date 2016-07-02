package edu.kit.student.graphmodel;

import edu.kit.student.plugin.Exporter;

import java.util.List;
import java.util.Map;

/**
 * A serialized version of a {@link Graph}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Graph}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedGraph are not synchronized with the origin {@link Graph} attributes.
 */
public class SerializedGraph<V extends SerializedVertex, E extends SerializedEdge<V>> {

	private final List<String[]> attributes;

	public SerializedGraph(List<String[]> attributes) {
		this.attributes = attributes;
	}

	/**
	 * Gets all serialized Attributes as a Map from String to String.
	 * This Map gets created when serializing a {@link Graph} and is returned on demand.
	 * This should only be used for exporting Graphs since the attributes are not synchronized with the attributes
	 * of the unserialized {@link Graph}
	 *
	 * @return The Map of serialized Attributes
	 * @see Map
     */
	public List<String[]> getAttributes() {
		return this.attributes;
	}
}
