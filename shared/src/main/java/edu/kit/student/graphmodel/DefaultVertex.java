package edu.kit.student.graphmodel;

import java.util.List;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;

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
		// TODO Auto-generated method stub

	}

	@Override
	public SerializedVertex serialize(List<Entry<String, String>> attributes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}
}
