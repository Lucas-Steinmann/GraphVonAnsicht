package edu.kit.student.joana;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.callgraph.CallGraphBuilder;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

import java.util.LinkedList;
import java.util.List;

/**
 * The JoanaGraphModelBuilder implements the {@link IGraphModelBuilder} and
 * creates a {@link JoanaGraphModel}.
 */
public class JoanaGraphModelBuilder implements IGraphModelBuilder {

    private JoanaWorkspace workspace;
    private List<MethodGraph> methodgraphs = new LinkedList<MethodGraph>();
    private CallGraph callgraph;
	
	public JoanaGraphModelBuilder(JoanaWorkspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public IGraphBuilder getGraphBuilder(String graphId) {
	    //check if callgraphbuilder
	    if(graphId == "callgraph") {
	        return new CallGraphBuilder(graphId);
	    }
	    //else return methodgraphbuilder
		return new MethodGraphBuilder(graphId);
	}

	@Override
	public GraphModel build() {
	    JoanaGraphModel model = new JoanaGraphModel();
	    model.setCallGraph(callgraph);
	    model.setMethodGraphs(methodgraphs);
	    workspace.setGraphModel(model);
		return model;
	}
	
	/**
	 * adds CallGraph to CallgraphBuilder.
	 * 
	 * @param callgraph
	 */
	public void setCallGraph(CallGraph callgraph) {
	    this.callgraph = callgraph;
	}
	
	/**
     * add a methodGraph.
     * 
     * @param callgraph
     */
    public void addMethodGraph(MethodGraph methodgraph) {
        this.methodgraphs.add(methodgraph);
    }

}
