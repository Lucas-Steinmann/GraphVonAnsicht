package edu.kit.student.graphmodel.directed;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

/**
 * A {@link DirectedEdge} is an edge that has one source and one target vertex.
 * The direction of the edge is specified.
 */
public interface DirectedEdge extends Edge {
	
	/**
	 * Returns the source vertex of this directed edge.
	 * 
	 * @return The vertex the edge is coming from.
	 */
	public Vertex getSource();

	/**
	 * Returns the target vertex of this edge.
	 * 
	 * @return The vertex the edge is pointing at/going to.
	 */
	public Vertex getTarget();



}
