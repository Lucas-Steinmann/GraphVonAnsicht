package edu.kit.student.gui;

import edu.kit.student.graphmodel.ViewableVertex;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class BackgroundShape extends GAnsGraphElement {
	
	private Rectangle rectangle;
	private Text text;
	private Color color;
	
	public BackgroundShape(ViewableVertex vertex) {
		Pair<Double,Double> size = vertex.getSize();
		this.rectangle = new Rectangle(size.getKey(),size.getValue(), vertex.getColor());
		this.text = new Text(vertex.getLabel());
		this.color = vertex.getColor();
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public String getText() {
		return this.text.getText();
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		this.rectangle.setFill(color);
	}

	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public Rectangle getElementShape() {
		return this.rectangle;
	}

}
