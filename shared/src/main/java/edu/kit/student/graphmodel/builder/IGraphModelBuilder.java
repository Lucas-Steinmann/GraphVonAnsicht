package edu.kit.student.graphmodel.builder;

import edu.kit.student.graphmodel.GraphModel;

/**
 * An abstract interface, which is used to build a graphmodel. This class is
 * based on the Builder Pattern.
 */
public interface IGraphModelBuilder {

	/**
	 * Returns a specific {@link IGraphBuilder} for a graph, which belongs to
	 * the {@link GraphModel}.
	 * 
	 * @param graphID
	 *            The id of the graph which associated {@link IGraphBuilder}
	 *            will be returned.
	 * @return The IGraphBuilder of the graph which is referenced over the
	 *         graphID.
	 */
	public IGraphBuilder getGraphBuilder(String graphID);

	/**
	 * This method is called, when the buildingprocess of the graphmodel is
	 * finished. It returns the finished graphmodel
	 * 
	 * @return GraphModel
	 */
	/**
	 * Builds a graphmodel from the given settings and returns it.
	 * 
	 * @return The {@link GraphModel} that is being build by the
	 *         IGraphModelBuilder.
	 */
	public GraphModel build() throws IllegalStateException;

}
