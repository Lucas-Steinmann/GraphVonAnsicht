package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.util.LanguageManager;

import java.util.function.Predicate;

/**
 * This is a common interface for all filter for vertex types.
 * The type of the vertex can be specified through different parameters.
 */
public abstract class VertexFilter {

    private String name;

    public VertexFilter(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the filter.
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the filter.
     * @param name the name
     */
    public void setName(String name) { 
        this.name = name; 
    }

    /**
     * Returns the name of the group this filter belongs to.
     * Groups of filters are visually represented together
     * and can be activated or deactivated as a group.
     * @return the group name
     */
    public String getGroup() {
        return LanguageManager.getInstance().get("filter_default_group");
    }

    /**
     * Returns a predicate which tests if a given vertex matches this filter.
     * @return The Predicate
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
