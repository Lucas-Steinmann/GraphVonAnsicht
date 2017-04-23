package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.action.EdgeAction;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.VertexFilter;

/**
 * The base graph accessed by the UI.
 */
public interface ViewableGraph extends Graph {

	/**
	 * Returns the name of the Graph.
	 * @return The name of the graph.
	 */
	String getName();

	/**
	 * Returns the ID of the graph.
	 * @return The id of the graph.
	 */
	Integer getID();
	
	/**
	 * Returns the statistics for this graph as a list of GansProperties.
	 * @return the statistics
	 */
	List<GAnsProperty<?>> getStatistics();

	/**
	 * Returns a list of actions which can be executed on the specified vertex induced subgraph.
	 * The first action in the list will be implicitly used as the default action
	 * in the context of an user interface.
	 * @param vertices the vertices inducing the subgraph
	 * @return         the list of actions
	 */
	List<SubGraphAction> getSubGraphActions(Set<ViewableVertex> vertices);

	/**
	 * Returns a list of actions which can be executed on the specified vertex.
	 * The first action in the list will be implicitly used as the default action
	 * in the context of an user interface.
	 * @param vertex the vertex
	 * @return       the list of actions
	 */
	List<VertexAction> getVertexActions(Vertex vertex);

	/**
	 * Returns a list of actions which can be executed on the specified edge.
	 * The first action in the list will be implicitly used as the default action
	 * in the context of an user interface.
	 * @param edge    the edge
	 * @return     	the list of actions
	 */
	List<EdgeAction> getEdgeActions(Edge edge);

	/**
	 * Adds a vertex filter to the list of active filters for this graph.
	 * @param filter the filter to add
	 */
	void addVertexFilter(VertexFilter filter);
	
	/**
	 * Sets  vertex filter from the collection to the list of active filters for this graph.
	 * @param filter the filter collection to add
	 */
	void setVertexFilter(List<VertexFilter> filter);
	
	/**
	 * Returns a unmodifiable list of all active vertex filters for this graph.
	 * @return A list of all active vertex filters for this graph.
	 */
	List<VertexFilter> getActiveVertexFilter();
	
	/**
	 * Adds an edge filter to the list of active filters for this graph.
	 * @param filter the filter to add
	 */
	void addEdgeFilter(EdgeFilter filter);
	
	/**
	 * Adds all edge filter from the collection to the list of active filters for this graph.
	 * @param filter the filter collection to add
	 */
	void setEdgeFilter(List<EdgeFilter> filter);
	
	/**
	 * Returns a unmodifiable list of all active edge filters for this graph.
	 * @return A list of all active edge filters for this graph.
	 */
	List<EdgeFilter> getActiveEdgeFilter();
	
	/**
	 * Removes a vertex filter from the list of active filters
	 * @param filter the filter to remove
	 */
	void removeVertexFilter(VertexFilter filter);
	
	/**
	 * Removes a edge filter from the list of active filters
	 * @param filter the filter to remove
	 */
	void removeEdgeFilter(EdgeFilter filter);

	@Override
    Set<? extends ViewableVertex> getVertexSet();

	/**
	 * Returns the set of all conceptual subgraphs in this graph
	 * @return the list of subgraphs
	 */
	Set<? extends InlineSubGraph> getInlineSubGraphs();

	/**
	 * Returns a list of layouts which have been registered at the corresponding
	 * LayoutRegister for this graph type. The graph implementing this interface
	 * will be set as target of the LayoutOption.
	 * 
	 * @return A list of layouts which have been registered at the corresponding
	 *         LayoutRegister for this graph type.
	 */
	List<LayoutOption> getRegisteredLayouts();

	/**
	 * Returns the default layout for this graph.
	 * This can be called when to quickly get a suiting layout without
	 * having to decide between multiple options.
	 * @return the default layout for this graph
	 */
	LayoutOption getDefaultLayout();
}
