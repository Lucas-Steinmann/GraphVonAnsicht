package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.steps.ICrossMinimizer;
import edu.kit.student.sugiyama.steps.ICycleRemover;
import edu.kit.student.sugiyama.steps.IEdgeDrawer;
import edu.kit.student.sugiyama.steps.ILayerAssigner;
import edu.kit.student.sugiyama.steps.IVertexPositioner;

/**
 * This class supports a customizable implementation of the Sugiyama-framework.
 * The single stages of the framework can be chosen individually.
 * Additionally this class tries to follow the given constraints, if possible.
 * 
 * 
 * @param <G> the type of the graph the layout should be applied
 * @param <V> the type of the vertices the graph contains
 * @param <E> the type of the edges the graph contains
 */
public class SugiyamaLayoutAlgorithm<G extends DirectedGraph<V, E>, V extends Vertex, E extends DirectedEdge<V>> 
	implements LayeredLayoutAlgorithm<G, V, E> {
	
	
	/**
	 * Sets the algorithm to remove all cycles for a graph to layout used when applying the layout
	 * @param remover the algorithm
	 */
	public void setCycleRemover(ICycleRemover remover) {}
	
	/**
	 * Sets the algorithm for layer assigning used when applying the layout
	 * @param assigner the layer assign algorithm
	 */
	public void setLayerAssigner(ILayerAssigner assigner) {}
	
	/**
	 * Sets the algorithm for cross minimization used when applying the layout
	 * @param minimizer the cross minimization algorithm
	 */
	public void setCrossMinimizer(ICrossMinimizer minimizer) {}
	
	/**
	 * Sets the algorithm for vertex positioning used when applying the layout
	 * @param positioner the positioning algorithm
	 */
	public void setVertexPositioner(IVertexPositioner positioner) {}
	
	/**
	 * Sets the algorithm for edge drawing used when applying the layout
	 * @param drawer the edge drawing algorithm
	 */
	public void setEdgeDrawer(IEdgeDrawer drawer) {}
	
	/**
	 * Adds an {@link AbsoluteLayerConstraint} to the set of constraints which should be followed.
	 * @param constraint the constraint to follow
	 */
	public void addAbsoluteLayerConstraint(AbsoluteLayerConstraint constraint) {}
	
	/**
	 * Adds an {@link RelativeLayerConstraint} to the set of constraints which should be followed.
	 * @param constraint the constraint to follow
	 */
	public void addRelativeLayerConstraint(RelativeLayerConstraint constraint) {}

	@Override
	public Settings getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void layout(G graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutLayeredGraph(LayeredGraph<V, E> graph) {
		// TODO Auto-generated method stub
		
	}

}
