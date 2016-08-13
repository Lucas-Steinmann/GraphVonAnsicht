package edu.kit.student.joana;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;
import javafx.util.Pair;

/**
 * This specifies the vertex representation of FieldAccesses in a MethodGraph It
 * contains a {@code FieldAccessGraph}.
 */
public class FieldAccess extends JoanaCompoundVertex {

	private FieldAccessGraph graph;
	public static double padding = 10;
	
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
        return null;
    }

    @Override
    public Pair<Double, Double> getSize() {
        if (graph.getVertexSet().isEmpty()) {
            return super.getSize();
        }
        Double maxX = 0.0;
        Double maxY = 0.0;
        for (JoanaVertex vertex : this.graph.getVertexSet()) {
            maxX = vertex.getX() + vertex.getSize().getKey() < maxX ? maxX : vertex.getX() + vertex.getSize().getKey();
            maxY = vertex.getY() + vertex.getSize().getValue() < maxY ? maxY : vertex.getY() + vertex.getSize().getValue();
        }
        return new Pair<Double, Double>(maxX + padding, maxY + padding);
    }
    
}
