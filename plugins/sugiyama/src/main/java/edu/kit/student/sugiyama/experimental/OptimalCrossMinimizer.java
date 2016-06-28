package edu.kit.student.sugiyama.experimental;

import edu.kit.student.sugiyama.graph.ICrossMinimizerGraph;
import edu.kit.student.sugiyama.steps.ICrossMinimizer;

/**
 * An implementation for a CrossMinimizer solving the Problem of minimizing cross sections for every 2 Layer
 * optimal by solving the Bipartite Crossing Number Problem.
 * The number of iterations the algorithm goes through all layers can be specified on construction.
 * An optimal solution for the whole graph won't be achieved in general.
 */
public class OptimalCrossMinimizer implements ICrossMinimizer {

	/**
	 * Constructs a new optimal cross minimizer and specifies the number
	 * of iterations it will cycle through the graph from bottom to top
	 * to minimize the cross sections of edges
	 * @param iterations the number of iterations
	 */
	public OptimalCrossMinimizer(int iterations) {}
	/* (non-Javadoc)
	 * @see sugiyama.ICrossMinimizer#minimizeCrossings(sugiyama.ICrossMinimizerGraph)
	 */
	@Override
	public void minimizeCrossings(ICrossMinimizerGraph graph) {
		// TODO Auto-generated method stub

	}

}
