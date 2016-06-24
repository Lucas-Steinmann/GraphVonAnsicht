package edu.kit.student.gui;

import edu.kit.student.graphmodel.Edge;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;

/**
 * A visual representation of an edge with a text.
 * 
 * @author Nicolas
 */
public class EdgeShape extends GAnsGraphElement {

	private Line line;
	private Text text;

	/**
	 * Constructs a EdgeShape with the information supplied by edge. The path of the EdgeShape is set by the EdgePath of edge.
	 * @param edge The Edge that supplies the information for building an EdgeShape.
	 */
	public EdgeShape(Edge edge) {
		// TODO: Find more beautiful way
		// TODO: Edge anhand der Nodes im EdgePath aufbauen.
		this.line = new Line();
		this.text = new Text();

		getChildren().addAll(this.line, this.text);
		
		setText(edge.getLabel());
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
		// TODO: Text passend alignen
	}

	@Override
	public String getText() {
		return text.getText();
	}

	@Override
	public void setColor(Color color) {
		line.setFill(color);
	}
}
