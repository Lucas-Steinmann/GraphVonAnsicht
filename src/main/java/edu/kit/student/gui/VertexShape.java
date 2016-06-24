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
	 * Constructor. All settings are being automatically set through the supplied vertex.
	 * @param vertex The vertex that will be represented.
	 */
	public VertexShape(Vertex vertex) {
		this();

		setText(vertex.getLabel());
		relocate(vertex.getX(), vertex.getY());
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
