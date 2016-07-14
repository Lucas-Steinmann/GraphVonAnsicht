package edu.kit.student.joana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;

public class JoanaCollapsedVertex extends JoanaVertex implements CollapsedVertex {

	private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
	private Map<JoanaEdge, JoanaEdge> modifiedEdgeMap;

	/**
	 * Constructs a new collapsed vertex with a name, label and graph.
	 * @param name the name of the collapsed vertex
	 * @param label the label of the collapsed vertex
	 * @param graph the contained graph
	 * @param newEdgeToOldEdge a map from newly added edges between this collapsed vertex 
	 * and the surrounding graph with an old edge on the cut between graph and the surrounding graph.
	 */
	public JoanaCollapsedVertex(String name, String label,  DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph, 
	        Map<JoanaEdge, JoanaEdge> newEdgeToOldEdge) {
        super(name, label, Kind.SUMMARY);

        this.modifiedEdgeMap = new HashMap<>();
        for (JoanaEdge newEdge : newEdgeToOldEdge.keySet()) {
            this.addModifiedEdge(newEdge, newEdgeToOldEdge.get(newEdge));
        }
		this.graph = graph;
	}

	@Override
	public DefaultDirectedGraph<JoanaVertex, JoanaEdge> getGraph() {
		return graph;
	}

	@Override
	public JoanaVertex getConnectedVertex(Edge edge) {
		if(graph.getEdgeSet().contains(edge)) {
			for(Vertex v : edge.getVertices()) {
				if(graph.getVertexSet().contains(v)) {
				    return graph.getVertexById(v.getID());
				}
			}
		}
		return null;
	}

	/**
	 * Adds an edge which is connected to this collapsed vertex and represents 
	 * a formerly present connection between a vertex in this collapsed vertex and one not being contained.
	 * This can be used to save old connections, which should be restored after expanding this vertex.
	 * @param newEdge the new edge
	 * @param oldEdge the replaced edge
	 */
	public void addModifiedEdge(JoanaEdge newEdge, JoanaEdge oldEdge) {
	    if (!newEdge.getVertices().contains(this)) {
	        throw new IllegalArgumentException("The new edge must connect to the collapsed vertex.");
	    }
//        System.out.println("Adding to storage of " + this.getID() + ": " 
//		        + newEdge.getSource().getID() + "->" +  newEdge.getTarget().getID()+ ", " +
//		               oldEdge.getSource().getID() + "->" + oldEdge.getTarget().getID() );
	    modifiedEdgeMap.put(newEdge, oldEdge);
	}

	/**
	 * Removes the connection of an edge connected to this collapsed vertex with the stored edge.
	 * @param edge the edge being removed.
	 */
	public void removeModifiedEdge(JoanaEdge edge) {
	    if (!edge.getVertices().contains(this)) {
	        throw new IllegalArgumentException("The edge must connect to the collapsed vertex.");
	    }
//        System.out.println("Removing from storage of " + this.getID() + ": " 
//		        + edge.getSource().getID() + "->" +  edge.getTarget().getID()+ ", " +
//		               modifiedEdgeMap.get(edge).getSource().getID() + "->" + modifiedEdgeMap.get(edge).getTarget().getID() );
	    modifiedEdgeMap.remove(edge);
	}

	public JoanaEdge getModifiedEdge(Edge edge) {
		return modifiedEdgeMap.get(edge);
	}

}
