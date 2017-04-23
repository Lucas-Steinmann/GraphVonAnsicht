package edu.kit.student.joana;

import java.util.*;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;

/**
 * A Joana specific {@link GraphModel}. It can only contain {@link MethodGraph}
 * and {@link CallGraph}.
 */
public class JoanaGraphModel extends GraphModel implements  JoanaObjectPool {

    /**
     * The CallGraph of this GraphModel. All further information can be retrieved from this Graph (e.g. MethodGraphs).
     */
	private CallGraph callgraph;

	/**
	 * A mapping from the names of all Java source files occurring in this JoanaGraphModel to the {@link JavaSource} objects.
	 */
	private Set<JavaSource> javaSources = new HashSet<>();

	/**
	 * Constructs a new JoanaGraphModel with the specified callgraph and the specified MethodGraphs
	 * @param callgraph the {@link CallGraph} of this model
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
		return callgraph.getMethodGraphs();
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


	public Set<JavaSource> getJavaSources() {
		return new HashSet<>(this.javaSources);
	}

	@Override
	public JavaSource getJavaSource(String sourceName) {
	    Optional<JavaSource> sourceOptional = this.javaSources.stream().filter(js -> js.getFileName().equals(sourceName)).findFirst();
	    if (sourceOptional.isPresent()) {
	    	return sourceOptional.get();
		}
		JavaSource source = new JavaSource(sourceName);
	    this.javaSources.add(source);
	    return source;
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
