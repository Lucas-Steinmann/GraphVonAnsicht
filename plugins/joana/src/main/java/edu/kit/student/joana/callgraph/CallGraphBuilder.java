package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaGraphModelBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder{

    JoanaGraphModelBuilder modelBuilder;
    String name;
    
    public CallGraphBuilder(JoanaGraphModelBuilder modelBuilder, String name) {
        this.modelBuilder = modelBuilder;
        this.name = name;
    }
    
	@Override
	public IEdgeBuilder getEdgeBuilder() {
		return new JoanaEdgeBuilder(this);
	}

	@Override
	public IVertexBuilder getVertexBuilder(String vertexID) {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public IGraphBuilder getGraphBuilder(String graphID) {
        return new MethodGraphBuilder(this.modelBuilder, this, graphID);
    }
	
	@Override
	public Graph build() {
		// TODO Auto-generated method stub
		return null;
	}

}
