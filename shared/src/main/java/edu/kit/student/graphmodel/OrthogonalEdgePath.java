package edu.kit.student.graphmodel;

import edu.kit.student.util.Point;

import java.util.LinkedList;
import java.util.List;

/**
 * An orthogonal edge path used as standard graphical edge representation.
 */
public class OrthogonalEdgePath extends EdgePath {
	private List<Point> nodes;

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
	public List<Point> getNodes() {
		return this.nodes;
	}

}
