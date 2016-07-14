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

}
