package edu.kit.student.plugin;

/**
 * The most abstract interface for a constraint.
 * Can later be used to achieve compatibility between constraints of different domains and layouts.
 */
public interface Constraint { 
    
    /**
     * The name of the constraint.
     * @return the name
     */
    public String getName();
}
