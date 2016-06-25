package edu.kit.student.graphmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A {@link DirectedEdge} is an edge that has one source and one target vertex.
 * The direction of the edge is specified.
 */
public class DirectedEdge<V extends Vertex> implements Edge<V> {

	private List<V> vertices;
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
        this.vertices = new ArrayList<V>();
        
        this.name = new GAnsProperty<String>("graphName", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = new GAnsProperty<Integer>("graphID", id);
    }
    
    public void setVertices(V source, V target) {
        //set source to first index and target to second
        this.vertices.add(source);
        this.vertices.add(target);
    }
	
	/**
	 * Returns the source vertex of this directed edge.
	 * 
	 * @return The vertex the edge is coming from.
	 */
	public V getSource() {
	    if(this.vertices.size() == 1) {
	        return this.vertices.get(0);
	    }
	    return null;		
	}

	/**
	 * Returns the target vertex of this edge.
	 * 
	 * @return The vertex the edge is pointing at/going to.
	 */
	public V getTarget() {
        if(this.vertices.size() == 2) {
            return this.vertices.get(1);
        }
        return null;
	}

	@Override
	public List<V> getVertices() {
		return this.vertices;
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
	    //TODO: 
	}

	@Override
	public SerializedEdge<V> serialize(List<Entry<String, String>> attributes) {
	    //TODO:
		return null;
	}

	@Override
	public OrthogonalEdgePath getPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
