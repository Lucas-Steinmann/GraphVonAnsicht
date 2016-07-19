package edu.kit.student.sugiyama.graph;

import edu.kit.student.sugiyama.graph.SugiyamaGraph.SupplementPath;
import java.util.Set;

/**
 * A LayeredGraph which additionally defines functions draw the edges in the sugiyama-layout.
 */
public interface IEdgeDrawerGraph  extends ISugiyamaStepGraph {
	
	/**
	 * Reverses the direction of an directed edge.
	 * 
	 * @param edge the edge to return its direction
	 */
	public void reverseEdge(ISugiyamaEdge edge);
	
	
	/**
	 * Returns all SupplementPaths that have been created in CrossMinimizer.
	 * So these are all paths that contain DummyVertices
	 * 
	 * @return a set of all SupplementPaths 
	 */
	public Set<SupplementPath> getSupplementPaths();

	/**
	 * Returns the set of all with {@code reverseEdge(E edge)} reversed edges.
	 * 
	 * @return the set of all reversed edges.
	 */
	public Set<ISugiyamaEdge> getReversedEdges();
}
