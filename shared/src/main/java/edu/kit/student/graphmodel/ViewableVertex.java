package edu.kit.student.graphmodel;

public interface ViewableVertex extends Vertex {
    
    /**
     * Returns the id of the graph this linked vertex links to.
     * Returns -1 when no link is set.
     * @return the id of the graph
     */
    public int getLink();
}
