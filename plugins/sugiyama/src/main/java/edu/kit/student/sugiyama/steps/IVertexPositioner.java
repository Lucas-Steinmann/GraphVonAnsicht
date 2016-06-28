package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.IVertexPositionerGraph;

/**
 * This interface represents a class that takes a directed graph and position 
 * its vertices in order to look more clearly. (e.g. position vertices in a row or column)
 * This step has to access the GraphicEngine to get the size of each vertex.
 */
public interface IVertexPositioner {

	/**
	 * Sets absolute coordinates for every vertex in the graph.
	 * Before this step the vertices are only positioned relatively to each other (layer and order in one layer)
	 * 
	 * @param graph input graph
	 */
	public void positionVertices(IVertexPositionerGraph graph);
}
