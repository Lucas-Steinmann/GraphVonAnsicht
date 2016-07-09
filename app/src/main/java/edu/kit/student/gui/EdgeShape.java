package edu.kit.student.gui;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.EdgePath;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.util.Point;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import javafx.util.Pair;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * A visual representation of an edge with a text.
 * 
 * @author Nicolas
 */
public class EdgeShape extends GAnsGraphElement {

	private Set<Line> lines;
	private Text text;
	
	private double startX;
	private double startY;
	private double endX;
	private double endY;

	/**
	 * Constructs a EdgeShape with the information supplied by edge. The path of the EdgeShape is set by the EdgePath of edge.
	 * @param edge The Edge that supplies the information for building an EdgeShape.
	 */
	public EdgeShape(Edge edge) {
		//TODO: Woher wei� man welche vertex die am n�hsten an dem ersten punkt des pfads ist?! (Damit auch von wo aus dem knoten die kante gehen soll!)
		this.lines = new HashSet<>();
		this.text = new Text();
		EdgePath path = edge.getPath();
		
		Vertex vertex = edge.getVertices().get(0);
		Pair<Double, Double> vertexSize = GraphViewGraphFactory.getSizeOfVertex(vertex.getLabel());
		this.startX = vertex.getX(); //TODO: richtige position zum beginnen der edge herausfinden(selbes beim ende)
		this.startY = vertex.getY();
		double startX = this.startX;
		double startY = this.startY ;
		double endX = 0;
		double endY = 0;
		
		for(int i = 0; i < path.getSegmentsCount(); i++) {
			Point point = path.getNodes().get(i);
			endX = point.x;
			endY = point.y;
			
			Line line = new Line(startX, startY, endX, endY);
			getChildren().add(line);
			line.setManaged(false);
			line.relocate(Math.min(startX, endX), Math.min(startY, endY));
			lines.add(line);
			
			startX = endX;
			startY = endY;
		}
		vertex = edge.getVertices().get(1);
		vertexSize = GraphViewGraphFactory.getSizeOfVertex(vertex.getLabel());
		this.endX = vertex.getX();
		this.endY = vertex.getY();
		Line line = new Line(startX, startY, this.endX, this.endY);
		line.setManaged(false);
		//relocate(Math.min(startX, this.endX), Math.min(startY, this.endY));
		lines.add(line);
		getChildren().add(line);
		
		setText(edge.getLabel());
		
		//getChildren().addAll(this.lines);
		getChildren().add(this.text);
		setColor(null);
	}
	
	public EdgeShape(DirectedEdge edge) {
		// TODO: anderen konstruktor aufrufen und zeichnen des pfeilkopfes.
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
		Random random = new Random();
		color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
		for(Line line: this.lines) {
			line.setFill(color);
			line.setStroke(color);
		}
	}
}
