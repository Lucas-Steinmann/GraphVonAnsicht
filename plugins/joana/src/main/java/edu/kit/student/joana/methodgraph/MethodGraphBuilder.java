package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.JoanaGraphModelBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertexBuilder;
import edu.kit.student.joana.callgraph.CallGraphBuilder;

import java.util.Set;
import java.util.TreeSet;

/**
 * The MethodGraphBuilder is a {@link IGraphBuilder}, specifically for building
 * {@link MethodGraph}.
 */
public class MethodGraphBuilder implements IGraphBuilder {

    String name;
    Set<JoanaVertex> vertices = new TreeSet<JoanaVertex>();
    
    /**
     * Constructor for methodgraphBuilder which is created by a callgraphBuilder.
     */
    public MethodGraphBuilder(String name) {
        this.name = name;
    }
    
	@Override
	public IEdgeBuilder getEdgeBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVertexBuilder getVertexBuilder(String vertexId) {
		return new JoanaVertexBuilder(vertexId);
	}
	
    @Override
    public IGraphBuilder getGraphBuilder(String graphID) {
        //is not allowed to happen
        //TODO: throw exception?
        return null;
    }
    
    /**
     * Adds an vertex to MethodGraphBuilder
     * 
     * @param vertex
     */
    public void addVertex(JoanaVertex vertex) {
        this.vertices.add(vertex);
    }

	public MethodGraph build() {
	    //TODO: Get fieldaccess
	    
	    //check if methodgraph belongs to a callgraph
	    //TODO set methodgraph to model
	    //this.modelBuilder.addMethodGraph(methodGraph);
	    
	    
		//return methodGraph;
        return null;
	}
}
