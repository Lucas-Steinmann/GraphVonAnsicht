package edu.kit.student.joana;

import java.util.ArrayList;
import java.util.LinkedList;
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

    /**
     * The CallGraph of this GraphModel. All further information can be retrieved from this Graph (e.g. MethodGraphs).
     */
	private CallGraph callgraph;
	
	/**
	 * Constructs a new JoanaGraphModel with the specified callgraph and the specified MethodGraphs
	 * @param callgraph
	 */
	public JoanaGraphModel(CallGraph callgraph) {
		this.callgraph = callgraph;
	}

	/**
	 * Returns all {@link MethodGraph} contained in the JoanaGraphModel.
	 * 
	 * @return A list of all the {@link MethodGraph} contained in the
	 *         JoanaGraphModel.
	 */
	public List<MethodGraph> getMethodGraphs() {
		return callgraph.getMethodgraphs();
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
	private void setCallGraph(CallGraph callgraph) {
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
		if (id.equals(callgraph.getID())) {
			return callgraph;
		}
		for (ViewableGraph graph : this.getMethodGraphs()) {
			if (graph.getID().compareTo(id) == 0) {
				return graph;
			}
		}
		return null;
	}

    @Override
    public ViewableGraph getParentGraph(ViewableGraph graph) {
        if (getMethodGraphs().contains(graph)) {
            return callgraph;
        } else {
            return null;
        }
    }

    @Override
    public List<MethodGraph> getChildGraphs(ViewableGraph graph) {
        if (graph.equals(callgraph)) {
            return this.getMethodGraphs();
        }
        return new LinkedList<>();
    }
}
