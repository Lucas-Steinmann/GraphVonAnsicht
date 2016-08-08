package edu.kit.student.graphmodel;

import java.util.List;

/**
 * A GraphModel contains one or more graphs. It is used to save nested or
 * hierarchical graphs in one class.
 */
public abstract class GraphModel {

	/**
	 * Returns all {@link Graph} at the rootlevel contained in the GraphModel.
	 * 
	 * @return A list of all the root{@link Graph} contained in the GraphModel.
	 */
	public abstract List<? extends ViewableGraph> getRootGraphs();
	
	public abstract ViewableGraph getGraphFromId(Integer id);

	/**
	 * Returns the parent graph of the graph
	 * @param graph the graph to get the parent of
	 * @return the parent graph
	 */
    public abstract ViewableGraph getParentGraph(ViewableGraph graph);
	
	
	/**
	 * Returns a list of child graphs of the graph
	 * @param graph the graph to get the children of
	 * @return the child graphs
	 */
	public abstract List<? extends ViewableGraph> getChildGraphs(ViewableGraph graph);
}
