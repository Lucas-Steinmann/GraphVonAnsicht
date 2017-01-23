package edu.kit.student.joana;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.InlineSubGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.graphmodel.DirectedOnionPath;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.util.DoublePoint;
import javafx.scene.paint.Color;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph implements InlineSubGraph {

    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    private JoanaVertex fieldEntry;
	public static double paddingx = 40;
	public static double paddingy = 50;

    private JoanaCollapser collapser;
    
    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name, vertices, edges);
        Set<JoanaEdge> innerEdges = edges.stream().filter((e) -> vertices.contains(e.getSource()) && vertices.contains(e.getTarget())).collect(Collectors.toSet());
        graph = new DefaultDirectedGraph<>(vertices, innerEdges);

        Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges = new HashMap<>();
        collapser = new JoanaCollapser(graph, onionEdges);
    }

	public JoanaCollapsedVertex collapse(Set<? extends ViewableVertex> subset, JoanaCollapsedVertex collapse) {
        Set<JoanaVertex> directedSubset = new HashSet<JoanaVertex>();
	    for (Vertex v : subset) {
	        if (!graph.contains(v)) {
                throw new IllegalArgumentException("Cannot collapse vertices, not contained in this graph.");
	        } else {
	            directedSubset.add(graph.getVertexById(v.getID()));
	        }
	    }
	    JoanaCollapsedVertex collapsed = collapser.collapse(directedSubset);
		return collapsed;
	}
    
    public Set<JoanaVertex> expand(JoanaCollapsedVertex vertex) {
        return collapser.expand(vertex);
    }

    @Override
    public FastGraphAccessor getFastGraphAccessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
        // TODO Auto-generated method stub
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        return super.getRegisteredLayouts();
    }

    @Override
    public LayoutOption getDefaultLayout() {
        return null;
    }

    public JoanaVertex getFieldEntry() {
        return fieldEntry;
    }

    public void setFieldEntry(JoanaVertex fieldEntry) {
        this.fieldEntry = fieldEntry;
    }

   @Override
    public Integer outdegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex)).size();
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex)).size();
    }

    @Override
    public Integer selfLoopNumberOf(Vertex vertex) {
        return this.selfLoopsOf(vertex).size();
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> selfLoopsOf(Vertex vertex) {
        return removeFilteredEdges(graph.selfLoopsOf(vertex));
    }

    @Override
    public Set<JoanaVertex> getVertexSet() {
        return removeFilteredVertices(graph.getVertexSet());
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return removeFilteredEdges(graph.getEdgeSet());
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.edgesOf(vertex));
    }
    
    @Override
    public List<SubGraphAction> getSubGraphActions(Set<ViewableVertex> vertices) {
        return new LinkedList<>();
    }

    @Override
    public List<VertexAction> getVertexActions(Vertex vertex) {
        return new LinkedList<>();
    }


    @Override
    public Color getBackgroundColor() {
        return Color.LIGHTGREEN;
    }


    @Override
    public Set<? extends InlineSubGraph> getInlineSubGraphs() {
        return new HashSet<>();
    }

    @Override
    public DoublePoint getSize() {
        if (this.getVertexSet().isEmpty()) {
            return new DoublePoint(0, 0);
        }
        Set<JoanaVertex> fagVertices = this.getVertexSet();
        Set<JoanaEdge> fagEdges = this.getEdgeSet();
        
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
		return new DoublePoint(maxX - minX + paddingx, maxY - minY + paddingy);	
    }


    @Override
    public Double getX() {
        if (getVertexSet().isEmpty()) {
            return 0d;
        }
        return (double) getVertexSet().stream().min(Comparator.comparing(v -> v.getX())).get().getX() - paddingx/2;
    }


    @Override
    public Double getY() {
        if (getVertexSet().isEmpty()) {
            return 0d;
        }
        return (double) getVertexSet().stream().min(Comparator.comparing(v -> v.getY())).get().getY() - paddingy/2;
    }
    
}
