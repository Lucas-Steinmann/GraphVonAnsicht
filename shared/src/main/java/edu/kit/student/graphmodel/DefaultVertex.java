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

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name.getValue();
	}

	@Override
	public Integer getID() {
		// TODO Auto-generated method stub
		return id.getValue();
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return x;
	}

	@Override
	public int getY() {
		// TODO Auto-generated method stub
		return y;
	}
}
