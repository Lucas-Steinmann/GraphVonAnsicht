package edu.kit.student.sugiyama.graph;

import edu.kit.student.graphmodel.Vertex;

/**
 * This interfaces adds functionality to an implementing class for the use in the last three steps in 
 * Sugiyama Framework: CrossMinimizer, VertexPositioner and EdgeDrawer
 * 
 * An ISugiyamaVertex can be a SugiyamaVertex or a dummy vertex. The SugiyamaVertex still got the functionality of this interface, but the DummyVertex need these,
 * in order to treat both the same way in the last three steps.
 */
public interface ISugiyamaVertex extends Vertex{

	/**
	 * Returns the layer of this vertex represented by a number.
	 * 
	 * @return the layer of this vertex
	 */
	public int getLayer();
	
	public void setX(double x);
	
	public void setY(double y);

	public void setLayer(int layerNum);

	public boolean isDummy();

	public Vertex getVertex();
	
	@Override
	public boolean equals(Object o);
}
