package edu.kit.student.joana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.*;
import edu.kit.student.graphmodel.action.EdgeAction;
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
    private DoublePoint size = DoublePoint.zero();
    private double x = 0d, y = 0d;
    private boolean dirtyProperties = false; //tells if the actual saved properties like x,y,size are still valid (in case of a relayout the saved values are not appropriate set and have to be recalculated)
	public static double paddingx = 80;
	public static double paddingy = 120;

    private JoanaCollapser collapser;
    
    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name, vertices, edges);
        Set<JoanaEdge> innerEdges = edges.stream().filter(e -> vertices.contains(e.getSource()) && vertices.contains(e.getTarget())).collect(Collectors.toSet());
        graph = new DefaultDirectedGraph<>(vertices, innerEdges);

        Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges = new HashMap<>();
        collapser = new JoanaCollapser(graph, onionEdges);
    }

	public JoanaCollapsedVertex collapse(Set<? extends ViewableVertex> subset, JoanaCollapsedVertex collapse) {
        Set<JoanaVertex> directedSubset = new HashSet<>();
	    for (Vertex v : subset) {
	        if (!graph.contains(v)) {
                throw new IllegalArgumentException("Cannot collapse vertices, not contained in this graph.");
	        } else {
	            directedSubset.add(graph.getVertexById(v.getID()));
	        }
	    }
        return collapser.collapse(directedSubset);
	}
    
    public Set<JoanaVertex> expand(JoanaCollapsedVertex vertex) {
        return collapser.expand(vertex);
    }

    public void invalidateProperties(){
        this.dirtyProperties = true;
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
    public List<EdgeAction> getEdgeActions(Edge edge) {
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
    public DoublePoint getSize() { //TODO: think if calculation is necessary. But else the FieldAccess has to set FAG's size if its size was set
        //return this.size;
        if(!this.size.equals(DoublePoint.zero()) && !dirtyProperties) return this.size;
        this.dirtyProperties = false;
        DoublePoint min = this.getMinCoordinate();
        DoublePoint max = this.getMaxCoordinate();
        assert(max.x > min.x && max.y > min.y);
        DoublePoint ret = new DoublePoint(max.x - min.x + paddingx, max.y - min.y + paddingy);
        this.size = ret;
        return ret;
    }


    @Override
    public double getX() {
        //return this.x;
        if(!Objects.equals(this.x,0d) && !dirtyProperties) return this.x;
        this.dirtyProperties = false;
        double ret = this.getMinCoordinate().x - paddingx / 2;
        this.x = ret;
        return ret;
    }

    @Override
    public double getY() {
        //return this.y;
        if(!Objects.equals(this.y,0d) && !dirtyProperties) return this.y;
        this.dirtyProperties = false;
        double ret = this.getMinCoordinate().y - paddingy / 2;
        this.y = ret;
        return ret;
    }

    @Override
    public void setX(double x){
        this.x = x;
    }

    @Override
    public void setY(double y){
        this.y = y;
    }

    //minimum x and y coordinate of vertices and edges in this graph
    private DoublePoint getMinCoordinate(){
        if(this.getVertexSet().isEmpty()) return DoublePoint.zero();
        double minx = Double.MAX_VALUE;
        double miny = Double.MAX_VALUE;
        OptionalDouble tmpx = this.getVertexSet().stream().mapToDouble(DefaultVertex::getX).min();
        OptionalDouble tmpy = this.getVertexSet().stream().mapToDouble(DefaultVertex::getY).min();
        if(tmpx.isPresent()) minx = Math.min(minx, tmpx.getAsDouble());
        if(tmpy.isPresent()) miny = Math.min(miny, tmpy.getAsDouble());
        for(JoanaEdge e : this.getEdgeSet()){
            tmpx = e.getPath().getNodes().stream().mapToDouble(p->p.x).min();
            tmpy = e.getPath().getNodes().stream().mapToDouble(p->p.y).min();
            if(tmpx.isPresent()) minx = Math.min(minx, tmpx.getAsDouble());
            if(tmpy.isPresent()) miny = Math.min(miny, tmpy.getAsDouble());
        }

        return new DoublePoint(minx, miny);
    }

    //maximum x and y coordinate of vertices and edges in this graph
    private DoublePoint getMaxCoordinate(){
        if(this.getVertexSet().isEmpty()) return DoublePoint.zero();
        double maxx = Double.MIN_VALUE;
        double maxy = Double.MIN_VALUE;
        OptionalDouble tmpx = this.getVertexSet().stream().mapToDouble(v -> v.getX()+ v.getSize().x).max();
        OptionalDouble tmpy = this.getVertexSet().stream().mapToDouble(v -> v.getY()+ v.getSize().y).max();
        if(tmpx.isPresent()) maxx = Math.max(maxx, tmpx.getAsDouble());
        if(tmpy.isPresent()) maxy = Math.max(maxy, tmpy.getAsDouble());
        for(JoanaEdge e : this.getEdgeSet()){
            tmpx = e.getPath().getNodes().stream().mapToDouble(p->p.x).max();
            tmpy = e.getPath().getNodes().stream().mapToDouble(p->p.y).max();
            if(tmpx.isPresent()) maxx = Math.max(maxx, tmpx.getAsDouble());
            if(tmpy.isPresent()) maxy = Math.max(maxy, tmpy.getAsDouble());
        }

        return new DoublePoint(maxx, maxy);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        FieldAccessGraph that = (FieldAccessGraph) o;

        if (Double.compare(that.x, x) != 0) return false;
        if (Double.compare(that.y, y) != 0) return false;
        if (dirtyProperties != that.dirtyProperties) return false;
        if (graph != null ? !graph.equals(that.graph) : that.graph != null) return false;
        if (fieldEntry != null ? !fieldEntry.equals(that.fieldEntry) : that.fieldEntry != null) return false;
        if (size != null ? !size.equals(that.size) : that.size != null) return false;
        return collapser != null ? collapser.equals(that.collapser) : that.collapser == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        long temp;
        result = 31 * result + (graph != null ? graph.hashCode() : 0);
        result = 31 * result + (fieldEntry != null ? fieldEntry.hashCode() : 0);
        result = 31 * result + (size != null ? size.hashCode() : 0);
        temp = Double.doubleToLongBits(x);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (dirtyProperties ? 1 : 0);
        result = 31 * result + (collapser != null ? collapser.hashCode() : 0);
        return result;
    }

}
