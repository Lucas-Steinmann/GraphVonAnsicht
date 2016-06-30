package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.Map;

public class CollapsedVertex<V extends Vertex, E extends Edge<V>> extends DefaultVertex implements CompoundVertex<V, E> {
	
	private Graph<V, E> graph;
	private Map<E,V> modifiedEdgeMap;

	public CollapsedVertex(String name, String label, Integer id) {
		super(name, label, id);
		modifiedEdgeMap = new HashMap<E,V>();
	}
	
	public void setGraph(Graph<V, E> graph) {
		this.graph = graph;
	}

	@Override
	public Graph<V, E> getGraph() {
		return graph;
	}

	@Override
	public Vertex getConnectedVertex(E edge) {
		if(graph.getEdgeSet().contains(edge)) {
			for(Vertex v : edge.getVertices()) {
				if(graph.getVertexSet().contains(v)) return v;
			}
		}
		return null;
	}
	
	public void addModifiedEdge(E edge, V vertex) {
		modifiedEdgeMap.put(edge, vertex);
	}
	
	public V getVertexForEdge(E edge) {
		return modifiedEdgeMap.get(edge);
	}
}
