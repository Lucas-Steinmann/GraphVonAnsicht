package edu.kit.student.graphmodel;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.DoublePoint;
import edu.kit.student.util.IdGenerator;
import edu.kit.student.util.IntegerPoint;
import edu.kit.student.util.Settings;
import javafx.scene.paint.Color;

/**
 * This is an DefaultVertex, which has basic functions and is provided by the
 * main application. This vertex can be derived by plugins which offer more
 * functionality than the basic vertex.
 */
public class DefaultVertex implements ViewableVertex {

    private GAnsProperty<String> name;
	private Integer id;
	private GAnsProperty<String> label;
	private int x;
	private int y;
	private IntegerPoint leftRightMargin;
	private int link = 0;
	private List<GAnsProperty<?>> data;
	private IntegerPoint leftRightMargin;

	/**
	 * Constructs a new DefaultVertex.
	 * Sets the specified attributes.
	 * 
	 * @param name of the new vertex
	 * @param label of the new vertex
	 */
	public DefaultVertex(String name, String label) {
		this(name, label, new HashMap<>());
	}

	/**
	 * Constructs a new DefaultVertex.
	 * Sets the specified attributes.
	 * Adds all specified additional data as properties of this vertex.
	 * 
	 * @param name of the new vertex
	 * @param label of the new vertex
	 * @param data additional data to be stored
	 */
	public DefaultVertex(String name, String label, Map<String, ?> data) {
        this.name = new GAnsProperty<String>("name", name);
        this.label = new GAnsProperty<String>("label", label);
        this.id = IdGenerator.getInstance().createId();
        this.data = new LinkedList<>();
        for (Entry<String, ?> entry : data.entrySet()) {
        	this.data.add(new GAnsProperty<Object>(entry.getKey(), entry.getValue()));
        }
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
		properties.addAll(data);
		properties.add(name);
		properties.add(label);
		return properties;
	}
	
	@Override
	public String toString(){
		return getName() + "[" + getID() + "]";
	}

	@Override
	public DoublePoint getSize() {
		return Settings.getSize(this.getLabel(), true);
	}
	
	@Override
	public IntegerPoint getLeftRightMargin() {
		if(this.leftRightMargin != null){
			return this.leftRightMargin;
		}else{
			return new IntegerPoint(2,2);
		}
	}
	
	@Override
	public void setLeftRightMargin(IntegerPoint newMargin) {
		this.leftRightMargin = newMargin;
	}

	@Override
	public Color getColor() {
		return Color.LIGHTGREY;
	}

	@Override
    public int hashCode() {
        return id.hashCode();
    }

	@Override
	public int getLink() {
		return this.link;
	}
	
	public void setLink(int graphId) {
		this.link = graphId;
	}

	@Override
	public VertexPriority getPriority() {
		return VertexPriority.HIGH;
	}
}
