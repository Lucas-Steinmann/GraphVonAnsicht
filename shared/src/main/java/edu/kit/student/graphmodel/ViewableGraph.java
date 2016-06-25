package edu.kit.student.graphmodel;

/**
 * The base graph accessed by the UI.
 */
public interface ViewableGraph<V extends Vertex, E extends Edge<V>> extends Viewable<V, E>, Graph<V, E> {

}
