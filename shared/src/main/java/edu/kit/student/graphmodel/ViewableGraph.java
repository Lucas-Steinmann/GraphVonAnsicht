package edu.kit.student.graphmodel;

import java.util.List;

import edu.kit.student.objectproperty.GAnsProperty;

/**
 * The base graph accessed by the UI.
 */
public interface ViewableGraph extends Viewable, Graph {

	public ViewableGraph getParentGraph();
	
	public void setParentGraph(ViewableGraph parent);
	
	public List<ViewableGraph> getChildGraphs();
	
	public void addChildGraph(ViewableGraph child);
	
	public List<GAnsProperty<?>> getStatistics();

	/**
	 * Returns the name of the Graph.
	 * 
	 * @return The name of the graph.
	 */
	public String getName();

	/**
	 * Returns the ID of the graph.
	 * 
	 * @return The id of the graph.
	 */
	public Integer getID();

}
