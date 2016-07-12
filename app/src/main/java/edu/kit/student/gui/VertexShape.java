package edu.kit.student.gui;

import edu.kit.student.graphmodel.Vertex;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * A visual representation of a vertex with a text inside of it.
 * 
 * @author Nicolas
 */
public class VertexShape extends GAnsGraphElement {

	private Rectangle rectangle;
	private Text text;
	private Color color;
	private static double mindWidth = 20;
	private static double mindHeight = 5;
	private static double leftRightMargin = 8;
	private static double topBottomMargin = 4;

	/**
	 * Constructor
	 */
	public VertexShape() {
		rectangle = new Rectangle(mindWidth, mindHeight);
		rectangle.setFill(Color.GREEN);
		text = new Text();

		getChildren().addAll(rectangle, text);
	}
	
	/**
	 * Constructor. All settings are being automatically set through the supplied vertex.
	 * @param vertex The vertex that will be represented.
	 */
	public VertexShape(Vertex vertex) {
		this();
		
		setColor(vertex.getColor());
		setText(vertex.getLabel());
		relocate(vertex.getX(), vertex.getY());
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
		double width = this.text.getLayoutBounds().getWidth() + leftRightMargin;
		double height = this.text.getLayoutBounds().getHeight() + topBottomMargin;

		if (width < mindWidth)
			width = mindWidth;
		if (height < mindHeight)
			height = mindHeight;

		rectangle.setWidth(width);
		rectangle.setHeight(height);
	}

	@Override
	public String getText() {
		return this.text.getText();
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		rectangle.setFill(color);
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public String toString() {
		return "Vertex \"" + text.getText() + "\"";
	}

	@Override
	public Rectangle getElementShape() {
		return rectangle;
	}
}
