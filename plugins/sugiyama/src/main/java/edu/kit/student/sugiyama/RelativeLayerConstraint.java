package edu.kit.student.sugiyama;

import java.util.Set;

import edu.kit.student.plugin.Constraint;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaVertex;

/**
 * A relative constraint, regarding layer assignment, between to sets of vertices.
 * Can describe if one set of vertices should be on top of the other.
 * When the exact is set a layer distance can be set.
 */
public class RelativeLayerConstraint implements Constraint
{
	private Set<SugiyamaVertex> top;
	private Set<SugiyamaVertex> bottom;
	private boolean exact;
	private int distance;
	private String name = "RelativeLayerConstraint";
	
	/**
	 * Constructs a new RelativeLayerConstraint, sets the top and bottom vertices, whether its direct and the distance.
	 * @param top The top vertices.
	 * @param bottom The bottom vertices.
	 * @param direct True is direct, false is not direct.
	 * @param distance The distance between top and bottom layer.
	 */
	public RelativeLayerConstraint(Set<SugiyamaVertex> top, Set<SugiyamaVertex> bottom, boolean direct, int distance) {}
	
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
	public Set<SugiyamaVertex> topSet() { return top; }
	
	/**
	 * Returns the set which should be below.
	 * @return the bottom layer
	 */
	public Set<SugiyamaVertex> bottomSet() { return bottom; }

	/**
	 * Returns the name of the layout constraint
	 */
	public String getName() {
		return name;
	}
	
}