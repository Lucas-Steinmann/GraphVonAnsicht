package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Vertex;

import java.util.function.Predicate;

/**
 * This is a common interface for all filter for vertex types.
 * The type of the vertex can be specified through different parameters.
 */
public abstract class VertexFilter extends Filter {

    public VertexFilter(String name) {
        super(name);
    }

    /**
     * Returns a predicate which tests if a given vertex matches this filter.
     * @return The Predicate
     */
    public abstract Predicate<Vertex> getPredicate();

}
