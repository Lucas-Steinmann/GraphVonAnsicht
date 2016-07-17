
package edu.kit.student.joana.methodgraph;


import edu.kit.student.parameter.*;
import edu.kit.student.sugiyama.LayeredLayoutAlgorithm;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Implements hierarchical layout with layers for {@link MethodGraph}.
 * This graph contains field access subgraphs.
 */
public class MethodGraphLayout implements LayeredLayoutAlgorithm<MethodGraph> {
	private SugiyamaLayoutAlgorithm sugiyamaLayoutAlgorithm;

	public MethodGraphLayout() {
		this.sugiyamaLayoutAlgorithm = new SugiyamaLayoutAlgorithm();
	}

	@Override
	public Settings getSettings() {
		Settings sugiyamaSettings = sugiyamaLayoutAlgorithm.getSettings();
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
		sugiyamaLayoutAlgorithm.layout(graph);
	}

	@Override
	public void layoutLayeredGraph(MethodGraph graph) {
		// TODO Auto-generated method stub
		
	}

}
