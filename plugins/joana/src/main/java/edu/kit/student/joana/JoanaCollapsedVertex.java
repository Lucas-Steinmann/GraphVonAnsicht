package edu.kit.student.joana;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.LanguageManager;

public class JoanaCollapsedVertex extends JoanaCompoundVertex {

	private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
	
	private GAnsProperty<String> label;
	private GAnsProperty<VertexKind> kind;
	private GAnsProperty<Integer> vertexCount;
	private GAnsProperty<Integer> edgeCount;

	/**
	 * Constructs a new collapsed vertex with a name, label and graph.
	 * 
	 * @param name
	 *            the name of the collapsed vertex
	 * @param label
	 *            the label of the collapsed vertex
	 * @param graph
	 *            the contained graph
	 * @param newEdgeToOldEdge
	 *            a map from newly added edges between this collapsed vertex and
	 *            the surrounding graph with an old edge on the cut between
	 *            graph and the surrounding graph.
	 */
	public JoanaCollapsedVertex(String name, String label, DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph,
			Map<JoanaEdge, JoanaEdge> newEdgeToOldEdge) {
		super(name, label, VertexKind.SUMMARY);

		this.graph = graph;
		
		this.label = new GAnsProperty<String>(LanguageManager.getInstance().get("coll_label"), label);
		this.kind = new GAnsProperty<VertexKind>(LanguageManager.getInstance().get("coll_type"), VertexKind.SUMMARY);
		this.vertexCount = new GAnsProperty<Integer>(LanguageManager.getInstance().get("coll_vertices"), this.graph.getVertexSet().size());
		this.edgeCount = new GAnsProperty<Integer>(LanguageManager.getInstance().get("coll_edges"), this.graph.getEdgeSet().size());
	}

	@Override
	public DefaultDirectedGraph<JoanaVertex, JoanaEdge> getGraph() {
		return graph;
	}

	@Override
	public JoanaVertex getConnectedVertex(Edge edge) {
		if (graph.getEdgeSet().contains(edge)) {
			for (Vertex v : edge.getVertices()) {
				if (graph.getVertexSet().contains(v)) {
					return graph.getVertexById(v.getID());
				}
			}
		}
		return null;
	}
	
	@Override
	public List<GAnsProperty<?>> getProperties() {
		List<GAnsProperty<?>> properties = new LinkedList<GAnsProperty<?>>();
		properties.add(this.label);
		properties.add(this.kind);
		properties.add(this.vertexCount);
		properties.add(this.edgeCount);
		return properties;
	}
}
