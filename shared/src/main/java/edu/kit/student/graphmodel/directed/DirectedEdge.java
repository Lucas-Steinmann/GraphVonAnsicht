package edu.kit.student.graphmodel.directed;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.OrthogonalEdgePath;
import edu.kit.student.graphmodel.SerializedEdge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.objectproperty.GAnsProperty;

import java.util.*;

/**
 * A {@link DirectedEdge} is an edge that has one source and one target vertex.
 * The direction of the edge is specified.
 */
public class DirectedEdge<V extends Vertex> implements Edge<V> {

	private V target;
	private V source;
	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private GAnsProperty<String> label;

	/**
	 * Constructor
	 * 
	 * @param name of the new edge
	 * @param label of the new edge
	 * @param id of the new edge
	 */
    public DirectedEdge(String name, String label, Integer id) {       
        this.name = new GAnsProperty<String>("graphName", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = new GAnsProperty<Integer>("graphID", id);
    }
    
    public void setVertices(V source, V target) {
        //set source to first index and target to second
        this.source = source;
        this.target = target;
    }
	
	/**
	 * Returns the source vertex of this directed edge.
	 * 
	 * @return The vertex the edge is coming from.
	 */
	public V getSource() {
	    return this.source;		
	}

	/**
	 * Returns the target vertex of this edge.
	 * 
	 * @return The vertex the edge is pointing at/going to.
	 */
	public V getTarget() {
        return this.target;
	}

	@Override
	public List<V> getVertices() {
		List<V> vertices = new ArrayList<V>();
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
		return id.getValue();
	}

	@Override
	public String getLabel() {
		return label.getValue();
	}

	@Override
	public void addToFastGraphAccessor(FastGraphAccessor fga) {
	    fga.addEdgeForAttribute(this, "name", this.name.toString());
	    fga.addEdgeForAttribute(this, "id", this.id.getValue());
	    fga.addEdgeForAttribute(this, "label", this.label.toString());

	    fga.addEdgeForAttribute(this, "sourceVertex", this.source.getID());
	    fga.addEdgeForAttribute(this, "targetVertex", this.target.getID());
	}

	@Override
	public SerializedEdge<V> serialize() {
		List<String[]> attributes = new LinkedList<>();
		attributes.add(new String[] {"name", this.name.toString()});
		attributes.add(new String[] {"id", this.id.toString()});
		attributes.add(new String[] {"label", this.label.toString()});
		//TODO: add Vertices

		return new SerializedEdge<>(attributes, this.name.toString(), this.id.getValue(), this.label.toString());
	}

	@Override
	public OrthogonalEdgePath getPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
