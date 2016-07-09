package edu.kit.student.joana.methodgraph;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.parameter.IntegerParameter;
import edu.kit.student.parameter.MultipleChoiceParameter;
import edu.kit.student.parameter.Parameter;
import edu.kit.student.parameter.Settings;
import edu.kit.student.parameter.StringParameter;
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
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayeredLayoutAlgorithm<MethodGraph, JoanaVertex, JoanaEdge> {

	@Override
	public Settings getSettings() {
		IntegerParameter p1 = new IntegerParameter("Max-Layer-Count", 20, 1, 100);
		IntegerParameter p2 = new IntegerParameter("Min-Layer-Count", 5, 1, 100);
		StringParameter p3 = new StringParameter("StringParameter", "Hallo");
		List<String> options = new ArrayList<String>();
		options.add("option1");
		options.add("option2");
		options.add("option3");
		MultipleChoiceParameter p4 = new MultipleChoiceParameter("Multiple-Choice-Test", options, 2);
		HashMap<String, Parameter<?,?>> parameter = new HashMap<String, Parameter<?,?>>();
		parameter.put(p1.getName(), p1);
		parameter.put(p2.getName(), p2);
		parameter.put(p3.getName(), p3);
		parameter.put(p4.getName(), p4);
		Settings  s = new Settings(parameter);
		return s;
	}

	/**
	 * Layouts a single {@link MethodGraph} with the configured settings.
	 * 
	 * @param graph The {@link MethodGraph} to layout.
	 */
	public void layout(MethodGraph graph) {
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
	public void layoutLayeredGraph(LayeredGraph<JoanaVertex, JoanaEdge> graph) {
		// TODO Auto-generated method stub
		
	}
}