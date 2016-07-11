package edu.kit.student.joana.callgraph;

import java.util.HashMap;
import java.util.HashSet;

import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.LayeredLayoutAlgorithm;
import edu.kit.student.sugiyama.RelativeLayerConstraint;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.CrossMinimizer;
import edu.kit.student.sugiyama.steps.CycleRemover;
import edu.kit.student.sugiyama.steps.EdgeDrawer;
import edu.kit.student.sugiyama.steps.ICrossMinimizer;
import edu.kit.student.sugiyama.steps.ICycleRemover;
import edu.kit.student.sugiyama.steps.IEdgeDrawer;
import edu.kit.student.sugiyama.steps.IVertexPositioner;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.sugiyama.steps.VertexPositioner;

/**
 * Offers a layout for {@link CallGraph}.
 * Groups vertices representing the same Java-Method together.
 */
public class CallGraphLayout implements LayeredLayoutAlgorithm<CallGraph> {

	@Override
	public Settings getSettings() {
		return new Settings(new HashMap<>());
	}

	@Override
	public void layout(CallGraph graph) {
		//TODO: call SugiyamaLayoutAlgorithm layout(graph) after refactoring
		SugiyamaGraph wrappedGraph = new SugiyamaGraph(graph);
		
		LayerAssigner assigner = new LayerAssigner();
		assigner.addConstraints(new HashSet<RelativeLayerConstraint>());

		ICycleRemover remover = new CycleRemover();
		ICrossMinimizer minimizer = new CrossMinimizer();
		IVertexPositioner positioner = new VertexPositioner();
		IEdgeDrawer drawer = new EdgeDrawer();
		remover.removeCycles(wrappedGraph);
		assigner.assignLayers(wrappedGraph);
		minimizer.minimizeCrossings(wrappedGraph);
		positioner.positionVertices(wrappedGraph);
		drawer.drawEdges(wrappedGraph);
		
	}

	@Override
	public void layoutLayeredGraph(CallGraph graph) {
		// TODO Auto-generated method stub
		
	}

}
