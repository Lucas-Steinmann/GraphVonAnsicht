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

/**
 * Generates a compound vertex given the vertices and edges.
 */
public class FieldAccessCollapser {
    // TODO: Remove code duplication and split into two classes
    
    public List<JoanaCollapsedVertex> collapsedVertices;
    public List<FieldAccess> collapsedFA;
    public Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges;
    public DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    
    public FieldAccessCollapser(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph) {
        this.collapsedVertices = new LinkedList<>();
        this.collapsedFA = new LinkedList<>();
        this.onionEdges = new HashMap<>();
        this.graph = graph;
    }

    /**
     * Collapses the field access
     * @param graph the graph containing the FieldAccess
     * @param fa the FieldAccess
     */
    public void collapseFieldAccess(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, FieldAccess fa) {

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
     * @param graph the graph containing the FieldAccess
     * @param fa the FieldAccess
     */
    public void expandFieldAccess(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, FieldAccess fa) {
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

    /**
     * Expand the collapsed vertex 
     * @param graph the graph containing the vertex
     * @param vertex the vertex to expand
     * @return 
     */
    public Set<JoanaVertex> expand(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, CompoundVertex vertex) {
        
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
     * @param graph the graph containing the subgraph
     * @param vertices the subgraph
     * @return 
     */
    public JoanaCollapsedVertex collapse(DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, Set<JoanaVertex> vertices) {

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
}
