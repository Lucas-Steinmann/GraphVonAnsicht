package edu.kit.student.graphmodel;

import java.util.Set;

public class CollapsedVertex<V extends Vertex, E extends Edge<V>> extends DefaultVertex implements CompoundVertex<V, E> {
	
	private Graph<V, E> graph;
	private Set<E> removedIncomingEdges;

	public CollapsedVertex(String name, String label, Integer id) {
		super(name, label, id);
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
	
	public void addRemovedEdge(E edge) {
		removedIncomingEdges.add(edge);
	}
	
	public Set<E> getRemovedEdges() {
		return removedIncomingEdges;
	}
}
