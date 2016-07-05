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
    JoanaVertex source = null;
    JoanaVertex target = null;
    String edgeKind = "";
    String name = "";
    
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
	    this.name = id;
	}


	@Override
	public void newEdge(String source, String target) {
		// TODO: search in methodgraphbuilder or callgraphbuilder the source and target node
	}

	@Override
	public void addData(String keyname, String value) {
		if (keyname == "edgeKind") {
		    this.edgeKind = value;
		}
	}

	public JoanaEdge build() {
		// TODO build edge and set to graphbuilder
		return null;
	}
}
