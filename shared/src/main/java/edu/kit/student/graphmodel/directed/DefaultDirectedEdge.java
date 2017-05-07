package edu.kit.student.graphmodel.directed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.student.graphmodel.EdgeArrow;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.OrthogonalEdgePath;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.IdGenerator;
import javafx.scene.paint.Color;

public class DefaultDirectedEdge<V extends Vertex> implements DirectedEdge {

	private V target;
	private V source;
	private GAnsProperty<String> name;
	private Integer id;
	private GAnsProperty<String> label;
	private OrthogonalEdgePath path;
	private List<GAnsProperty<?>> data;

	/**
     * Constructs a new DefaultDirectedEdge.
     * Sets the specified attributes.
	 * 
	 * @param name of the new edge
	 * @param label of the new edge
	 */
    public DefaultDirectedEdge(String name, String label) {       
    	this(name, label, null, null);
    }
    
    /**
     * Constructs a new DefaultDirectedEdge.
     * Sets the specified attributes.
     * 
	 * @param name of the new edge
	 * @param label of the new edge
     * @param source source vertex
     * @param target target vertex
     */
    public DefaultDirectedEdge(String name, String label, V source, V target) {  
    	this(name,label,source,target,new HashMap<>());
    }
    
    /**
     * Constructs a new DefaultDirectedEdge.
     * Sets the specified attributes and stores additional data as properties of the edge.
     * 
	 * @param name of the new edge
	 * @param label of the new edge
     * @param source source vertex
     * @param target target vertex
     * @param data additional data as key-value pairs
     */
    public DefaultDirectedEdge(String name, String label, V source, V target, Map<String, ?> data) {  
        this.name = new GAnsProperty<String>("name", name);
        this.label = new GAnsProperty<String>("label", label);
        this.source = source;
        this.target = target;
        this.id = IdGenerator.getInstance().createId();
        this.path = new OrthogonalEdgePath();
        this.data = new LinkedList<>();
        for (Entry<String, ?> entry : data.entrySet()) {
        	this.data.add(new GAnsProperty<Object>(entry.getKey(), entry.getValue()));
        }
    }

    /**
     * Sets source and target vertex of this directed edge
     * 
     * @param source vertex
     * @param target vertex
     */
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
	public List<V> getVertices() {
		List<V> vertices = new ArrayList<>();
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
	public EdgeArrow getArrowHead() {
		return EdgeArrow.ARROW;
	}
	
	@Override
	public List<GAnsProperty<?>> getProperties() {
		LinkedList<GAnsProperty<?>> properties = new LinkedList<>();
		properties.addAll(data);
		properties.add(name);
		properties.add(label);
		return properties;
	}
	
	@Override
	public Color getColor() {
		return Color.LIGHTGRAY;
	}
	
	@Override
	public String toString(){
		return name.toString() + "[" + id + "](" + source.toString() + "->" + target.toString() + ")";
	}

    @Override
    public int hashCode() {
        return id.hashCode();
    }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		DefaultDirectedEdge<?> that = (DefaultDirectedEdge<?>) o;

		if (target != null ? !target.equals(that.target) : that.target != null) return false;
		if (source != null ? !source.equals(that.source) : that.source != null) return false;
		if (name != null ? !name.equals(that.name) : that.name != null) return false;
		if (id != null ? !id.equals(that.id) : that.id != null) return false;
		if (label != null ? !label.equals(that.label) : that.label != null) return false;
		if (path != null ? !path.equals(that.path) : that.path != null) return false;
		return data != null ? data.equals(that.data) : that.data == null;
	}
}
