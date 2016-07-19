package edu.kit.student.graphmodel;

import edu.kit.student.util.DoublePoint;
import java.util.LinkedList;
import java.util.List;

/**
 * An orthogonal edge path used as standard graphical edge representation.
 */
public class OrthogonalEdgePath extends EdgePath {
	private List<DoublePoint> nodes;

	public OrthogonalEdgePath() {
		nodes = new LinkedList<>();
	}

	/* (non-Javadoc)
         * @see graphmodel.EdgePath#getSegments()
         */
	@Override
	public int getSegmentsCount() {
		return nodes.size();
	}

	/* (non-Javadoc)
	 * @see graphmodel.EdgePath#getNodes()
	 */
	@Override
	public List<DoublePoint> getNodes() {
		return this.nodes;
	}

	@Override
	public void addPoint(DoublePoint newPoint) {
		this.nodes.add(newPoint);
	}

	@Override
	public void clear() {
		nodes.clear();
	}
}
