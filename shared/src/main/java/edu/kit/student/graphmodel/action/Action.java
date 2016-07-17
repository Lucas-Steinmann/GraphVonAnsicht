package edu.kit.student.graphmodel.action;

/**
 * An action, which can be performed on some graph element.
 * This is just the super interface for all other actions to achieve a common interface.
 *
 */
public interface Action {
    
    /**
     * Returns the name of the action.
     * @return the name
     */
    public String getName();
    
    /**
     * Returns an description on what the actions effects will be.
     * @return the description
     */
    public String getDescription();
    
    /**
     * When executed, this action will be performed.
     */
    public void handle();

}
