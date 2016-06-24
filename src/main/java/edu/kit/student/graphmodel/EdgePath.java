package edu.kit.student.graphmodel;

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
	public abstract List<Point> getNodes();


	/**
	 * This class is a standard immutable 2D Vector with integer values as it's components.
	 */
	public class Point 
	{
		public final int x;
		public final int y;
		
		public Point(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

}
