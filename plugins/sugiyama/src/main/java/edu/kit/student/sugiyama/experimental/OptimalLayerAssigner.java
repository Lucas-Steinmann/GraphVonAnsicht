package edu.kit.student.sugiyama.experimental;

import edu.kit.student.sugiyama.AbsoluteLayerConstraint;
import edu.kit.student.sugiyama.LayerContainsOnlyConstraint;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.ILayerAssignerGraph;
import edu.kit.student.sugiyama.steps.ILayerAssigner;

import java.util.Set;

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

	@Override
	public void addLayerContainsOnlyConstraints(Set<LayerContainsOnlyConstraint> constraints) {

	}

	@Override
    public void addRelativeConstraints(Set<RelativeLayerConstraint> constraints) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void addAbsoluteConstraints(Set<AbsoluteLayerConstraint> constraints) {
        // TODO Auto-generated method stub
        
    }

}
