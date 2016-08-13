package edu.kit.student.joana;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.plugin.LayoutOption;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph {

    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
	private Set<JoanaEdge> inEdges;
	private Set<JoanaEdge> outEdges;
    private JoanaVertex fieldEntry;
    
    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name, vertices, edges);
        Set<JoanaEdge> innerEdges = edges.stream().filter((e) -> vertices.contains(e.getSource()) && vertices.contains(e.getTarget())).collect(Collectors.toSet());
        graph = new DefaultDirectedGraph<>(vertices, innerEdges);
        this.inEdges = new HashSet<>();
        this.outEdges = new HashSet<>();
    }
    
    /**
     * Returns the edge which is going into the entry vertex of this FieldAccessGraph.
     * 
     * @return the edge going into this FieldAccessGraph
     */
    public Set<JoanaEdge> getReplacedInEdges(){
    	return this.inEdges;
    }
    
    /**
     * Returns the edge which is coming out of this FieldAccessGraph. 
     * If there is no such edge, returns null.
     * 
     * @return the edge going out of this FieldAccessGraph, or null if not present
     */
    public Set<JoanaEdge> getReplacedOutEdges(){
    	return this.outEdges;
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

}
