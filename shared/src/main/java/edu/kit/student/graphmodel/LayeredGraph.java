package edu.kit.student.graphmodel;

import edu.kit.student.graphmodel.directed.DirectedGraph;

/**
 * A DirectedGraph which in addition to coordinates saves the relative position of all vertices
 * in a layered structure.
 * Every vertex is in a layer. Every layer is sorted so that every node has zero to two horizontal neighbors.
 *
 */
public interface LayeredGraph extends DirectedGraph {
    
    /**
     * Returns the {@link GraphLayering} for this Graph
     * This Layering contains a mapping from all vertices in this graph
     * to their position in the layers.
     * @return the graph layering
     */
    public GraphLayering<? extends Vertex> getGraphLayering();
}
