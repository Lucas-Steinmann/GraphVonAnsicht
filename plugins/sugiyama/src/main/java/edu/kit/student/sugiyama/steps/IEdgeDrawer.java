package edu.kit.student.sugiyama.steps;

import edu.kit.student.sugiyama.graph.IEdgeDrawerGraph;

/**
 * This interface represents a class that takes a directed graph, as a {@link edu.kit.student.sugiyama.graph.SugiyamaGraph}.
 * It removes dummy vertices and reverses previously reversed edges.
 * Afterwards it assigns every edge points it must run through.
 */
public interface IEdgeDrawer {

	/**
	 * Draws the edges from the graph argument and reverses the edges, which have been reversed earlier,
	 * so they have now the correct direction.
	 * 
	 * @param graph the input graph
	 */
	public void drawEdges(IEdgeDrawerGraph graph);
}
