package edu.kit.student.sugiyama.graph;

import java.util.List;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DirectedEdge;

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

}
