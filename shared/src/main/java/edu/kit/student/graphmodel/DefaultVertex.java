package edu.kit.student.graphmodel;

import edu.kit.student.objectproperty.GAnsProperty;

import java.util.LinkedList;
import java.util.List;

/**
 * This is an DefaultVertex, which has basic functions and is provided by the
 * main application. This vertex can be derived by plugins which offer more
 * functionality than the basic vertex.
 */
public class DefaultVertex implements Vertex {

	private GAnsProperty<String> name;
	private GAnsProperty<Integer> id;
	private GAnsProperty<String> label;
	private int x;
	private int y;

	/**
	 * Constructor
	 * 
	 * @param name of the new vertex
	 * @param label of the new vertex
	 * @param id of the new vertex
	 */
	public DefaultVertex(String name, String label, Integer id) {
        this.name = new GAnsProperty<String>("graphName", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = new GAnsProperty<Integer>("graphID", id);
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
		fga.addVertexForAttribute(this, "name", this.name.toString());
		fga.addVertexForAttribute(this, "id", this.id.getValue());
		fga.addVertexForAttribute(this, "label", this.label.toString());
		fga.addVertexForAttribute(this, "x", x);
		fga.addVertexForAttribute(this, "y", y);
	}

	@Override
	public SerializedVertex serialize() {
		List<String[]> attributes = new LinkedList<>();
		attributes.add(new String[] {"name", this.name.toString()});
		attributes.add(new String[] {"id", this.id.toString()});
		attributes.add(new String[] {"label", this.label.toString()});
		attributes.add(new String[] {"x", Integer.toString(this.x)});
		attributes.add(new String[] {"y", Integer.toString(this.y)});
		//TODO: add Vertices

		return new SerializedVertex(attributes, this.name.toString(), this.id.getValue(), this.label.toString());
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }
}
