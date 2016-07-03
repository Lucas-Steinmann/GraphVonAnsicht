package edu.kit.student.joana;

import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.callgraph.CallGraphBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The JoanaVertexBuilder implements an {@link IVertexBuilder} and
 * creates a {@link JoanaVertex}.
 */
public class JoanaVertexBuilder implements IVertexBuilder {
    
    boolean vertexForCallGraph;
    CallGraphBuilder callGraph = null;
    MethodGraphBuilder methodGraph = null;
    String id;
    
    public JoanaVertexBuilder(CallGraphBuilder graphBuilder, String id) {
        this.callGraph = graphBuilder;
        vertexForCallGraph = true;
        this.id = id;
    }
    
    public JoanaVertexBuilder(MethodGraphBuilder graphBuilder, String id) {
        this.methodGraph = graphBuilder;
        vertexForCallGraph = false;
        this.id = id;
    }
    
	@Override
	public void addData(String value, String keyname) {
		// TODO Auto-generated method stub
	    //TODO parse data and set
	}

	@Override
	public Vertex build() {
		//TODO build vertex
		return null;
	}

	@Override
	public void setID(String id) {
		// TODO Auto-generated method stub
		
	}

}
