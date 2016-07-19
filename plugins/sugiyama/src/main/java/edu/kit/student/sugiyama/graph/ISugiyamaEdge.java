package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DirectedEdge;

import java.util.List;

public interface ISugiyamaEdge extends DirectedEdge {

	void reverse();

	boolean isReversed();
	
	public boolean isSupplementEdge();

    @Override
    public List<? extends Vertex> getVertices();

    @Override
    public ISugiyamaVertex getSource();

    @Override
    public ISugiyamaVertex getTarget();

    void setVertices(ISugiyamaVertex source, ISugiyamaVertex nv);

    public DirectedEdge getWrappedEdge();
}
