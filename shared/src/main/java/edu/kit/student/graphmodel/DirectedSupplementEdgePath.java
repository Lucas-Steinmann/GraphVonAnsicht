package edu.kit.student.graphmodel;

import java.util.List;

import edu.kit.student.graphmodel.directed.DirectedEdge;



/**
 * Provides methods for creating a path that represents a DirectedEdge. 
 * Between the source and target of the edge any number of vertices can be added.
 * It's necessary that given edges connect source and target across the given edges.
 */
public interface DirectedSupplementEdgePath {

	public int getLength();
	
	public List<Vertex> getDummyVertices();
	
	public List<DirectedEdge> getSupplementEdges();
	
	public DirectedEdge getReplacedEdge();
}
