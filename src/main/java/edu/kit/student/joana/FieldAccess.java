package edu.kit.student.joana;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;

/**
 * This specifies the vertex representation of FieldAccesses in a MethodGraph It
 * contains a {@code FieldAccessGraph}.
 */
public class FieldAccess extends JoanaVertex implements CompoundVertex {

	/**
	 * Constructor.
	 * 
	 * @param graph The FieldAccessGraph that will be set in the FieldAccess.
	 */
	public FieldAccess(FieldAccessGraph graph) {
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
		// TODO Auto-generated method stub
	}

	@Override
	public FieldAccessGraph getGraph() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JoanaVertex getConnectedVertex(Edge edge) {
		// TODO Auto-generated method stub
		return null;
	}
}
