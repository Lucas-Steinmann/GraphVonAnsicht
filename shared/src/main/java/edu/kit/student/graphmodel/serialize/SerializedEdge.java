package edu.kit.student.graphmodel.serialize;

import java.util.List;
import java.util.Map;

import edu.kit.student.graphmodel.Edge;

/**
 * A serialized version of a {@link Edge}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Edge}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedEdge are not synchronized with the origin {@link Edge} attributes.
 */
public class SerializedEdge extends SerializedGraphElement {

	public SerializedEdge(Map<String, String> shapeProperties, Map<String, String> metaProperties) {
		super(shapeProperties, metaProperties);
	}
}
