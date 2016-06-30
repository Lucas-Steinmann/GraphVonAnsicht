package edu.kit.student.graphmodel;

/**
 * A interface of a vertex that contains an entire subgraph.
 *
 */
public interface CompoundVertex<V extends Vertex, E extends Edge<V>> extends Vertex {

	/**
	 * Returns the graph contained in the vertex.
	 * 
	 * @return the graph contained in the vertex.
	 */
	public Graph<? extends V, ? extends E> getGraph();
	
	
	/**
	 * Returns the connected vertex contained in the compound vertex for a given edge,
	 * where one end point of the edge has to be this vertex.
	 * @param edge the edge to get the vertex to
	 * @return the connected vertex if the edge is valid
	 */
	public Vertex getConnectedVertex(E edge);
}
