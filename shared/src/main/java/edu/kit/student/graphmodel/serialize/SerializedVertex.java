package edu.kit.student.graphmodel.serialize;

import edu.kit.student.graphmodel.Vertex;
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
public class SerializedVertex extends SerializedGraphElement {

	public SerializedVertex(Map<String, String> shapeProperties, Map<String, String> metaProperties) {
		super(shapeProperties, metaProperties);
	}
}
