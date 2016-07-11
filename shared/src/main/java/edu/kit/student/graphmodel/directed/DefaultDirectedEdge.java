package edu.kit.student.graphmodel.directed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.OrthogonalEdgePath;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.serialize.SerializedEdge;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.IdGenerator;

public class DefaultDirectedEdge implements DirectedEdge {

	private Vertex target;
	private Vertex source;
	private GAnsProperty<String> name;
	private Integer id;
	private GAnsProperty<String> label;
	private OrthogonalEdgePath path;

	/**
	 * Constructor
	 * 
	 * @param name of the new edge
	 * @param label of the new edge
	 * @param id of the new edge
	 */
    public DefaultDirectedEdge(String name, String label) {       
        this.name = new GAnsProperty<String>("name", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = IdGenerator.getInstance().createId();
        this.path = new OrthogonalEdgePath();
    }
    
    /**
     * Constructor to set source and target
     * 
     * @param name
     * @param label
     * @param id
     * @param source
     * @param target
     */
    public DefaultDirectedEdge(String name, String label, Vertex source, Vertex target) {  
    	this(name,label);
        this.source = source;
        this.target = target;
    }
    
    /**
     * Set source and target vertex of this directed edge
     * 
     * @param source vertex
     * @param target vertex
     */
    public void setVertices(Vertex source, Vertex target) {
        //set source to first index and target to second
        this.source = source;
        this.target = target;
    }
	
	/**
	 * Returns the source vertex of this directed edge.
	 * 
	 * @return The vertex the edge is coming from.
	 */
	public Vertex getSource() {
	    return this.source;		
	}

	/**
	 * Returns the target vertex of this edge.
	 * 
	 * @return The vertex the edge is pointing at/going to.
	 */
	public Vertex getTarget() {
        return this.target;
	}

	@Override
	public List<Vertex> getVertices() {
		List<Vertex> vertices = new ArrayList<Vertex>();
		vertices.add(this.source);
		vertices.add(this.target);
	    return vertices;
	}

	@Override
	public String getName() {
		return name.getValue();
	}

	@Override
	public Integer getID() {
		return id;
	}

	@Override
	public String getLabel() {
		return label.getValue();
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
	    fga.addEdgeForAttribute(this, "name", this.name.toString());
	    fga.addEdgeForAttribute(this, "id", this.id);
	    fga.addEdgeForAttribute(this, "label", this.label.toString());

	    fga.addEdgeForAttribute(this, "sourceVertex", this.source.getID());
	    fga.addEdgeForAttribute(this, "targetVertex", this.target.getID());
	}

	@Override
	public OrthogonalEdgePath getPath() {
	    return path;
	}
	
	@Override
	public List<GAnsProperty<?>> getProperties() {
		LinkedList<GAnsProperty<?>> properties = new LinkedList<>();
		properties.add(name);
		properties.add(label);
		return properties;
	}
}
