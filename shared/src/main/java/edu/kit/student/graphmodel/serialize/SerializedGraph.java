package edu.kit.student.graphmodel.serialize;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.plugin.Exporter;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A serialized version of a {@link Graph}.
 * It contains all attributes as a {@link List} of String to String entries which can be used by an {@link Exporter} to
 * export a {@link Graph}. It is designed as an intermediate Step in the export workflow and should not be used for other
 * purposes. Attributes in the {@link List} are not synchronized with attributes outside the {@link List}, and Attributes of
 * SerializedGraph are not synchronized with the origin {@link Graph} attributes.
 */
public class SerializedGraph extends SerializedGraphElement {

	Set<SerializedVertex> vertices;
	Set<SerializedEdge> edges;
	
	public SerializedGraph(Map<String, String> shapeProperties, Map<String, String> metaProperties, Set<SerializedVertex> vertices, Set<SerializedEdge> edges) {
		super(shapeProperties, metaProperties);
		
		this.vertices = vertices;
		this.edges = edges;
	}
	
	public Set<SerializedVertex> getVertices() {
		return vertices;
	}
	
	public Set<SerializedEdge> getEdges() {
		return edges;
	}
}
