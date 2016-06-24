package edu.kit.student.graphmodel;

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
	 * Builds a graph from the given settings and returns it.
	 * 
	 * @return The graph that is being build by the IGraphBuilder.
	 */
	public Graph build();
}
