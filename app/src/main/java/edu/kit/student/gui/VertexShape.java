package edu.kit.student.gui;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.util.Settings;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Pair;

/**
 * A visual representation of a vertex with a text inside of it.
 * 
 * @author Nicolas
 */
public class VertexShape extends GAnsGraphElement {

	private Rectangle rectangle;
	private Text text;
	private Color color;
	private String style = "";
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
		Pair<Double,Double> size = vertex.getSize();
		
		this.rectangle = new Rectangle(size.getKey(), size.getValue());
		
//		Text tmp = new Text(vertex.getLabel());
//		if(tmp.getLayoutBounds().getWidth() + Settings.leftRightMargin > size.getKey()) {
//			double tmpLength = tmp.getLayoutBounds().getWidth() + Settings.leftRightMargin;
//			tmpLength = tmpLength - size.getKey();
//			tmpLength -= new Text("...").getLayoutBounds().getWidth();
//			//the size that needs to be removed... 
//		}
		
		this.text = new Text(vertex.getLabel());
		getChildren().addAll(rectangle, text);

		setColor(vertex.getColor());
		
		relocate(vertex.getX(), vertex.getY());
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
	
	public void setVertexStyle(String style) {
		this.style = style;
		this.setStyle(style);
	}
	
	public String getVertexStyle() {
		return this.style;
	}
}
