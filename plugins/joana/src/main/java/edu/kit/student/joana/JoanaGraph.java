package edu.kit.student.joana;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.util.IdGenerator;

/**
 * An abstract superclass for all JOANA specific graphs.
 */
public abstract class JoanaGraph
    implements DirectedGraph, ViewableGraph {
    
    private Integer id;
    private GAnsProperty<String> name;
    
    private GAnsProperty<Integer> edgeCount;
    private GAnsProperty<Integer> vertexCount;
    private List<VertexFilter> vertexFilter;
    private List<EdgeFilter> edgeFilter;
    

    public JoanaGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        this.name = new GAnsProperty<String>("Name", name);
        this.id = IdGenerator.getInstance().createId();    
        this.edgeCount = new GAnsProperty<Integer>("Edge count", edges.size());
        this.vertexCount = new GAnsProperty<Integer>("Vertex count", vertices.size());
        this.vertexFilter = new LinkedList<>();
        this.edgeFilter = new LinkedList<>();  
    }

    @Override
    public String getName() {
        return name.getValue();
    }

    @Override
    public Integer getID() {
        return this.id;
    }
    
    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = new LinkedList<GAnsProperty<?>>();
    	statistics.add(name);
    	statistics.add(this.vertexCount);
    	statistics.add(this.edgeCount);
    	return statistics;
    }

    public List<LayoutOption> getRegisteredLayouts() {
        return new LinkedList<>();
    }

    @Override
    public void addVertexFilter(VertexFilter filter) {
        this.vertexFilter.add(filter);
    }
    
    @Override
    public void setVertexFilter(List<VertexFilter> filter) {
    	this.vertexFilter = filter;
    }

    @Override
    public void addEdgeFilter(EdgeFilter filter) {
        this.edgeFilter.add(filter);
    }
    
    @Override
    public void setEdgeFilter(List<EdgeFilter> filter) {
    	this.edgeFilter = filter;
    }
    
    @Override
    public List<VertexFilter> getActiveVertexFilter() {
    	return Collections.unmodifiableList(this.vertexFilter);
    }
    
    @Override
    public List<EdgeFilter> getActiveEdgeFilter() {
    	return Collections.unmodifiableList(this.edgeFilter);
    }
    
    public Set<JoanaEdge> removeFilteredEdges(Set<JoanaEdge> edges) {
        Set<JoanaEdge> edgeFiltered = edges.stream().filter(e -> edgeFilter.stream().allMatch(f -> f.getPredicate().negate().test(e))).collect(Collectors.toSet());
        Set<JoanaEdge> vertexFiltered = new HashSet<>(edgeFiltered);
        for (JoanaEdge edge : edgeFiltered) {
            JoanaVertex source = edge.getSource();
            JoanaVertex target = edge.getTarget();
            if (vertexFilter.stream().anyMatch(f -> f.getPredicate().test(source) || f.getPredicate().test(target))) {
                vertexFiltered.remove(edge);
            }
        }
        return vertexFiltered;
    }

    public Set<JoanaVertex> removeFilteredVertices(Set<JoanaVertex> vertices) {
        return vertices.stream().filter(v -> vertexFilter.stream().allMatch(f -> f.getPredicate().negate().test(v))).collect(Collectors.toSet());
    }

    @Override
    public void removeVertexFilter(VertexFilter filter) {
        this.vertexFilter.remove(filter);
    }

    @Override
    public void removeEdgeFilter(EdgeFilter filter) {
        this.edgeFilter.remove(filter);
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
}
