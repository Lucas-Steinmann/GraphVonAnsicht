package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaVertex;

/**
 * A LayeredGraph which additionally defines functions to position vertices in the sugiyama-layout.
 */
public interface IVertexPositionerGraph extends LayeredGraph<SugiyamaVertex, SugiyamaEdge> {

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
	public void setX(SugiyamaVertex vertex, int x);

	
}
