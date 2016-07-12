package edu.kit.student.graphmodel;

import java.util.List;

import edu.kit.student.util.Point;

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
	public abstract List<Point> getNodes();
	
	/**
	 * Adds a new Point to to this EdgePath.
	 * @param newPoint new point to add to this Edgepath
	 */
	public abstract void addPoint(Point newPoint);


}
