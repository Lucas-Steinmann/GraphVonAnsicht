package edu.kit.student.gui;

import java.util.HashSet;
import java.util.Set;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ColorPicker;
import javafx.scene.paint.Color;

public class VertexGroup {
	
	private String name;
	private Set<VertexShape> vertices = new HashSet<VertexShape>();
	private ColorPicker picker;
	
	public VertexGroup() {
		this("Group", new HashSet<VertexShape>());
	}
	
	public VertexGroup(String name, Set<VertexShape> vertices) {
		picker = new ColorPicker();
		picker.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent t) {
				colorVertices(picker.getValue());
			}
		 });
		this.name = name;
		setVertices(vertices);
	}
	
	public String getName() {
		return this.name;
	}
	
	public Color getColor() {
		return this.picker.getValue();
	}
	
	public ColorPicker getPicker() {
		return this.picker;
	}
	
	public void setColor(Color color) {
		this.picker.setValue(color);
		colorVertices(color);
	}
	
	public void setVertices(Set<VertexShape> vertices) {
		this.vertices.addAll(vertices);
		colorVertices(picker.getValue());
	}
	
	public void uncolorVertices() {
		for(VertexShape shape : vertices) {
			shape.setVertexStyle("-fx-effect: none");
		}
	}
	
	private void colorVertices(Color color) {
		for(VertexShape shape : vertices) {
			shape.setVertexStyle("-fx-effect: dropshadow(three-pass-box, " + GraphViewGraphFactory.toRGBCode(color) + ", 4, 4, 0, 0);");
		}
	}
}
