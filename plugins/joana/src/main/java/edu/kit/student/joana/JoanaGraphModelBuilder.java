package edu.kit.student.joana;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.joana.callgraph.CallGraphBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The JoanaGraphModelBuilder implements the {@link IGraphModelBuilder} and
 * creates a {@link JoanaGraphModel}.
 */
public class JoanaGraphModelBuilder implements IGraphModelBuilder {

    private JoanaWorkspace workspace;
	
	public JoanaGraphModelBuilder(JoanaWorkspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public IGraphBuilder getGraphBuilder(String graphId) {
	    //check if callgraphbuilder
	    if(graphId == "callgraph") {
	        return new CallGraphBuilder(this, graphId);
	    }
	    //else return methodgraphbuilder
		return new MethodGraphBuilder(this, graphId);
	}

	@Override
	public GraphModel build() {
		//TODO: vor dem Return die spezielle JoanaGraphModel-Instanz im workspace.setGraphModel(JoanaGraphModel model) setzen.
		return null;
	}

}
