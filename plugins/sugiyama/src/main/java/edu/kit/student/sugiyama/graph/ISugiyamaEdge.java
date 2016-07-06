package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.directed.DirectedEdge;

public interface ISugiyamaEdge extends DirectedEdge<ISugiyamaVertex>{

	void setReversed(boolean b);

	boolean isReversed();




}
