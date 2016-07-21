package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.Constraint;

import java.util.Set;

/**
 * A relative constraint, regarding layer assignment, between to sets of vertices.
 * Can describe if one set of vertices should be on top of the other.
 * When the exact is set a layer distance can be set.
 */
public class RelativeLayerConstraint implements Constraint
{
	private final Set<Vertex> top;
	private final Set<Vertex> bottom;
	private final boolean exact;
	private final int distance;

	/**
	 * Constructs a new RelativeLayerConstraint, sets the top and bottom vertices, whether its exact and the distance.
	 * @param top The top vertices.
	 * @param bottom The bottom vertices.
	 * @param exact True is exact, false is not exact.
	 * @param distance The distance between top and bottom layer.
	 */
	public RelativeLayerConstraint(Set<Vertex> top, Set<Vertex> bottom, boolean exact, int distance) {
	    this.top = top;
	    this.bottom = bottom;
	    this.exact = exact;
	    this.distance = distance;
	    
	}
	
	/**
	 * Returns true if the constraints describes an exact distance between the two sets, false otherwise.
	 * @return true if exact
	 */
	public boolean isExact() { return exact; };
	
	/**
	 * Returns the distance the two sets should be apart, if this constraint is exact.
	 * @return the number of layers between the sets
	 * @throws IllegalStateException if the set is not exact
	 */
	public int getDistance() throws IllegalStateException {
		if (!exact)
			throw new IllegalStateException();
		return distance; 
    }
	
	/**
	 * Returns the set which should be on top.
	 * @return the top layer
	 */
	public Set<Vertex> topSet() { return top; }
	
	/**
	 * Returns the set which should be below.
	 * @return the bottom layer
	 */
	public Set<Vertex> bottomSet() { return bottom; }

	/**
	 * Returns the name of the layout constraint
	 */
	public String getName() {
		return "RelativeLayerConstraint";
	}

	@Override
	public String toString() {
		return "RelativeLayerConstraint{" +
				"top=" + top +
				", bottom=" + bottom +
				", exact=" + exact +
				", distance=" + distance +
				'}';
	}
}