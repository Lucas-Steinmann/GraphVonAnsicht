package edu.kit.student.joana;

import edu.kit.student.graphmodel.CompoundVertex;
import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.joana.methodgraph.MethodGraph;

public class JoanaCompoundVertex extends JoanaVertex implements CompoundVertex {
	
	private MethodGraph graph;

	public JoanaCompoundVertex(String name, String label, MethodGraph graph) {
		super(name, label, graph.getEntryVertex().getNodeKind());
		this.graph = graph;
	}

	@Override
	public MethodGraph getGraph() {
		return this.graph;
	}

	@Override
	public Vertex getConnectedVertex(Edge edge) {
		if(edge.getVertices().contains(this)) {
			return this.graph.getEntryVertex();
		}
		return null;
	}
	
	@Override
	public String getLabel() {
		return graph.getEntryVertex().getLabel();
	}
	
	@Override
	public void setProperties(Kind nodeKind, String nodeSource, Integer nodeProc, String nodeOperation,
			String nodeBcName, Integer nodeBCIndex, Integer nodeSr, Integer nodeSc, Integer nodeEr, Integer nodeEc) {
		graph.getEntryVertex().setProperties(nodeKind, nodeSource, nodeProc, nodeOperation, nodeBcName, nodeBCIndex, nodeSr, nodeSc, nodeEr, nodeEc);
	}
	
	@Override
	public Kind getNodeKind() {
		return graph.getEntryVertex().getNodeKind();
	}

	@Override
	public String getNodeSource() {
		return graph.getEntryVertex().getNodeSource();
	}

	@Override
	public Integer getNodeProc() {
		return graph.getEntryVertex().getNodeProc();
	}

	@Override
	public String getNodeOperation() {
		return graph.getEntryVertex().getNodeOperation();
	}

	@Override
	public String getNodeBcName() {
		return graph.getEntryVertex().getNodeBcName();
	}

	@Override
	public Integer getNodeBcIndex() {
		return graph.getEntryVertex().getNodeBcIndex();
	}

	@Override
	public Integer getNodeSr() {
		return graph.getEntryVertex().getNodeSr();
	}

	@Override
	public Integer getNodeSc() {
		return graph.getEntryVertex().getNodeSc();
	}

	@Override
	public Integer getNodeEr() {
		return graph.getEntryVertex().getNodeEr();
	}

	@Override
	public Integer getNodeEc() {
		return graph.getEntryVertex().getNodeEc();
	}

}
