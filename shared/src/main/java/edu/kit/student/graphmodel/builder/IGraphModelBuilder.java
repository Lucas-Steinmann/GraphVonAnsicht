package edu.kit.student.graphmodel.builder;

import edu.kit.student.graphmodel.GraphModel;

/**
 * An abstract interface, which is used to build a {@link GraphModel}.
 * This class is based on the Builder Pattern.
 */
public interface IGraphModelBuilder {

	/**
	 * Returns a specific {@link IGraphBuilder} for a graph, which belongs to
	 * the {@link GraphModel}.
	 * 
	 * @param graphID
	 *            The id of the graph which associated {@link IGraphBuilder}
	 *            will be returned.
	 * @return The {@link IGraphBuilder} of the graph which is referenced over the
	 *         graphID.
	 */
	IGraphBuilder getGraphBuilder(String graphID);

	/**
	 * Builds the {@link GraphModel}, which was described before this call.
	 *
	 * @return The {@link GraphModel} that is being build by the IGraphModelBuilder.
	 * @throws GraphBuilderException if the model could not be build for some reason
	 */
	GraphModel build() throws GraphBuilderException;
}
