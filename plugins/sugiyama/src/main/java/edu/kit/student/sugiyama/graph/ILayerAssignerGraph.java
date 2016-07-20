package edu.kit.student.sugiyama.graph;


/**
 * A LayeredGraph which additionally defines functions to assign layers in the sugiyama-layout.
 */
public interface ILayerAssignerGraph extends ISugiyamaStepGraph {
	/**
	 * Reverses the direction of a sugiyama edge.
	 * The underlying edge won't be reversed to avoid
	 * inconsistencies in the underlying graph
	 * Instead the reversing will be saved in the SugiyamaEdge.
	 * If the edge is already reversed it will be reversed again.
	 *
	 * @param edge the edge to reverse its direction
	 */
	public void reverseEdge(ISugiyamaEdge edge);

	/**
	 * Assigns a vertex to a certain layer represented by a number.
	 * 
	 * @param vertex   the vertex to assign to a layer
	 * @param layerNum the layer number to assign a vertex to
	 */
	public void assignToLayer(ISugiyamaVertex vertex, int layerNum);
	
	/**
	 * Get the layer from the vertex
	 * 
	 * @param vertex the vertex to get its layer from
	 * @return 		 the layer number from this vertex
	 */
	public int getLayer(ISugiyamaVertex vertex);
	
	
	/**
	 * Get the number of vertices which are on a  certain layer
	 * 
	 * @param layerNum the layer number to get the vertex count from
	 * @return         the number of vertices which are on this layer
	 */
	public int getVertexCount(int layerNum);

	public void insertLayers(int position, int numberOfLayers);

	public ISugiyamaVertex getVertexByID(int vertexID);

	public void cleanUpEmtpyLayers();
}
