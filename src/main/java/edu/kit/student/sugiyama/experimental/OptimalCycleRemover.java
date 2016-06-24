package edu.kit.student.sugiyama.experimental;

import java.util.Set;

import edu.kit.student.sugiyama.ICycleRemover;
import edu.kit.student.sugiyama.ICycleRemoverGraph;
import edu.kit.student.sugiyama.SugiyamaGraph.SugiyamaEdge;

/**
 * An algorithm for transforming a given graph into an maximal DAG (Directed Acyclic Graph).
 * This means it will remove a set of minimal size from the original graph.
 *
 */
public class OptimalCycleRemover implements ICycleRemover {

	/* (non-Javadoc)
	 * @see sugiyama.ICycleRemover#removeCycles(sugiyama.ICycleRemoverGraph)
	 */
	@Override
	public Set<SugiyamaEdge> removeCycles(ICycleRemoverGraph graph) {
		// TODO Auto-generated method stub
		return null;
	}

}
