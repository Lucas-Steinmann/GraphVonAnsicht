package edu.kit.student.joana;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.callgraph.CallGraphBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The JoanaGraphModelBuilder implements the {@link IGraphModelBuilder} and
 * creates a {@link JoanaGraphModel}.
 */
public class JoanaGraphModelBuilder implements IGraphModelBuilder {

    private JoanaWorkspace workspace;
    CallGraphBuilder callBuilder;
    List<MethodGraphBuilder> methodBuilders = new LinkedList<MethodGraphBuilder>();
    
    public JoanaGraphModelBuilder(JoanaWorkspace workspace) {
        this.workspace = workspace;
    }

    @Override
    public IGraphBuilder getGraphBuilder(String graphId) {
        //check if callgraphbuilder
        if (graphId.equals("callgraph")) {
            callBuilder = new CallGraphBuilder(graphId);
            return callBuilder;
        }
        return null;
    }

    @Override
    public GraphModel build() throws Exception {
        CallGraph callGraph = callBuilder.build();
        JoanaGraphModel model = new JoanaGraphModel(callGraph);
        workspace.setGraphModel(model);
        return model;
    }
}
