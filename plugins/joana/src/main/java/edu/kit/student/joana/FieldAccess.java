package edu.kit.student.joana;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;
import edu.kit.student.util.DoublePoint;

/**
 * This specifies the vertex representation of FieldAccesses in a MethodGraph It
 * contains a {@code FieldAccessGraph}.
 */
public class FieldAccess extends JoanaCompoundVertex {

	private FieldAccessGraph graph;
	
    /**
     * Constructor.
     *
     * @param graph The FieldAccessGraph that will be set in the FieldAccess.
     * @param name The name of this FieldAccess
     * @param label of this FieldAccess
     */
    public FieldAccess(FieldAccessGraph graph, String name, String label) {
        //TODO: Rework hierarchy. FieldAccess can not inherit from JoanaVertex, 
        // because having a kind doesn't make sense.
        super(name, label, VertexKind.FIELDACCESS);
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
        return null;
    }

    @Override
    public DoublePoint getSize() {
        return graph.getSize();
    }

    @Override
    public void setX(double x){
        super.setX(x);
        graph.setX(x);
    }

    @Override
    public void setY(double y){
        super.setY(y);
        graph.setY(y);
    }

}
