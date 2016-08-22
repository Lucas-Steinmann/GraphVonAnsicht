package edu.kit.student.joana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.graphmodel.DirectedOnionPath;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;

public class JoanaCollapser {

    private List<JoanaCollapsedVertex> collapsedVertices;
    private Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges;
    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;

    public JoanaCollapser(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph,
                          Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges) {
        this.collapsedVertices = new LinkedList<>();
        this.onionEdges = onionEdges;
        this.graph = graph;

    }
    /**
     * Expand the collapsed vertex.
     * @param vertex the vertex to expand
     * @return the set of vertices the compound vertex has been expand to
     */
    public Set<JoanaVertex> expand(CompoundVertex vertex) {
        
	    if (!collapsedVertices.contains(vertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not collapsed in this graph.");
	    }
	    JoanaCollapsedVertex jvertex = null;
        for (JoanaCollapsedVertex v : collapsedVertices) {
            if (vertex.getID().equals(v.getID())) {
                jvertex = v;
            }
        }

        if (!graph.contains(jvertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not in this graph.");
        }

		Set<JoanaVertex> containedVertices = new HashSet<>(jvertex.getGraph().getVertexSet());
		graph.addAllVertices(containedVertices);
		graph.addAllEdges(jvertex.getGraph().getEdgeSet());


		for (JoanaEdge edge : graph.edgesOf(jvertex)) {
		    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = this.onionEdges.get(edge);
		    onionEdge.removeNode(jvertex);
		    JoanaVertex newSource = onionEdge.getSource() == null ? onionEdge.getEdge().getSource() : onionEdge.getSource();
		    JoanaVertex newTarget = onionEdge.getTarget() == null ? onionEdge.getEdge().getTarget() : onionEdge.getTarget();
		    JoanaEdge newEdge = new JoanaEdge(onionEdge.getName(), onionEdge.getLabel(), onionEdge.getID(), newSource, newTarget, onionEdge.getEdge().getEdgeKind());
		  
		    onionEdges.remove(edge);
		    onionEdges.put(newEdge, onionEdge);
		    graph.removeEdge(edge);
		    graph.addEdge(newEdge);
		}
		graph.removeVertex(jvertex);
		collapsedVertices.remove(jvertex);
		
		return containedVertices;
    }

    /**
     * Collapses a subgraph
     * @param vertices the subgraph
     * @return the collapsed vertex
     */
    public JoanaCollapsedVertex collapse(Set<JoanaVertex> vertices) {

		// Construct collapsed vertex
		DefaultDirectedGraph<JoanaVertex, JoanaEdge> collapsedGraph = new DefaultDirectedGraph<>(vertices, new HashSet<JoanaEdge>());
		JoanaCollapsedVertex collapsed = new JoanaCollapsedVertex("Collapsed", "Collapsed (" + collapsedGraph.getVertexSet().size() + ")",
		        collapsedGraph, new HashMap<>());
		graph.addVertex(collapsed); 
		collapsedVertices.add(collapsed);

		for (JoanaEdge edge : graph.getEdgeSet()) {
			boolean containsSource = vertices.contains(edge.getSource());
			boolean containsTarget = vertices.contains(edge.getTarget());

			if (containsSource && containsTarget) {
                graph.removeEdge(edge);
				collapsedGraph.addEdge(edge);
			} else if (containsSource && !containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getID(), collapsed, edge.getTarget(), edge.getEdgeKind());
                graph.addEdge(newEdge);
                
                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = onionEdges.get(edge);
                    assert(collapsed.getGraph().getVertexSet().contains(edge.getSource()));
                    onionEdge.addAsSource(collapsed);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsSource(collapsed);
                }

			} else if (!containsSource && containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getID(), edge.getSource(), collapsed, edge.getEdgeKind());
                graph.addEdge(newEdge);

                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = onionEdges.get(edge);
                    assert(collapsed.getGraph().getVertexSet().contains(edge.getTarget()));
                    onionEdge.addAsTarget(collapsed);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsTarget(collapsed);
                }
			} 
		}

		graph.removeAllVertices(vertices);
		return collapsed;
    }

    /**
     * Returns the currently collapsed vertices.
     * @return the currently collapsed vertices
     */
    public List<JoanaCollapsedVertex> getCollapsedVertices() {
        return collapsedVertices;
    }
}
