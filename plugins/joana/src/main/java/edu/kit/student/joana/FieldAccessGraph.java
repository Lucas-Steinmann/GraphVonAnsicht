package edu.kit.student.joana;

import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.SerializedGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.plugin.LayoutOption;

import java.util.List;
import java.util.Set;


/**
 * A {@link JoanaGraph} which specifies a {@link FieldAccess} in a {@link JoanaGraph}.
 */
public class FieldAccessGraph extends JoanaGraph {

    DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    DefaultGraphLayering<JoanaVertex> layering;

    public FieldAccessGraph(String name, Set<JoanaVertex> vertices, Set<JoanaEdge> edges) {
        //TODO: Check whether the sets build a valid field access
        super(name);
        this.graph = new DefaultDirectedGraph<>("", vertices, edges);
        this.layering = new DefaultGraphLayering<>(vertices);
    }

    @Override
    public Integer outdegreeOf(Vertex vertex) {
        return graph.outdegreeOf(vertex);
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return graph.indegreeOf(vertex);
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaVertex> getVertexSet() {
        return graph.getVertexSet();
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return graph.getEdgeSet();
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return graph.edgesOf(vertex);
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
    public SerializedGraph serialize() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public LayoutOption getDefaultLayout() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLayerCount() {
        return this.layering.getLayerCount();
    }

    @Override
    public int getVertexCount(int layerNum) {
        return layering.getVertexCount(layerNum);
    }

    @Override
    public int getLayerFromVertex(Vertex vertex) {
        return layering.getLayerFromVertex(vertex);
    }

    @Override
    public List<? extends Vertex> getLayer(int layerNum) {
        return layering.getLayer(layerNum);
    }

    @Override
    public List<List<JoanaVertex>> getLayers() {
        return layering.getLayers();
    }

    @Override
    public int getHeight() {
        return layering.getHeight();
    }

    @Override
    public int getLayerWidth(int layerN) {
        return layering.getLayerWidth(layerN);
    }

    @Override
    public int getMaxWidth() {
        return layering.getMaxWidth();
    }
}
