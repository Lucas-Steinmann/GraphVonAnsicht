package edu.kit.student.sugiyama.steps;

import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.graph.ICrossMinimizerGraph;

/**
 * This interface represents a class that takes a Sugiyama Graph and rearranges its vertices on each layer to minimize
 * the amount of edge crossings.
 */ 
public interface ICrossMinimizer {

	
	/**
	 * Rearranges vertices in the graph argument in order to remove the amount of crosses of their edges.
	 * 
	 * @param graph input graph
	 */
	public void minimizeCrossings(ICrossMinimizerGraph graph);

	public Settings getSettings();
}
