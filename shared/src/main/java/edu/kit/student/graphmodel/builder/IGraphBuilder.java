package edu.kit.student.graphmodel.builder;

/**
 * An abstract interface, which is used to build a graph.
 * A build() method is not defined through this interface.
 * The build process is managed by the implementation itself or an implementation
 * of an {@link IGraphModelBuilder}, which itself has a build method.
 *
 * @author Jonas Fehrenbach, Lucas Steinmann
 */
public interface IGraphBuilder {

	/**
	 * Returns the {@link IEdgeBuilder}, which is needed build an edge with the specified
     * parameters in this graph.
	 * 
	 * @return The {@link IEdgeBuilder} which is specified for this graph.
	 * @param sourceId the ID of the source vertex
	 * @param targetId the ID of the target vertex
	 */ // TODO: Add id of the edge as parameter.
	IEdgeBuilder getEdgeBuilder(String sourceId, String targetId);

	/**
	 * Returns the {@link IVertexBuilder}, which is needed to build a vertex with the specified
     * ID in this graph.
	 * 
	 * @param vertexID
	 *            The ID of the vertex which associated {@link IVertexBuilder} will be
	 *            returned.
	 * @return The {@link IVertexBuilder} which is specified for this graph.
	 */
	IVertexBuilder getVertexBuilder(String vertexID);
	
	/**
     * Returns the {@link IGraphBuilder}, which is needed to build a sub-graph with the specified
     * ID in this graph.
     *
     * @param graphID
     *          The ID of the graph whose {@link IGraphBuilder} should be returned.
     * @return The {@link IGraphBuilder} of the graph which is referenced over the
     *         graphID.
     */
    IGraphBuilder getGraphBuilder(String graphID);

    /**
     * Returns the ID of the graph which is build by this builder.
     * @return the ID of the graph
     */
    String getId();

	/**
	 * Adds additional data to this graph.
	 * The implementation needs to decide how to save and process the value.
	 *
	 * @param keyname name of the attribute
	 * @param value   value of the attribute
	 *
	 * @throws IllegalArgumentException if the key value combination is illegal
	 * 					or incompatible with earlier information provided through this interface.
	 */
	public abstract void addData(String keyname, String value) throws IllegalArgumentException;
}
