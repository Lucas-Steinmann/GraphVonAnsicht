package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.VertexFilter;

/**
 * The base graph accessed by the UI.
 */
public interface ViewableGraph extends Viewable, Graph {

	/**
	 * Returns the name of the Graph.
	 * @return The name of the graph.
	 */
	public String getName();

	/**
	 * Returns the ID of the graph.
	 * @return The id of the graph.
	 */
	public Integer getID();
	
	/**
	 * Returns the statistics for this graph as a list of GansProperties.
	 * @return the statistics
	 */
	public List<GAnsProperty<?>> getStatistics();

	/**
	 * Returns a list of actions which can be executed on the specified vertex induced subgraph.
	 * @param vertices the vertices inducing the subgraph
	 * @return         the list of actions
	 */
	public List<SubGraphAction> getSubGraphActions(Set<Vertex> vertices);

	/**
	 * Returns a list of actions which can be executed on the specified vertex.
	 * @param vertex the vertex
	 * @return       the list of actions
	 */
	public List<VertexAction> getVertexActions(Vertex vertex);
	
	/**
	 * Adds a vertex filter to the list of active filters for this graph.
	 * @param filter the filter to add
	 */
	public void addVertexFilter(VertexFilter filter);
	
	/**
	 * Adds an edge filter to the list of active filters for this graph.
	 * @param filter the filter to add
	 */
	public void addEdgeFilter(EdgeFilter filter);
	
	/**
	 * Removes a vertex filter from the list of active filters
	 * @param filter the filter to remove
	 */
	public void removeVertexFilter(VertexFilter filter);
	
	/**
	 * Removes a edge filter from the list of active filters
	 * @param filter the filter to remove
	 */
	public void removeEdgeFilter(EdgeFilter filter);

	@Override
    public Set<? extends ViewableVertex> getVertexSet();

	// TODO: Maybe move methods below this to GraphModel, which should be in power of the inter-graph structure
	/**
	 * Returns the parent graph of this graph
	 * @return the parent graph
	 */
    public ViewableGraph getParentGraph();
	
    /**
     * Sets the parent graph for this graph
     * @param parent the parent graph
     */
	public void setParentGraph(ViewableGraph parent);
	
	/**
	 * Returns a list of child graphs of this graph
	 * @return the child graphs
	 */
	public List<ViewableGraph> getChildGraphs();
	
	/**
	 * Adds a child graph to the children of this graph
	 * @param child the graph to add
	 */
	public void addChildGraph(ViewableGraph child);
}
