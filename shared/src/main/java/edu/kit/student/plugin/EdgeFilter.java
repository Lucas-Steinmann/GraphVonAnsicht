package edu.kit.student.plugin;

import java.util.function.Predicate;

import edu.kit.student.graphmodel.Edge;

/**
 * This class represents a filter for edges. 
 * To check if an edge passes through this filter, 
 * the client can specify it in {@code matches(Edge edge)}.
 */
public abstract class EdgeFilter {

    private String name;

    public EdgeFilter(String name) {
        this.name = name;
    }
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
     * Returns a predicate which tests if a given vertex matches this filter.
     */
    public abstract Predicate<Edge> getPredicate();
    
    @Override
    public boolean equals(Object o) {
    	if(o instanceof EdgeFilter) {
    		return (this.getName().compareTo(((EdgeFilter)o).getName()) == 0);
    	} else {
    		return super.equals(o);
    	}
    }
}
