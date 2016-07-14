package edu.kit.student.joana;

import java.util.ArrayList;
import java.util.List;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;

/**
 * A Joana specific {@link GraphModel}. It can only contain {@link MethodGraph}
 * and {@link CallGraph}.
 */
public class JoanaGraphModel extends GraphModel {

	private List<MethodGraph> methodgraphs;
	private CallGraph callgraph;

	/**
	 * Returns all {@link MethodGraph} contained in the JoanaGraphModel.
	 * 
	 * @return A list of all the {@link MethodGraph} contained in the
	 *         JoanaGraphModel.
	 */
	public List<MethodGraph> getMethodGraphs() {
		return methodgraphs;
	}

	/**
	 * Sets the {@link MethodGraph} objects in the JoanaGraphModel.
	 * 
	 * @param methodgraphs
	 *            The {@link MethodGraph} objects that will be set in the
	 *            JoanaGraphModel.
	 */
	public void setMethodGraphs(List<MethodGraph> methodgraphs) {
		this.methodgraphs = methodgraphs;
	}

	/**
	 * Returns all {@link CallGraph} contained in the JoanaGraphModel.
	 * 
	 * @return A list of all the {@link CallGraph} contained in the
	 *         JoanaGraphModel.
	 */
	public CallGraph getCallGraph() {
		return callgraph;
	}

	/**
	 * Sets the {@link CallGraph} in the JoanaGraphModel.
	 * 
	 * @param callgraph
	 *            The {@link CallGraph} that will be set in the JoanaGraphModel.
	 */
	public void setCallGraph(CallGraph callgraph) {
		this.callgraph = callgraph;
	}

	@Override
	public List<CallGraph> getRootGraphs() {
		List<CallGraph> root = new ArrayList<>();
		root.add(this.callgraph);
		return root;
	}

	@Override
	public ViewableGraph getGraphFromId(Integer id) {
		return this.getGraphFromId(getRootGraphs(), id);
	}
	
	private ViewableGraph getGraphFromId(List<? extends ViewableGraph> graphs, Integer id) {
		for (ViewableGraph graph : graphs) {
			if (graph.getID().compareTo(id) == 0) {
				return graph;
			}

			ViewableGraph tmp = getGraphFromId(graph.getChildGraphs(), id);
			if (tmp != null) {
				return tmp;
			}
		}

		return null;
	}
}
