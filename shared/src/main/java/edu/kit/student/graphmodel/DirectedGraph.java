package edu.kit.student.graphmodel;

import java.util.Set;

/**
 * A {@link DirectedGraph} is a specific Graph which contains just
 * {@link DirectedEdge} as edges
 */
public interface DirectedGraph<V extends Vertex, E extends DirectedEdge<V>> extends Graph<V, E> {

	/**
	 * Returns the outdegree of a vertex of the graph.
	 * 
	 * @param vertex
	 *            Vertex whose outdegree will be returned.
	 * @return The number of edges going out of the supplied vertex.
	 */
	public Integer outdegreeOf(V vertex);

	/**
	 * Returns the indegree of a vertex of the graph.
	 * 
	 * @param vertex
	 *            Vertex whose indegree will be returned.
	 * @return The number of edges going into the supplied vertex.
	 */
	public Integer indegreeOf(V vertex);

	/**
	 * Returns a set of all outgoing edges of a vertex.
	 * 
	 * @param vertex
	 *            Vertex whose outgoing edges will be returned.
	 * @return The edges going out of the supplied vertex.
	 */
	public Set<E> outgoingEdgesOf(V vertex);

	/**
	 * Returns a set of all incoming edges of a vertex.
	 * 
	 * @param vertex
	 *            Vertex whose incoming edges will be returned.
	 * @return The edges coming in the supplied vertex.
	 */
	public Set<E> incomingEdgesOf(V vertex);
}
