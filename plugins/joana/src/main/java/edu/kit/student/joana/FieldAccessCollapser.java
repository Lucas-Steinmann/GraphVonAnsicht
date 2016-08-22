package edu.kit.student.joana;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.graphmodel.DirectedOnionPath;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;

/**
 * Generates a compound vertex given the vertices and edges.
 */
public class FieldAccessCollapser {
    // TODO: Remove code duplication and split into two classes
    
    private List<FieldAccess> collapsedFA;
    private Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges;
    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    
    public FieldAccessCollapser(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, 
                                Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges) {
        this.collapsedFA = new LinkedList<>();
        this.onionEdges = onionEdges; 
        this.graph = graph;
    }

    /**
     * Collapses the field access
     * @param fa the FieldAccess
     */
    public void collapseFieldAccess(FieldAccess fa) {

		// Construct collapsed vertex
		graph.addVertex(fa); 
		collapsedFA.add(fa);
		Set<JoanaVertex> vertices = fa.getGraph().getVertexSet();

		for (JoanaEdge edge : graph.getEdgeSet()) {
			boolean containsSource = vertices.contains(edge.getSource());
			boolean containsTarget = vertices.contains(edge.getTarget());

			if (containsSource && containsTarget) {
                graph.removeEdge(edge);
			} else if (containsSource && !containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getID(), fa, edge.getTarget(), edge.getEdgeKind());
                graph.addEdge(newEdge);
                
                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = onionEdges.get(edge);
                    assert(fa.getGraph().getVertexSet().contains(edge.getSource()));
                    onionEdge.addAsSource(fa);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsSource(fa);
                }

			} else if (!containsSource && containsTarget) {

                graph.removeEdge(edge);
                JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getID(), edge.getSource(), fa, edge.getEdgeKind());
                graph.addEdge(newEdge);

                if (onionEdges.keySet().contains(edge)) {
                    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = onionEdges.get(edge);
                    assert(fa.getGraph().getVertexSet().contains(edge.getTarget()));
                    onionEdge.addAsTarget(fa);
                    onionEdges.remove(edge);
                    onionEdges.put(newEdge, onionEdge);
                }  else {
                    onionEdges.put(newEdge, new DirectedOnionPath<>(edge));
                    onionEdges.get(newEdge).addAsTarget(fa);
                }
			}
		}

		graph.removeAllVertices(vertices);
    }

    /**
     * Expand the field access
     * @param fa the FieldAccess
     */
    public void expandFieldAccess(FieldAccess fa) {
        if (!collapsedFA.contains(fa)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not collapsed in this graph.");
	    }

        if (!graph.contains(fa)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not in this graph.");
        }

		Set<JoanaVertex> containedVertices = new HashSet<>(fa.getGraph().getVertexSet());
		graph.addAllVertices(containedVertices);
		graph.addAllEdges(fa.getGraph().getEdgeSet());


		for (JoanaEdge edge : graph.edgesOf(fa)) {
		    DirectedOnionPath<JoanaEdge, JoanaCompoundVertex> onionEdge = this.onionEdges.get(edge);
		    onionEdge.removeNode(fa);
		    JoanaVertex newSource = onionEdge.getSource() == null ? onionEdge.getEdge().getSource() : onionEdge.getSource();
		    JoanaVertex newTarget = onionEdge.getTarget() == null ? onionEdge.getEdge().getTarget() : onionEdge.getTarget();
		    JoanaEdge newEdge = new JoanaEdge(onionEdge.getName(), onionEdge.getLabel(), onionEdge.getID(), newSource, newTarget, onionEdge.getEdge().getEdgeKind());
		  
		    onionEdges.remove(edge);
		    onionEdges.put(newEdge, onionEdge);
		    graph.removeEdge(edge);
		    graph.addEdge(newEdge);
		}
		graph.removeVertex(fa);
		collapsedFA.remove(fa);
    }

    public List<FieldAccess> getFieldAccessVertices() {
        return collapsedFA;
    }
}
