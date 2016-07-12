package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

/**
 * This class represents a filter for edges. 
 * To check if an edge passes through this filter, 
 * the client can specify it in {@code matches(Edge edge)}.
 */
public abstract class EdgeFilter<E extends Edge, V extends Vertex> {

    private String name;

    /**
     * Returns the name of the filter.
     * @return the name of the filter
     */
    public String getName() {
        return name; 
    }

    /**
     * Sets the name of the filter.
     * @param name the name of the filter
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * This method checks if an edge matches this Filter.
     * It will compare specified parameters of the edge with the defined parameters of this filter.
     * 
     * @param  toMatch the edge which should be checked
     * @return true    if the edge matches this Filter, otherwise false
     */
    public abstract boolean matches(E toMatch); 

}
