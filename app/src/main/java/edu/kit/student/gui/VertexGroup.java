package edu.kit.student.gui;

import java.util.HashSet;
import java.util.Set;

import edu.kit.student.util.IdGenerator;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;

public class VertexGroup {
	
	private Integer id;
	private String name;
	private Set<VertexShape> vertices = new HashSet<VertexShape>();
	private Label label;
	private ColorPicker picker;
	
	public VertexGroup() {
		this("Group", new HashSet<VertexShape>());
	}
	
	public VertexGroup(String name, Set<VertexShape> vertices) {
		this.id = IdGenerator.getInstance().createId();
		this.name = name;
		this.label = new Label(name);
		this.picker = new ColorPicker();
		
		setVertices(vertices);
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Label getLabel() {
		return this.label;
	}
	
	public Color getColor() {
		return this.picker.getValue();
	}
	
	public ColorPicker getPicker() {
		return this.picker;
	}
	
	public void setColor(Color color) {
		this.picker.setValue(color);
	}
	
	public void setVertices(Set<VertexShape> vertices) {
		this.vertices.addAll(vertices);
	}
	
	public void uncolorVertices() {
		for(VertexShape shape : vertices) {
			shape.setVertexStyle("-fx-effect: none");
		}
	}
	
	public void colorVertices() {
		for(VertexShape shape : vertices) {
			shape.setVertexStyle("-fx-effect: dropshadow(three-pass-box, " + GraphViewGraphFactory.toRGBCode(this.getColor()) + ", 4, 4, 0, 0);");
		}
	}
}
