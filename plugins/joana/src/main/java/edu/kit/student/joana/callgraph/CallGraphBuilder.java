package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaEdgeBuilder;
import edu.kit.student.joana.JoanaVertexBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The CallGraphBuilder implements an {@link IGraphBuilder} and builds 
 * one {@link CallGraph}.
 */
public class CallGraphBuilder implements IGraphBuilder {

    String name;
    
    public CallGraphBuilder(String name) {
        this.name = name;
    }
    
	@Override
	public IEdgeBuilder getEdgeBuilder() {
		return new JoanaEdgeBuilder(this);
	}

	@Override
	public IVertexBuilder getVertexBuilder(String vertexID) {
	    return new JoanaVertexBuilder(vertexID);
	}

    @Override
    public IGraphBuilder getGraphBuilder(String graphID) {
        return new MethodGraphBuilder(graphID);
    }
	
	public CallGraph build() {
		return null;
	}

}
