package edu.kit.student.plugin;

import java.util.function.Predicate;

import edu.kit.student.graphmodel.Vertex;

/**
 * This Class represents a filter for vertex types. 
 * The type of the vertex can be specified through different parameters.
 * 
 *
 */
public abstract class VertexFilter {

    private String name;

    public VertexFilter(String name) {
        this.name = name;
    }

    /**
     * Getter of name.
     */
    public String getName() {
        return name;
    }

    /**
     * Setter of name.
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * Returns a predicate which tests if a given vertex matches this filter.
     */
    public abstract Predicate<Vertex> getPredicate();
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof VertexFilter) {
            return (name.compareTo(((VertexFilter) o).name) == 0);
    	} else {
    		return super.equals(o);
    	}
    }

}
