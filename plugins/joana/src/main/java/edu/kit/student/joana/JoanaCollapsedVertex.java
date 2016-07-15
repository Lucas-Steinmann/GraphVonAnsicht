package edu.kit.student.joana;

import java.util.List;
import java.util.Map;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.objectproperty.GAnsProperty;

public class JoanaCollapsedVertex extends JoanaVertex implements CollapsedVertex {

	private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
	
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
		super(name, label, Kind.SUMMARY);

		this.graph = graph;
		
		this.vertexCount = new GAnsProperty<Integer>("Vertices contained", this.graph.getVertexSet().size());
		this.edgeCount = new GAnsProperty<Integer>("Edges contained", this.graph.getEdgeSet().size());
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
		List<GAnsProperty<?>> properties = super.getProperties();
		for(GAnsProperty<?> property : properties) {
			if(!(property.getName().compareTo("Name") == 0) || !(property.getName().compareTo("Label") == 0)) {
				properties.remove(property);
			}
		}
		
		properties.add(this.vertexCount);
		properties.add(this.edgeCount);
		return properties;
	}
}
