package edu.kit.student.graphmodel;

/**
 * This class allows to collect an amount of vertices.
 *
 * @param <V> the type of vertex in this Group
 */
public class Group<V extends Vertex>{

	/**
	 * Adds the vertex to this Group.
	 * 
	 * @param vertex
	 * 		vertex to add to this group
	 */
	public void addVertex(V vertex){}
	
	/**
	 * Removes the vertex argument from this Group.
	 * 
	 * @param vertex
	 * 		vertex to remove from this Group
	 */
	public void removeVertex(V vertex){}
	
}
