package edu.kit.student.graphmodel;

import edu.kit.student.util.DoublePoint;

import java.util.List;

/**
 * An abstract super class for edge paths.
 * Contains basic information every edge path must provide.
 */
public abstract class EdgePath {
	
	/**
	 * Returns out of how many segments the path consists.
	 * @return the number of segments
	 */
	public abstract int getSegmentsCount();
	
	
	/**
	 * Returns all nodes the edge has to pass through.
	 * In the order it has to pass through them.
	 * @return the list of nodes
	 */
	public abstract List<DoublePoint> getNodes();
	
	/**
	 * Adds a new Point to to this EdgePath.
	 * @param newPoint new point to add to this Edgepath
	 */
	public abstract void addPoint(DoublePoint newPoint);


	public abstract void clear();
}
