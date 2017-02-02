package edu.kit.student.graphmodel.viewable;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;

public class GenericGraphModelBuilder implements IGraphModelBuilder {

	List<DirectedViewableGraphBuilder> graphBuilders;
	GenericWorkspace ws;
	
	public GenericGraphModelBuilder(GenericWorkspace ws) {
		this.ws = ws;
		graphBuilders = new LinkedList<>();
	}

	@Override
	public IGraphBuilder getGraphBuilder(String graphID) {
		DirectedViewableGraphBuilder builder = new DirectedViewableGraphBuilder(graphID);
		graphBuilders.add(builder);
		return builder;
	}
	
	private void addSubgraphsToModel(GenericGraphModel model, DirectedViewableGraphBuilder graphBuilder) {
		for (DirectedViewableGraphBuilder subgraphBuilder : graphBuilder.subraphBuilders) {
			model.addGraph(graphBuilder.graph, subgraphBuilder.graph);
			addSubgraphsToModel(model, subgraphBuilder);
		}
	}

	@Override
	public GenericGraphModel build() throws GraphBuilderException {
		GenericGraphModel model = new GenericGraphModel();
		for (DirectedViewableGraphBuilder builder : graphBuilders){
			DirectedViewableGraph graph = builder.build();
			model.addRootGraph(graph);
			addSubgraphsToModel(model, builder);
		}
		this.ws.setGraphModel(model);
		return model;
	}

}
