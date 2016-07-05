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
        //else return methodgraphbuilder
        MethodGraphBuilder builder = new MethodGraphBuilder(graphId);
        methodBuilders.add(builder);
        return builder;
    }

    @Override
    public GraphModel build() {
        JoanaGraphModel model = new JoanaGraphModel();
        CallGraph callGraph = callBuilder.build();
        List<MethodGraph> methodGraphs = new LinkedList<>();
        for (MethodGraphBuilder b : methodBuilders) {
            methodGraphs.add(b.build());
        }
        model.setCallGraph(callGraph);
        model.setMethodGraphs(methodGraphs);
        workspace.setGraphModel(model);
        return model;
    }
}
