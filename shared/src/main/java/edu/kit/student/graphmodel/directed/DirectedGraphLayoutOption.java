package edu.kit.student.graphmodel.directed;

import edu.kit.student.plugin.LayoutOption;

/**
 * A {@link LayoutOption} which is specific for {@link DirectedGraph}.
 */
public abstract class DirectedGraphLayoutOption extends LayoutOption {
	
	protected DirectedGraph graph;

	/**
	 * Sets the graph that will be the target of the DirectedGraphLayoutOption.
	 * 
	 * @param graph
	 *            The graph that will be the target of this
	 *            DirectedGraphLayoutOption.
	 */
	public void setGraph(DirectedGraph graph) {
		this.graph = graph;
	}
}
