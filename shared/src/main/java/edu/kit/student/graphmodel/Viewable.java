package edu.kit.student.graphmodel;

import java.util.Set;

/**
 * Adds methods for manipulation and observation of graphs.
 * This interface differentiates between domain specific graphs, which can be viewed (children of ViewableGraph),
 * and utility graphs like {@link SerializedGraph} and {@link SugiyamaGraph}
 */
public interface Viewable {

	/**
	 * Collapses a set of vertices in one compound vertex.
	 * The collapsed vertices can be expanded back into 
	 * their previous state with {@code expand(CompoundVertex)}.
	 * @param subset the subset to collapse
	 * @return		 the resulting collapsed vertex
	 */
	public CollapsedVertex collapse(Set<Vertex> subset);
	
	/**
	 * Expands a collapsed vertex into its substituted set of vertices
	 * The vertices will be added back to the set of vertices of this graph.
	 * The compound vertex will be removed from the set of vertices.
	 * All to the compound vertex incident edges, will be resolved back into an
	 * edge between the vertices it connected before the collapse.
	 * @param vertex the collapsed vertex to expand
	 * @return 		 the set of vertices which was substituted by the collapsed vertex
	 */
	public Set<? extends Vertex> expand(CollapsedVertex vertex);
	
	/**
	 * Returns true if the specified vertex is a compound vertex
	 * @param vertex the vertex to check
	 * @return 		 true if the vertex is a compound, false otherwise
	 */
	public boolean isCollapsed(Vertex vertex);
	
}
