package edu.kit.student.graphmodel;

import java.util.List;

/**
 * The base graph accessed by the UI.
 */
public interface ViewableGraph extends Viewable, Graph {

	public ViewableGraph getParentGraph();
	
	public void setParentGraph(ViewableGraph parent);
	
	public List<ViewableGraph> getChildGraphs();
	
	public void addChildGraph(ViewableGraph child);
}
