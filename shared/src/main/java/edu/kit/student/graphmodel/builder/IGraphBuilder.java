package edu.kit.student.graphmodel.builder;

import edu.kit.student.graphmodel.Graph;

/**
 * An abstract interface, which is used to build a graph.
 */
public interface IGraphBuilder {

	/**
	 * Returns the EdgeBuilder which is specified for this graph.
	 * 
	 * @return The {@link IEdgeBuilder} which is specified for this graph.
	 */
	public abstract IEdgeBuilder getEdgeBuilder();

	/**
	 * Returns the VertexBuilder which is specified for this graph.
	 * 
	 * @param vertexID
	 *            The id of the vertex which associated IVertexBuilder will be
	 *            returned.
	 * @return The {@link IVertexBuilder} which is specified for this graph.
	 */
	public abstract IVertexBuilder getVertexBuilder(String vertexID);
	
	/**
     * This method returns an specific GraphBuilder. This method is used to
     * implement nested Graphs.
     * 
     * @param graphID
     *          The id of the graph which associated {@link IGraphBuilder}
     *          will be returned.
     * @return The IGraphBuilder of the graph which is referenced over the
     *         graphID.
     */
    public IGraphBuilder getGraphBuilder(String graphID);
}
