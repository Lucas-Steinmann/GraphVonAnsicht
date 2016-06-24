package edu.kit.student.joana;

import edu.kit.student.plugin.LayoutAlgorithm;
import edu.kit.student.plugin.LayoutOption;

/**
 * A {@link LayoutOption} which is specific for {@link CallGraph}.
 */
public abstract class CallGraphLayoutOption extends LayoutOption {

	private CallGraph graph;
	private LayoutAlgorithm<CallGraph, JoanaVertex, JoanaEdge> layout;

	/**
	 * Sets the {@link CallGraph} that will be the target of the
	 * CallGraphLayoutOption.
	 * 
	 * @param graph
	 *            The {@link CallGraph} that will be the target of this
	 *            CallGraphLayoutOption.
	 */
	public void setGraph(CallGraph graph) {
	}

	/**
	 * Sets the LayoutAlgorithm that will be used to layout the set graph.
	 * 
	 * @param layout
	 *            The LayoutAlgorithm that will be used to layout the set graph.
	 */
	public void setLayout(LayoutAlgorithm<CallGraph, JoanaVertex, JoanaEdge> layout) {
		this.layout = layout;
	}

	@Override
	public void applyLayout() {
		layout.layout(graph);
	}
	
	@Override
	public void chooseLayout() {};
}
