package edu.kit.student.joana.methodgraph;

import java.util.Set;
import java.util.TreeSet;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Graph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraphModelBuilder;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertexBuilder;
import edu.kit.student.joana.callgraph.CallGraphBuilder;

/**
 * The MethodGraphBuilder is a {@link IGraphBuilder}, specifically for building
 * {@link MethodGraph}.
 */
public class MethodGraphBuilder implements IGraphBuilder {

    JoanaGraphModelBuilder modelBuilder;
    CallGraphBuilder callGraphBuilder = null;
    String name;
    Set<JoanaVertex> vertices = new TreeSet<JoanaVertex>();
    
    /**
     * Constructor for methodgraphBuilder which is not created by a callgraphBuilder.
     * 
     * @param modelBuilder
     * @param name
     */
    public MethodGraphBuilder(JoanaGraphModelBuilder modelBuilder, String name) {
        this.modelBuilder = modelBuilder;
        this.name = name;
    }
    
    /**
     * Constructor for methodgraphBuilder which is created by a callgraphBuilder.
     * 
     * @param modelBuilder
     * @param name
     */
    public MethodGraphBuilder(JoanaGraphModelBuilder modelBuilder, CallGraphBuilder callGraphBuilder, String name) {
        this.modelBuilder = modelBuilder;
        this.callGraphBuilder = callGraphBuilder;
        this.name = name;
    }
    
	@Override
	public IEdgeBuilder getEdgeBuilder() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public IVertexBuilder getVertexBuilder(String vertexId) {
		return new JoanaVertexBuilder(this, vertexId);
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

	@Override
	public Graph build() {
	    //TODO: Get fieldaccess
	    
	    //check if methodgraph belongs to a callgraph
	    if (this.callGraphBuilder != null) {
	        //TODO: set methodgraph to callgraph as compoundvertex
	    }
		return null;
	    
	    //TODO set methodgraph to model
	    //this.modelBuilder.addMethodGraph(methodGraph);
	    
	    
		//return methodGraph;
	}

}
