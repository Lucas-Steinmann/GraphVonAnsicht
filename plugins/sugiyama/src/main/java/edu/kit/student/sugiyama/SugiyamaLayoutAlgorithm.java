package edu.kit.student.sugiyama;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.*;

import java.util.HashSet;
import java.util.Set;

/**
 * This class supports a customizable implementation of the Sugiyama-framework.
 * The single stages of the framework can be chosen individually.
 * Additionally this class tries to follow the given constraints, if possible.
 */
public class SugiyamaLayoutAlgorithm 
	implements LayeredLayoutAlgorithm<DirectedGraph<Vertex, DirectedEdge<Vertex>>, Vertex, DirectedEdge<Vertex>> {
	private ICycleRemover remover;
	private ILayerAssigner assigner;
	private ICrossMinimizer minimizer;
	private IVertexPositioner positioner;
	private IEdgeDrawer drawer;
	private Set<RelativeLayerConstraint> relativeLayerConstraints;
	private Set<AbsoluteLayerConstraint> absoluteLayerConstraints;

	/**
	 * Creates a SugiyamaLayoutAlgorithm
	 */
	public SugiyamaLayoutAlgorithm() {
		relativeLayerConstraints = new HashSet<>();
		absoluteLayerConstraints = new HashSet<>();
	}
	//TODO check if it would be wise to set the first steps in the constructor in order to avoid nullpointer exceptions
	//they can still be replaced in the setter
	

	/**
	 * Sets the algorithm to remove all cycles for a graph to layout used when applying the layout
	 * @param remover the algorithm
	 */
	public void setCycleRemover(ICycleRemover remover) {
		this.remover = remover;
	}
	
	/**
	 * Sets the algorithm for layer assigning used when applying the layout
	 * @param assigner the layer assign algorithm
	 */
	public void setLayerAssigner(ILayerAssigner assigner) {
		this.assigner = assigner;
	}
	
	/**
	 * Sets the algorithm for cross minimization used when applying the layout
	 * @param minimizer the cross minimization algorithm
	 */
	public void setCrossMinimizer(ICrossMinimizer minimizer) {
		this.minimizer = minimizer;
	}
	
	/**
	 * Sets the algorithm for vertex positioning used when applying the layout
	 * @param positioner the positioning algorithm
	 */
	public void setVertexPositioner(IVertexPositioner positioner) {
		this.positioner = positioner;
	}
	
	/**
	 * Sets the algorithm for edge drawing used when applying the layout
	 * @param drawer the edge drawing algorithm
	 */
	public void setEdgeDrawer(IEdgeDrawer drawer) {
		this.drawer = drawer;
	}
	
	/**
	 * Adds an {@link AbsoluteLayerConstraint} to the set of constraints which should be followed.
	 * @param constraint the constraint to follow
	 */
	public void addAbsoluteLayerConstraint(AbsoluteLayerConstraint constraint) {
		absoluteLayerConstraints.add(constraint);
	}
	
	/**
	 * Adds an {@link RelativeLayerConstraint} to the set of constraints which should be followed.
	 * @param constraint the constraint to follow
	 */
	public void addRelativeLayerConstraint(RelativeLayerConstraint constraint) {
		relativeLayerConstraints.add(constraint);
	}

	@Override
	public Settings getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void layout(DirectedGraph<Vertex, DirectedEdge<Vertex>> graph) {
		SugiyamaGraph wrappedGraph = new SugiyamaGraph(graph);
		assigner.addConstraints(relativeLayerConstraints);

        remover.removeCycles(wrappedGraph);
		assigner.assignLayers(wrappedGraph);
		minimizer.minimizeCrossings(wrappedGraph);
		positioner.positionVertices(wrappedGraph);
		drawer.drawEdges(wrappedGraph);
    }

    @Override
    public void layoutLayeredGraph(LayeredGraph<Vertex, DirectedEdge<Vertex>> graph) {
		SugiyamaGraph wrappedGraph = new SugiyamaGraph(graph);
		assigner.addConstraints(relativeLayerConstraints);

		remover.removeCycles(wrappedGraph);
		assigner.assignLayers(wrappedGraph);
		minimizer.minimizeCrossings(wrappedGraph);
		positioner.positionVertices(wrappedGraph);
		drawer.drawEdges(wrappedGraph);
    }

	public void layout(DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> graph) {
		SugiyamaGraph wrappedGraph = new SugiyamaGraph(graph);
		assigner.addConstraints(relativeLayerConstraints);

		remover.removeCycles(wrappedGraph);
		assigner.assignLayers(wrappedGraph);
		minimizer.minimizeCrossings(wrappedGraph);
		positioner.positionVertices(wrappedGraph);
		drawer.drawEdges(wrappedGraph);
	}
}
