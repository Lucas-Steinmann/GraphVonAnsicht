package edu.kit.student.graphmodel.viewable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.GraphBuilderException.BuilderType;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;

public class DirectedEdgeBuilder implements IEdgeBuilder {
	
	private String id;
    private String source;
	private String target;
    private Map<String, String> edgeData = new HashMap<>();

	public DirectedEdgeBuilder(String source, String target) {
		this.source = source;
		this.target = target;
	}

    public String getId() {
		return id;
	}

	public String getSource() {
		return source;
	}

	public String getTarget() {
		return target;
	}

	public Map<String, String> getEdgeData() {
		return edgeData;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	public void setEndpoints(String source, String target) {
		this.source = source;
		this.target = target;
	}

	@Override
	public void addData(String key, String value) {
		edgeData.put(key, value);
	}

    public DefaultDirectedEdge<DefaultVertex> build(Set<DefaultVertex> vertexPool) throws GraphBuilderException {
    	DefaultVertex s = null;
    	DefaultVertex t = null;
    	for (DefaultVertex v : vertexPool) {
    		if (v.getName().toString().equals(source))
    			s = v;
    		if(v.getName().toString().equals(target))
    			t = v;
    	}
    	if (s == null || t == null) {
    		throw new GraphBuilderException(BuilderType.EDGE, "Cannot add edge to a graph without the vertex being present");
    	}
		DefaultDirectedEdge<DefaultVertex> edge = new DefaultDirectedEdge<>(id, id, s, t, edgeData);
    	return edge;
    }
}
