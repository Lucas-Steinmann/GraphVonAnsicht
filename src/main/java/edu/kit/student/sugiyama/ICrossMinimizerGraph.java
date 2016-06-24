package edu.kit.student.sugiyama;

import java.util.List;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.sugiyama.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.SugiyamaGraph.SugiyamaVertex;

/**
 * A LayeredGraph which additionally defines functions that can be used to minimize the crossings in the sugiyama-layout.
 */
public interface ICrossMinimizerGraph extends LayeredGraph<SugiyamaVertex, SugiyamaEdge> {

	/**
	 * Get the amount of layers.
	 * @return the amount of layers that contain at least one vertex
	 */
	public int getLayerCount();
	
	/**
	 * Swaps the position of two vertices that are on the same layer
	 * 
	 * @param first	 first vertex to change position with
	 * @param second second vertex to change position with
	 */
	public void swapVertices(SugiyamaVertex first, SugiyamaVertex second);
	
	/**
	 * Get the number of vertices which are on a  certain layer
	 * 
	 * @param layerNum the layer number to get the vertex count from
	 * @return		   the number of vertices which are on this layer
	 */
	public int getVertexCount(int layerNum);
	
	/**
	 * Get the layer from the vertex
	 * 
	 * @param vertex the vertex to get its layer from
	 * @return       the layer number from this vertex
	 */
	public int getLayer(SugiyamaVertex vertex);
	
	/**
	 * Get all vertices from a certain layer.
	 * 
	 * @param layerNum the layer number to get all vertices from
	 * @return		   a list of all vertices which are on this layer
	 */
	public List<SugiyamaVertex> getLayer(int layerNum);
	
	/**
	 * Get all layers that contain vertices.
	 * 
	 * @return a list of lists of vertices which are on this layer
	 */
	public List<List<SugiyamaVertex>> getLayers();
}
