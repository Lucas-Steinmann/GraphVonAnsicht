package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Edge;

import java.util.function.Predicate;

/**
 * This class represents a filter for edges.
 * To check if an edge passes through this filter, 
 * the client can specify it in {@code matches(Edge edge)}.
 */
public abstract class EdgeFilter extends Filter{

    public EdgeFilter(String name) {
        super(name);
    }

    /**
     * Returns a predicate which tests if a given vertex matches this filter.
     * @return Predicate
     */
    public abstract Predicate<Edge> getPredicate();

    @Override
    public boolean equals(Object o) {
    	if(o instanceof EdgeFilter) {
            return (name.compareTo(((EdgeFilter) o).name) == 0);
    	} else {
    		return super.equals(o);
    	}
    }
}
