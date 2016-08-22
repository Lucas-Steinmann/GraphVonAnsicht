package edu.kit.student.graphmodel;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.DoublePoint;
import edu.kit.student.util.IdGenerator;
import edu.kit.student.util.Settings;
import javafx.scene.paint.Color;

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
		List<GAnsProperty<?>> properties = new LinkedList<GAnsProperty<?>>();
		properties.add(name);
		properties.add(label);
		return properties;
	}
	
	@Override
	public String toString(){
		return this.label.getValue().toString() + "[" + this.id + "]";
	}

	@Override
	public DoublePoint getSize() {
		return Settings.getSize(this.getLabel(), true);
	}

	@Override
	public Color getColor() {
		return Color.LIGHTGREY;
	}
}
