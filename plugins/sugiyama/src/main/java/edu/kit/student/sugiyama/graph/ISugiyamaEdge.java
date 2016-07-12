package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.directed.DirectedEdge;

public interface ISugiyamaEdge extends DirectedEdge<ISugiyamaVertex>{

	public void setReversed(boolean b);

	public boolean isReversed();
	
	public boolean isSupplementEdge();
}
