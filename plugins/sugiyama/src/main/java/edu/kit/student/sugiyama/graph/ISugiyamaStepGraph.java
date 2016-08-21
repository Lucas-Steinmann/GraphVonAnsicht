package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;

import java.util.List;
import java.util.Set;

public interface ISugiyamaStepGraph extends LayeredGraph {


	@Override
	public List<ISugiyamaVertex> getSortedLayer(int layerNum);
	
    @Override
    public List<ISugiyamaVertex> getLayer(int layerNum);
    
    @Override
    public List<List<ISugiyamaVertex>> getSortedLayers();

    @Override
    public List<List<ISugiyamaVertex>> getLayers();

    @Override
    public Set<? extends ISugiyamaEdge> outgoingEdgesOf(Vertex vertex) ;

    @Override
    public Set<ISugiyamaEdge> incomingEdgesOf(Vertex vertex);

    @Override
    Set<ISugiyamaEdge> selfLoopsOf(Vertex vertex);

    @Override
    public Set<ISugiyamaVertex> getVertexSet();

    @Override
    public Set<ISugiyamaEdge> getEdgeSet();

    @Override
    public Set<ISugiyamaEdge> edgesOf(Vertex vertex);



}
