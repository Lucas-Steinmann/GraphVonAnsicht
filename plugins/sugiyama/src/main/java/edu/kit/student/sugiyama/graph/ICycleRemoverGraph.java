package edu.kit.student.sugiyama.graph;

import java.util.Set;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DirectedGraph;

/**
 * A DirectedGraph which additionally defines functions to remove cycles in the graph.
 */
public interface ICycleRemoverGraph extends DirectedGraph {
	/**
	 * Reverses the direction of a sugiyama edge.
	 * The underlying edge won't be reversed to avoid
	 * inconsistencies in the underlying graph
	 * Instead the reversing will be saved in the SugiyamaEdge.
	 * If the edge is already reversed it will be reversed again.
	 * 
	 * @param edge the edge to reverse its direction
	 */
	public void reverseEdge(ISugiyamaEdge edge);
	
	/**
	 * Returns true if the specified edge is reversed, false otherwise
	 * @param edge the edge
	 * @return true if the edge is reversed, false otherwise
	 */
	public boolean isReversed(ISugiyamaEdge edge);

    @Override
    public Set<ISugiyamaEdge> edgesOf(Vertex vertex);

    @Override
    public Set<ISugiyamaEdge> outgoingEdgesOf(Vertex vertex);

    @Override
    public Set<ISugiyamaEdge> incomingEdgesOf(Vertex vertex);

    @Override
    public Set<ISugiyamaVertex> getVertexSet();

    @Override
    public Set<ISugiyamaEdge> getEdgeSet();
}
