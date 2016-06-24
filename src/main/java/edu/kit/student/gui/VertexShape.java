package edu.kit.student.gui;

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
	private static double mindWidth = 20;
	private static double mindHeight = 5;

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
	 * Constructor which directly sets the text.
	 * 
	 * @param text
	 *            The text that will be displayed in the vertex.
	 */
	public VertexShape(String text) {
		this();

		setText(text);
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
		double width = this.text.getLayoutBounds().getWidth() + 8;
		double height = this.text.getLayoutBounds().getHeight() + 4;

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
		rectangle.setFill(color);
	}

}
