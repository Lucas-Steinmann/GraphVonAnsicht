package edu.kit.student.joana;

import java.util.Set;

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
    public DoublePoint getSize() {
        if (graph.getVertexSet().isEmpty()) {
            return super.getSize();
        }
        Set<JoanaVertex> fagVertices = this.graph.getVertexSet();
        Set<JoanaEdge> fagEdges = this.graph.getEdgeSet();
        
        double minX, minY, maxX, maxY;
		minX = fagVertices.stream().mapToDouble(vertex->vertex.getX()).min().getAsDouble();
		maxX = fagVertices.stream().mapToDouble(vertex->vertex.getX() + vertex.getSize().x).max().getAsDouble();
		minY = fagVertices.stream().mapToDouble(vertex->vertex.getY()).min().getAsDouble();
		maxY = fagVertices.stream().mapToDouble(vertex->vertex.getY() + vertex.getSize().y).max().getAsDouble();
		for(JoanaEdge e : fagEdges){	//look if there are some edges more right or left than a vertex.
			minX = Math.min(e.getPath().getNodes().stream().mapToDouble(point->(point.x)).min().getAsDouble(), minX);
			maxX = Math.max(e.getPath().getNodes().stream().mapToDouble(point->(point.x)).max().getAsDouble(), maxX);
			minY = Math.min(e.getPath().getNodes().stream().mapToDouble(point->(point.y)).min().getAsDouble(), minY);
			maxY = Math.max(e.getPath().getNodes().stream().mapToDouble(point->(point.y)).max().getAsDouble(), maxY);
		}
		
		
		// set now the new size of the representing vertex appropriated to the layouted FieldAccessGraphs
		return new DoublePoint(maxX - minX + padding, maxY - minY + padding);	
    }
    
}
