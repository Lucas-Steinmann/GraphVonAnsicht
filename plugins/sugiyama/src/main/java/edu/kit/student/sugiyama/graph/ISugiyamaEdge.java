package edu.kit.student.sugiyama.graph;

import java.util.List;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DirectedEdge;

public interface ISugiyamaEdge extends DirectedEdge {

	void setReversed(boolean b);

	boolean isReversed();

    @Override
    public List<? extends Vertex> getVertices();

    @Override
    public ISugiyamaVertex getSource();

    @Override
    public ISugiyamaVertex getTarget();

    void setVertices(ISugiyamaVertex source, ISugiyamaVertex nv);

}
