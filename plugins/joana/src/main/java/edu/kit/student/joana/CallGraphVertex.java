package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.objectproperty.GAnsProperty;

public class CallGraphVertex extends JoanaVertex {
	
    private MethodGraph methodGraph;

	public CallGraphVertex(String name, String label, MethodGraph graph) {
		super(name, label, graph.getEntryVertex().getNodeKind());
		this.methodGraph = graph;
	}

	public MethodGraph getGraph() {
		return this.methodGraph;
	}

	@Override
	public String getLabel() {
		return methodGraph.getEntryVertex().getLabel();
	}
	
	@Override
	public void setProperties(Kind nodeKind, String nodeSource, Integer nodeProc, String nodeOperation,
			String nodeBcName, Integer nodeBCIndex, Integer nodeSr, Integer nodeSc, Integer nodeEr, Integer nodeEc) {
	    //TODO: Replace setProperties with constructor, as all arguments should not change over time
	}
	
	@Override
	public Kind getNodeKind() {
		return methodGraph.getEntryVertex().getNodeKind();
	}

	@Override
	public String getNodeSource() {
		return methodGraph.getEntryVertex().getNodeSource();
	}

	@Override
	public Integer getNodeProc() {
		return methodGraph.getEntryVertex().getNodeProc();
	}

	@Override
	public String getNodeOperation() {
		return methodGraph.getEntryVertex().getNodeOperation();
	}

	@Override
	public String getNodeBcName() {
		return methodGraph.getEntryVertex().getNodeBcName();
	}

	@Override
	public Integer getNodeBcIndex() {
		return methodGraph.getEntryVertex().getNodeBcIndex();
	}

	@Override
	public Integer getNodeSr() {
		return methodGraph.getEntryVertex().getNodeSr();
	}

	@Override
	public Integer getNodeSc() {
		return methodGraph.getEntryVertex().getNodeSc();
	}

	@Override
	public Integer getNodeEr() {
		return methodGraph.getEntryVertex().getNodeEr();
	}

	@Override
	public Integer getNodeEc() {
		return methodGraph.getEntryVertex().getNodeEc();
	}

	@Override
    public int getLink() {
	    return methodGraph.getID();
    }

	@Override
    public List<GAnsProperty<?>> getProperties() {
	    // TODO: Add own properties like e.g. call count
        return super.getProperties();
    }
}

