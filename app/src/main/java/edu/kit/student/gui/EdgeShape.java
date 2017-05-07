package edu.kit.student.gui;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.EdgeArrow;
import edu.kit.student.graphmodel.EdgePath;
import edu.kit.student.util.DoublePoint;
import javafx.geometry.BoundingBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.text.Text;

import java.util.LinkedList;
import java.util.List;

/**
 * A visual representation of an edge with a text.
 * 
 * @author Nicolas
 */
public class EdgeShape extends GAnsGraphElement {

	private Text text;
	private Path path;
	private Color color;
	private String style = "";


	/**
	 * Constructs a EdgeShape with the information supplied by edge. The path of the EdgeShape is set by the EdgePath of edge.
	 * @param edge The Edge that supplies the information for building an EdgeShape.
	 */
	public EdgeShape(Edge edge) {
		this.path = new Path();
		this.text = new Text();
		
		List<PathElement> elements = new LinkedList<>();
		EdgePath edgePath = edge.getPath();
		
		// there are a minimum of two nodes per edge, a start and an end node
		int nodeCount = edgePath.getNodes().size();
		
		double startX = edgePath.getNodes().get(0).x;
		double startY = edgePath.getNodes().get(0).y;
		double endX = edgePath.getNodes().get(nodeCount - 1).x;
		double endY = edgePath.getNodes().get(nodeCount - 1).y;

		// starting point of the edge
		elements.add(new MoveTo(startX, startY));
		
		for(int i = 1; i < nodeCount - 1; i++) {
			DoublePoint point = edgePath.getNodes().get(i);
			elements.add(new LineTo(point.x, point.y));
		}
		
		// line to the ending point of the edge
		elements.add(new LineTo(endX, endY));
		
		DoublePoint point1 = edgePath.getNodes().get(nodeCount - 2);
		DoublePoint point2 = edgePath.getNodes().get(nodeCount - 1);
		
		// adding the arrowhead to the edge
		elements.addAll(getArrow(edge.getArrowHead(), endX, endY, 
				ArrowDirection.calculateDirection(point1.x, point1.y, point2.x, point2.y)).getElements());
		
		this.path.getElements().addAll(elements);
		this.path.setManaged(false);
		this.text.setManaged(false);

		path.intersects(new BoundingBox(0,0,0,0));
		getChildren().addAll(this.path/*, this.text*/);
		
		setColor(edge.getColor());
		setText(edge.getLabel());
	}

	@Override
	public void setText(String text) {
		this.text.setText(text);
	}

	@Override
	public String getText() {
		return text.getText();
	}

	@Override
	public void setColor(Color color) {
		this.color = color;
		path.setStroke(color);
	}
	
	@Override
	public Color getColor() {
		return this.color;
	}

	@Override
	public Path getElementShape() {
		return path;
	}

	private Path getArrow(EdgeArrow arrowType, double startX, double startY, ArrowDirection direction) {
		switch(arrowType) {
			case NONE: return new Path();
			case ARROW: return arrowPath(startX, startY, direction);
			default: return new Path(); //default is an undirected edge
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		EdgeShape edgeShape = (EdgeShape) o;

		if (text != null ? !text.equals(edgeShape.text) : edgeShape.text != null) return false;
		if (path != null ? !path.equals(edgeShape.path) : edgeShape.path != null) return false;
		if (color != null ? !color.equals(edgeShape.color) : edgeShape.color != null) return false;
		return style != null ? style.equals(edgeShape.style) : edgeShape.style == null;
	}

	/*@Override
	public int hashCode() {
		int result = text != null ? text.hashCode() : 0;
		result = 31 * result + (path != null ? path.hashCode() : 0);
		result = 31 * result + (color != null ? color.hashCode() : 0);
		result = 31 * result + (style != null ? style.hashCode() : 0);
		return result;
	}*/

	/**
	 * Describes the position of the arrow on the vertex
	 */
	private enum ArrowDirection {
		LEFT, RIGHT, TOP, BOTTOM;
		
		public static ArrowDirection calculateDirection(double x1, double y1, double x2, double y2) {
			if(x1 == x2) {
				if(y1 < y2) {
					return TOP;
				} else {
					return BOTTOM;
				}
			}
			
			if(y1 == y2) {
				if(x1 < x2) {
					return LEFT;
				} else {
					return RIGHT;
				}
			}
			
			return TOP; //As a default top is used, but should never be reached with orthogonal paths
		}
	}
	
	// startingpoints x and y of the shape
	private Path arrowPath(double x, double y, ArrowDirection direction) {
		double x1, y1, x2, y2;
		switch(direction) {
		case LEFT: 
			x1 = x - 12;
			x2 = x1;
			y1 = y - 6;
			y2 = y + 6;
			break;
		case RIGHT:
			x1 = x + 12;
			x2 = x1;
			y1 = y - 6;
			y2 = y + 6;
			break;
		case TOP:
			x1 = x - 6;
			x2 = x + 6;
			y1 = y - 12;
			y2 = y1;
			break;
		case BOTTOM:
			x1 = x - 6;
			x2 = x + 6;
			y1 = y + 12;
			y2 = y1;
			break;
		default:
			x1 = 0;
			x2 = 0;
			y1 = 0;
			y2 = 0;
		}
		
		Path path = new Path();
		List<PathElement> elements = new LinkedList<>();
		elements.add(new MoveTo(x, y));
		elements.add(new LineTo(x1, y1));
		elements.add(new MoveTo(x2, y2));
		elements.add(new LineTo(x, y));
		path.getElements().addAll(elements);
		return path;
	}

	public void setEdgeStyle(String style) {
		this.style = style;
		this.setStyle(style);
	}

	public String getEdgeStyle() {
		return this.style;
	}
}
