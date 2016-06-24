package edu.kit.student.sugiyama;

import java.util.Set;

/**
 * This interface represents a class that takes a directed graph and assigns every vertex in it a layer.
 */
public interface ILayerAssigner {

	/**
	 * Assigns every vertex in the graph parameter e relative height.
	 * @param graph input graph 
	 */ 
	public void assignLayers(ILayerAssignerGraph graph);
	
	/**
	 * Defines a set of constraints which should be considered by the algorithm.
	 * @param constraints relative layer constraints the algorithm should consider
	 */
	public void addConstraints(Set<RelativeLayerConstraint> constraints);
	
	/**
	 * Reassigns the layer of vertices whose layer is greater than the height parameter.
	 * @param height maximum height for vertices
	 */
	public void setMaxHeight(int height);
	
	/**
	 * Reassigns the layer of vertices in case there are more than the width parameter in one layer.
	 * @param width maximum amount of vertices in one layer
	 */
	public void setMaxWidth(int width);
}
