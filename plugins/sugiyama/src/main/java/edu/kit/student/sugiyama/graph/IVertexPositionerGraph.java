package edu.kit.student.sugiyama.graph;


import java.util.Set;

/**
 * A LayeredGraph which additionally defines functions to position vertices in the sugiyama-layout.
 */
public interface IVertexPositionerGraph extends ISugiyamaStepGraph {

	/**
	 * Sets the y-coordinate of all vertices on layer Y.
	 * 
	 * @param layerN the index of the layer
	 * @param y 	 the y-coordinate
	 */
	public void setLayerY(int layerN, int y);
	
	
	/**
	 * Sets the x-coordinate of the specified vertex
	 * @param vertex the vertex to position
	 * @param x 	 the x-coordinate
	 */
	public void setX(ISugiyamaVertex vertex, double x);


	/**
	 * Returns all SupplementPaths that have been created in CrossMinimizer.
	 * So these are all paths that contain DummyVertices
	 *
	 * @return a set of all SupplementPaths
	 */
	public Set<SugiyamaGraph.SupplementPath> getSupplementPaths();

	
}
