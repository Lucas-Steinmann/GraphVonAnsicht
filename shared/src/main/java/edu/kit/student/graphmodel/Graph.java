package edu.kit.student.graphmodel;

import java.util.Set;


/**
 * This graph interface specifies a graph. A graph contains edges and vertices.
 */
public interface Graph {


	/**
	 * Returns all vertices of the graph.
	 * 
	 * @return A set of all vertices of the graph.
	 */
	public Set<? extends Vertex> getVertexSet();

	/**
	 * Returns all edges of the graph.
	 * 
	 * @return A set of all edges of the graph.
	 */
	public Set<? extends Edge> getEdgeSet();

	/**
	 * Returns a list of all edges of a vertex.
	 * 
	 * @param vertex the vertex which edges will be returned.
	 * @return All edges which are connected with the supplied vertex.
	 */
	public Set<? extends Edge> edgesOf(Vertex vertex);

	/**
	 * Returns the FastGraphAccessor of this Graph.
	 *
	 * @return the FastGraphAccessor of this Graph
     */
	public FastGraphAccessor getFastGraphAccessor();

	/**
	 * Adds the graph to a {@link FastGraphAccessor}.
	 * 
	 * @param fga the {@link FastGraphAccessor} to whom this graph will be added.
	 */
	public void addToFastGraphAccessor(FastGraphAccessor fga);
}
