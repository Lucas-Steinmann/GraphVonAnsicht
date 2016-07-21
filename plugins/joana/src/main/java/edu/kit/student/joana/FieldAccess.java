package edu.kit.student.joana;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;

/**
 * This specifies the vertex representation of FieldAccesses in a MethodGraph It
 * contains a {@code FieldAccessGraph}.
 */
public class FieldAccess extends JoanaVertex 
    implements CompoundVertex {

	private FieldAccessGraph graph;
	
    /**
     * Constructor.
     * 
     * @param graph The FieldAccessGraph that will be set in the FieldAccess.
     */
    public FieldAccess(FieldAccessGraph graph, String name, String label) {
        //TODO: Rework hierarchy. FieldAccess can not inherit from JoanaVertex, 
        // because having a kind doesn't make sense.
        super(name, label, VertexKind.EXPR);
        this.graph = graph;
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
        // TODO Auto-generated method stub
    }

    @Override
    public FieldAccessGraph getGraph() {
        return this.graph;
    }

    @Override
    public JoanaVertex getConnectedVertex(Edge edge) {
        // TODO Auto-generated method stub
        return null;
    }
}
