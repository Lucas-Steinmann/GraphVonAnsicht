package edu.kit.student.sugiyama.graph;

import edu.kit.student.sugiyama.graph.SugiyamaGraph.SupplementPath;
import edu.kit.student.util.DoublePoint;
import java.util.List;
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
	 * Sets the edgepath of all edges that have been replaced through a supplement path.
	 * Sets a {@see Point} for every DummyVertex inherited in a supplement path.
	 */
	public void setEdgepaths();
	
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

//	/**
//	 * Returns the set of replaced edges.
//	 * 
//	 * @return the set of replaced edges
//	 */
//	public Set<ISugiyamaEdge> getReplacedEdges();
	
	
	/**
	 * Adds a new edge corner to the specified edge.
	 * The index specifies the position between other edge corners.
	 * Every edge corner is connected with the corners with index +/- 1 of it's index. 
	 * Counting starts at 0 at the endpoint at the source vertex of the edge.
	 * End- and startpoint are also counted as corners
	 * 
	 * @param edge  the edge to add a new corner
	 * @param x 	the x coordinate of the corner
	 * @param y 	the y coordinate of the corner
	 * @param index the index on the edge of the corner
	 */
	public void addEdgeCorner(ISugiyamaEdge edge, int x, int y, int index);
	
	/**
	 * Removes the corner on the specified edge at the index
	 * @param edge  the edge to remove the corner
	 * @param index the index of the corner to remove
	 */
	public void removeEdgeCorner(ISugiyamaEdge edge, int index);
	
	/**
	 * Returns a list of points, which describe the coordinates of the edges
	 * @param edge the edge
	 * @return	   the list of points of the corners on the edge
	 */
	public List<DoublePoint> getEdgeCorners(ISugiyamaEdge edge);

}
