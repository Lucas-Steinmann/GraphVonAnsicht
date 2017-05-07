/**
 * 
 */
package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.Constraint;

import java.util.Set;

/**
 * A absolute constraint, regarding layer assignment, for one set of vertices.
 * Can describe if one set of vertices should be placed in one layer, in a range of layers.
 * The constraint can be inverted, meaning it should not be placed on this layer.
 * Additionally an exclusive flag can be set to mark that only this set of vertices should be placed
 * on the selected range of layer.
 * Consequently inverted and exclusive means all other vertices have to be placed in this range of layers.
 */
public class AbsoluteLayerConstraint implements Constraint {
	private final Set<Vertex> vertices;
	private final int layer;

	/**
	 * Constructs an AbsoluteLayerConstraint.
	 *  @param set set of sugiyama vertices to apply this constraint on
	 * @param layer layer bound of the vertex set
	 */
	public AbsoluteLayerConstraint(Set<Vertex> set, int layer) {
		this.vertices = set;
		this.layer = layer;
	}

	/* (non-Javadoc)
	 * @see plugin.Constraint#getName()
	 */
	@Override
	public String getName() {
		return "AbsoluteLayerConstraint";
	}

	/**
	 * Returns the set of vertices which should be affected by the constraint. 
	 * @return the set of vertices
	 */
	public Set<Vertex> getVertices() {
		return vertices;
	}

	/**
	 * Returns the minimum layer the vertices should be on.
	 * @return the minimum layer
	 */
	public int getLayer() {
		return layer;
	}

	@Override
	public String toString() {
		return "AbsoluteLayerConstraint{" +
				", vertices=" + vertices +
				", layer=" + layer +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbsoluteLayerConstraint that = (AbsoluteLayerConstraint) o;

		if (layer != that.layer) return false;
		return vertices != null ? vertices.equals(that.vertices) : that.vertices == null;
	}

	@Override
	public int hashCode() {
		int result = vertices != null ? vertices.hashCode() : 0;
		result = 31 * result + layer;
		return result;
	}
}
