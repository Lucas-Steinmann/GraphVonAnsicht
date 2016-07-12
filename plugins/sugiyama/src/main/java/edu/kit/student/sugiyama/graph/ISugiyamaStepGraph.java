package edu.kit.student.sugiyama.graph;

import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;

public interface ISugiyamaStepGraph extends LayeredGraph {


    @Override
    public List<ISugiyamaVertex> getLayer(int layerNum);

    @Override
    public List<List<ISugiyamaVertex>> getLayers();

    @Override
    public Set<? extends ISugiyamaEdge> outgoingEdgesOf(Vertex vertex) ;

    @Override
    public Set<ISugiyamaEdge> incomingEdgesOf(Vertex vertex);

    @Override
    public Set<ISugiyamaVertex> getVertexSet();

    @Override
    public Set<ISugiyamaEdge> getEdgeSet();

    @Override
    public Set<ISugiyamaEdge> edgesOf(Vertex vertex);



}
