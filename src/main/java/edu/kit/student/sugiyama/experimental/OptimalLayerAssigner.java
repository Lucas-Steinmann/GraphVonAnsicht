package edu.kit.student.sugiyama.experimental;

import java.util.Set;

import edu.kit.student.sugiyama.ILayerAssigner;
import edu.kit.student.sugiyama.ILayerAssignerGraph;
import edu.kit.student.sugiyama.RelativeLayerConstraint;

/**
 * Implements layer assigning for directed acyclic graphs to achieve minimal height with fixed width, set by max width.
 * When no maximal width is set, there will be chosen one by the algorithm to meet a the max height constraint.
 */
public class OptimalLayerAssigner implements ILayerAssigner {

	/* (non-Javadoc)
	 * @see sugiyama.ILayerAssigner#assignLayers(sugiyama.ILayerAssignerGraph)
	 */
	@Override
	public void assignLayers(ILayerAssignerGraph graph) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sugiyama.ILayerAssigner#addConstraints(java.util.Set)
	 */
	@Override
	public void addConstraints(Set<RelativeLayerConstraint> constraints) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sugiyama.ILayerAssigner#setMaxHeight(int)
	 */
	@Override
	public void setMaxHeight(int height) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see sugiyama.ILayerAssigner#setMaxWidth(int)
	 */
	@Override
	public void setMaxWidth(int width) {
		// TODO Auto-generated method stub

	}

}
