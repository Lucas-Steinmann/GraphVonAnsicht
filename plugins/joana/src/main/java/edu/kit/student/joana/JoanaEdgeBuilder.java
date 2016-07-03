package edu.kit.student.joana;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.joana.callgraph.CallGraphBuilder;
import edu.kit.student.joana.methodgraph.MethodGraphBuilder;

/**
 * The JoanaEdgeBuilder is a {@link IEdgeBuilder}, specifically for building
 * {@link JoanaEdge}.
 */
public class JoanaEdgeBuilder implements IEdgeBuilder {

    boolean edgeForCallGraph;
    CallGraphBuilder callGraph = null;
    MethodGraphBuilder methodGraph = null;
    
    public JoanaEdgeBuilder(CallGraphBuilder graphBuilder) {
        this.callGraph = graphBuilder;
        edgeForCallGraph = true;
    }
    
    public JoanaEdgeBuilder(MethodGraphBuilder graphBuilder) {
        this.methodGraph = graphBuilder;
        edgeForCallGraph = false;
    }
    
	@Override
	public void setID(String id) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setDirection(String direction) {
		// TODO Auto-generated method stub
	}

	@Override
	public void newEdge(String source, String target) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addData(String keyname, String value) {
		// TODO Auto-generated method stub
	}

	@Override
	public Edge build() {
		// TODO Auto-generated method stub
		return null;
	}
}
