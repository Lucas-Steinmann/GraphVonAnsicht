package edu.kit.student.plugin;

import java.util.List;

/**
 * Stores a collection of layouts for a specific graph.
 * This allows the client to select fitted layouts for every graph.
 */
public interface LayoutRegister<E extends LayoutOption> {
	
	/**
	 * Adds a layout option to this register.
	 * @param option the layout option
	 */
	public void addLayoutOption(E option);
	
	/**
	 * Returns all available layouts for the graph associated with this register.
	 * @return the available layouts
	 */
	public List<LayoutOption> getLayoutOptions();
}
