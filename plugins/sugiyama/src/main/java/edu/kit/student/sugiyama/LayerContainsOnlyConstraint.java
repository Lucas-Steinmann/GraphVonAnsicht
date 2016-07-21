package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.Constraint;

import java.util.Set;

/**
 * Created by Sven on 21.07.2016.
 */
public class LayerContainsOnlyConstraint implements Constraint {
    private final Set<Vertex> vertices;
    private final int Layer;

    public LayerContainsOnlyConstraint(Set<Vertex> vertices, int layer) {
        this.vertices = vertices;
        Layer = layer;
    }

    @Override
    public String getName() {
        return "LayerContainsOnlyConstraint";
    }

    public Set<Vertex> getVertices() {
        return vertices;
    }

    public int getLayer() {
        return Layer;
    }

    @Override
    public String toString() {
        return "LayerContainsOnlyConstraint{" +
                "vertices=" + vertices +
                ", Layer=" + Layer +
                '}';
    }
}
