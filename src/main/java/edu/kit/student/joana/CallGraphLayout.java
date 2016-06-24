package edu.kit.student.joana;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.LayeredLayoutAlgorithm;

/**
 * Offers a layout for {@link CallGraph}.
 * Groups vertices representing the same Java-Method together.
 */
public class CallGraphLayout implements LayeredLayoutAlgorithm <CallGraph, JoanaVertex, JoanaEdge> {

	@Override
	public Settings getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void layout(CallGraph graph) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void layoutLayeredGraph(LayeredGraph<JoanaVertex, JoanaEdge> graph) {
		// TODO Auto-generated method stub
		
	}

}
