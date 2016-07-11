package edu.kit.student.gui;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.EdgePath;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.util.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * A visual representation of an edge with a text.
 * 
 * @author Nicolas
 */
public class EdgeShape extends GAnsGraphElement {

	private Text text;
	private Path path;
	private double middleX;
	private double middleY;

	/**
	 * Constructs a EdgeShape with the information supplied by edge. The path of the EdgeShape is set by the EdgePath of edge.
	 * @param edge The Edge that supplies the information for building an EdgeShape.
	 */
	public EdgeShape(Edge<?> edge) {
		this.path = new Path();
		this.text = new Text();
		
		List<PathElement> elements = new LinkedList<PathElement>();
		EdgePath edgePath = edge.getPath();
		
		Vertex vertex = edge.getVertices().get(0);
		double startX = vertex.getX();
		double startY = vertex.getY();
		vertex = edge.getVertices().get(1);
		double endX = vertex.getX();
		double endY = vertex.getY();
		
		int nodeCount = edgePath.getNodes().size();

		//starting point of the edge
		elements.add(new MoveTo(startX, startY));
		
		if(nodeCount%2 == 0) {
			double x1, x2, y1, y2;
			if(nodeCount == 0) {
				x1 = startX;
				x2 = endX;
				y1 = startY;
				y2 = endY;
			} else {
				int listMiddle = nodeCount / 2;
				Point point1 = edgePath.getNodes().get(listMiddle - 1);
				Point point2 = edgePath.getNodes().get(listMiddle);
				x1 = point1.x;
				y1 = point1.y;
				x2 = point2.x;
				y2 = point2.y;
			}
			middleX = (x1 + x2) / 2;
			middleY = (y1 + y2) / 2;
		} else {
			int pos = (nodeCount - 1) / 2;
			middleX = edgePath.getNodes().get(pos).x;
			middleY = edgePath.getNodes().get(pos).y;
		}
	
		for(int i = 0; i < nodeCount; i++) {
			Point point = edgePath.getNodes().get(i);
			elements.add(new LineTo(point.x, point.y));
		}
		
		//line to the ending point of the edge
		elements.add(new LineTo(endX, endY));
		
		// TODO: when and where to draw an arrowhead?
		
		this.path.getElements().addAll(elements);
		this.path.setManaged(false);
		this.text.setManaged(false);
		
		getChildren().addAll(this.path, this.text);
		
		setColor(null);
		setText(edge.getLabel());
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
		this.text.relocate(middleX, middleY);
	}

	@Override
	public String getText() {
		return text.getText();
	}

	@Override
	public void setColor(Color color) {
		Random random = new Random();
		color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
		//path.setFill(color);
		path.setStroke(color);
	}

	@Override
	public Path getElementShape() {
		return path;
	}
}
