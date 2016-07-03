package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaGraphModelBuilder;

/**
 * The MethodGraphBuilder is a {@link IGraphBuilder}, specifically for building
 * {@link MethodGraph}.
 */
public class MethodGraphBuilder implements IGraphBuilder {

    JoanaGraphModelBuilder modelBuilder;
    String name;
    
    public MethodGraphBuilder(JoanaGraphModelBuilder modelBuilder, String name) {
        this.modelBuilder = modelBuilder;
        this.name = name;
    }
    
	@Override
	public IEdgeBuilder getEdgeBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVertexBuilder getVertexBuilder(String vertexID) {
		// TODO Auto-generated method stub
		return null;
	}
	
    @Override
    public IGraphBuilder getGraphBuilder(String graphID) {
        // TODO Auto-generated method stub
        return null;
    }

	@Override
	public Graph build() {
		// TODO Auto-generated method stub
		return null;
	}

}
