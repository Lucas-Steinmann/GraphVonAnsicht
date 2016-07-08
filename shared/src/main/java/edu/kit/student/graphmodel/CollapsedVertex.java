package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.Map;

public class CollapsedVertex extends DefaultVertex implements CompoundVertex {
	
	private Graph graph;
	private Map<Edge,Vertex> modifiedEdgeMap;

	public CollapsedVertex(String name, String label, Graph graph, 
	        Map<? extends Vertex, ? extends Edge> collapsedVertexToCutEdge) {
		super(name, label);
		modifiedEdgeMap = new HashMap<Edge,Vertex>();
		this.graph = graph;
	}

	@Override
	public Graph getGraph() {
		return graph;
	}

	@Override
	public Vertex getConnectedVertex(Edge edge) {
		if(graph.getEdgeSet().contains(edge)) {
			for(Vertex v : edge.getVertices()) {
				if(graph.getVertexSet().contains(v)) return v;
			}
		}
		return null;
	}
	
	@Deprecated
	public void addModifiedEdge(Edge edge, Vertex vertex) {
		modifiedEdgeMap.put(edge, vertex);
	}
	
	public Vertex getVertexForEdge(Edge edge) {
		return modifiedEdgeMap.get(edge);
	}
}
