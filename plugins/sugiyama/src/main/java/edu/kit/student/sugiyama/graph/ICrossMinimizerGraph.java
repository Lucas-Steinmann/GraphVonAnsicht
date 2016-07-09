package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.DummyVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SupplementEdge;

import java.util.List;
import java.util.Set;

/**
 * A LayeredGraph which additionally defines functions that can be used to minimize the crossings in the sugiyama-layout.
 */
public interface ICrossMinimizerGraph extends LayeredGraph<ISugiyamaVertex, ISugiyamaEdge> {

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
	public void swapVertices(ISugiyamaVertex first, ISugiyamaVertex second);
	
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
	public int getLayer(ISugiyamaVertex vertex);
	
	/**
	 * Get all vertices from a certain layer.
	 * 
	 * @param layerNum the layer number to get all vertices from
	 * @return		   a list of all vertices which are on this layer
	 */
	public List<ISugiyamaVertex> getLayer(int layerNum);
	
	/**
	 * Get all layers that contain vertices.
	 * 
	 * @return a list of lists of vertices which are on this layer
	 */
	public List<List<ISugiyamaVertex>> getLayers();
	
	/**
	 * Creates a new DummyVertex with the specified name, label and layer number.
	 * 
	 * @param name name of the new dummy vertex
	 * @param label label of the new dummy vertex
	 * @param layer layer number of the new dummy vertex
	 * @return a new dummy vertex
	 */
	public DummyVertex createDummy(String name, String label, int layer);
	
	/**
	 * Creates a new supplement edge with the specified name and label.
	 * @param name name of the new supplement edge
	 * @param label label of the new supplement edge
	 * @return a new supplement edge
	 */
	public SupplementEdge createSupplementEdge(String name, String label);

	public Set<SugiyamaGraph.SupplementPath> getSupplementPaths();
}
