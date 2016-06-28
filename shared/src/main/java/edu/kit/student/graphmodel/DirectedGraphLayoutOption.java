package edu.kit.student.graphmodel;

import edu.kit.student.plugin.LayoutOption;

/**
 * A {@link LayoutOption} which is specific for {@link DirectedGraph}.
 */
public abstract class DirectedGraphLayoutOption extends LayoutOption {
	
	DirectedGraph<? extends Vertex, ? extends DirectedEdge<? extends Vertex>> graph;

	/**
	 * Sets the graph that will be the target of the DirectedGraphLayoutOption.
	 * 
	 * @param graph
	 *            The graph that will be the target of this
	 *            DirectedGraphLayoutOption.
	 */
	public void setGraph(DirectedGraph<? extends Vertex, ? extends DirectedEdge<? extends Vertex>> graph) {
		this.graph = graph;
	}
}
