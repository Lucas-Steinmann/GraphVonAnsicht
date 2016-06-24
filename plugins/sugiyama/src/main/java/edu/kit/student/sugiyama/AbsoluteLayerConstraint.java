/**
 * 
 */
package edu.kit.student.sugiyama;

import java.util.Set;

import edu.kit.student.plugin.Constraint;
import edu.kit.student.sugiyama.SugiyamaGraph.SugiyamaVertex;

/**
 * A absolute constraint, regarding layer assignment, for one set of vertices.
 * Can describe if one set of vertices should be placed in one layer, in a range of layers.
 * The constraint can be inverted, meaning it should not be placed on this layer.
 * Additionally an exclusive flag can be set to mark that only this set of vertices should be placed
 * on the selected range of layer.
 * Consequently inverted and exclusive means all other vertices have to be placed in this range of layers.
 */
public class AbsoluteLayerConstraint implements Constraint {
	
	private boolean inverted;
	private boolean exclusive;
	private Set<SugiyamaVertex> vertices;
	private int minLayer;
	private int maxLayer;

	/**
	 * Constructs an AbsoluteLayerConstraint.
	 * 
	 * @param set set of sugiyama vertices to apply this constraint on
	 * @param inverted invertes this constraint, if true
	 * @param exclusive places only this set of vertices in this range of layers
	 * @param minLayer lower bound of layer number
	 * @param maxLayer upper bound of layer number
	 */
	public AbsoluteLayerConstraint(Set<SugiyamaVertex> set, boolean inverted, boolean exclusive, int minLayer, int maxLayer) { }

	/* (non-Javadoc)
	 * @see plugin.Constraint#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns true if the constraint should be inverted.
	 * @return true if inverted
	 */
	public boolean isInverted() {
		return inverted;
	}

	/**
	 * Returns true if the set of vertices should be affected by the constraint exclusively.
	 * @return true if exclusive
	 */
	public boolean isExclusive() {
		return exclusive;
	}

	/**
	 * Returns the set of vertices which should be affected by the constraint. 
	 * @return the set of vertices
	 */
	public Set<SugiyamaVertex> getVertices() {
		return vertices;
	}

	/**
	 * Returns the minimum layer the vertices should be on.
	 * @return the minimum layer
	 */
	public int getMinLayer() {
		return minLayer;
	}

	/**
	 * Returns the maximum layer the vertices should be on.
	 * @return the maximum layer
	 */
	public int getMaxLayer() {
		return maxLayer;
	}
}
