package edu.kit.student.joana.callgraph;

import edu.kit.student.parameter.Settings;
import edu.kit.student.sugiyama.LayeredLayoutAlgorithm;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;

/**
 * Offers a layout for {@link CallGraph}.
 * Groups vertices representing the same Java-Method together.
 */
public class CallGraphLayout implements LayeredLayoutAlgorithm<CallGraph> {
	private SugiyamaLayoutAlgorithm sugiyamaLayoutAlgorithm;

	public CallGraphLayout() {
		this.sugiyamaLayoutAlgorithm = new SugiyamaLayoutAlgorithm();
	}

	@Override
	public Settings getSettings() {
		return sugiyamaLayoutAlgorithm.getSettings();
	}

	@Override
	public void layout(CallGraph graph) {
		//TODO: call SugiyamaLayoutAlgorithm layout(graph) after refactoring
		sugiyamaLayoutAlgorithm.layout(graph);
		
	}

	@Override
	public void layoutLayeredGraph(CallGraph graph) {
		// TODO Auto-generated method stub
		
	}

}
