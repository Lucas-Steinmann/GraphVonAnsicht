package edu.kit.student.graphmodel.viewable;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;

public class GenericGraphModelBuilder implements IGraphModelBuilder {

	List<DirectedViewableGraphBuilder> builders;
	GenericWorkspace ws;
	
	public GenericGraphModelBuilder(GenericWorkspace ws) {
		this.ws = ws;
	}

	@Override
	public IGraphBuilder getGraphBuilder(String graphID) {
		builders = new LinkedList<>();
		DirectedViewableGraphBuilder builder = new DirectedViewableGraphBuilder(graphID);
		builders.add(builder);
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
		for (DirectedViewableGraphBuilder builder : builders){
			DirectedViewableGraph graph = builder.build();
			model.addRootGraph(graph);
			addSubgraphsToModel(model, builder);
		}
		this.ws.setGraphModel(model);
		return model;
	}

}
