package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.Map;

public class CollapsedVertex<G extends Graph> extends DefaultVertex implements CompoundVertex {
	
	private G graph;
	private Map<Edge,Vertex> modifiedEdgeMap;

	public CollapsedVertex(String name, String label) {
		super(name, label);
		modifiedEdgeMap = new HashMap<Edge,Vertex>();
	}
	
	public void setGraph(G graph) {
		this.graph = graph;
	}

	@Override
	public G getGraph() {
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
	
	public void addModifiedEdge(Edge edge, Vertex vertex) {
		modifiedEdgeMap.put(edge, vertex);
	}
	
	public Vertex getVertexForEdge(Edge edge) {
		return modifiedEdgeMap.get(edge);
	}
}
