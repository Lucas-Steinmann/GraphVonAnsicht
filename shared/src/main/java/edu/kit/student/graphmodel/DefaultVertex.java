package edu.kit.student.graphmodel;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.IdGenerator;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.List;

/**
 * This is an DefaultVertex, which has basic functions and is provided by the
 * main application. This vertex can be derived by plugins which offer more
 * functionality than the basic vertex.
 */
public class DefaultVertex implements Vertex {

	private GAnsProperty<String> name;
	private Integer id;
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
	public DefaultVertex(String name, String label) {
        this.name = new GAnsProperty<String>("name", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = IdGenerator.getInstance().createId();
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
		fga.addVertexForAttribute(this, "name", this.name.toString());
		fga.addVertexForAttribute(this, "id", this.id);
		fga.addVertexForAttribute(this, "label", this.label.toString());
		fga.addVertexForAttribute(this, "x", x);
		fga.addVertexForAttribute(this, "y", y);
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

	@Override
	public List<GAnsProperty<?>> getProperties() {
		LinkedList<GAnsProperty<?>> properties = new LinkedList<>();
		properties.add(name);
		properties.add(label);
		return properties;
	}
	
	@Override
	public String toString(){
		return this.name.toString() + "[" + this.id + "]";
	}

	@Override
	public Pair<Double, Double> getSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Color getColor() {
		return Color.LIGHTGREY;
	}
}
