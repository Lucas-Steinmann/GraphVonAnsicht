package edu.kit.student.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

/**
 * The GraphViewPanes class capsulates all graphical elements needed to make the GraphView behave as specified.
 * The GraphView is encapsulated in three other Panes.
 * 
 * |-----------------------------------------------|
 * |                  ScrollPane                   |
 * |   |---------------------------------------|   |
 * |   |              OuterPane                |   |
 * |   |   |-------------------------------|   |   |
 * |   |   |          InnerPane            |   |   |
 * |   |   |   |-----------------------|   |   |   |
 * |   |   |   |      GraphView        |   |   |   |
 * |   |   |   |                       |   |   |   |
 * |   |   |   |                       |   |   |   |
 * |   |   |   |                       |   |   |   |
 * |   |   |   |                       |   |   |   |
 * |   |   |   |                       |   |   |   |
 * |   |   |   |-----------------------|   |   |   |
 * |   |   |-------------------------------|   |   |
 * |   |---------------------------------------|   |
 * |-----------------------------------------------|
 *
 * ScrollPane: Uses standard implementation to show scrollbars.
 * OuterPane: Handles events for moving the inner Panes and zooming.
 *            Resizes to the InnerPanes size when its scaled(zoomed).
 * InnerPane: Is being scaled while zooming.
 * GraphView: Shows the graph.
 *
 */
public class GraphViewPanes {
	
	private ScrollPane scrollPane = new ScrollPane();
	private Pane outerPane = new Pane();
	private Pane innerPane = new Pane();
	private GraphView graphView;
	
	private DoubleProperty innerPaneScale = new SimpleDoubleProperty(1.0);
    private DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
	
    /**
	 * Constructor
	 * 
	 * @param graphView
	 *            The graphview placed inside of the panes.
	 */
	public GraphViewPanes(GraphView graphView) {
		this.graphView = graphView;
		
		Group group = new Group(this.graphView);
		innerPane.getChildren().add(group);
		outerPane.getChildren().add(innerPane);
		scrollPane.setContent(outerPane);
		outerPane.toBack();
		
		// add scale transform
		innerPane.scaleXProperty().bind(innerPaneScale);
		innerPane.scaleYProperty().bind(innerPaneScale);
		
		innerPane.boundsInParentProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                outerPane.setPrefSize(newValue.getWidth(), newValue.getHeight());
            }
        });
	}
	
	public ScrollPane getScrollPane() {
		return scrollPane;
	}
	
	public Pane getOuterPane() {
		return outerPane;
	}
	
	public Pane getInnerPane() {
		return innerPane;
	}
	
	public GraphView getGraphView() {
		return graphView;
	}
	
	/**
	 * Returns the scale on which the InnerPane currently is.
	 * 
	 * @return The scale of the InnerPane.
	 */
	public double getScale() {
        return innerPaneScale.get();
    }

	/**
	 * Sets the scale of the InnerPane.
	 * 
	 * @param scale
	 *            The scale of the InnerPane.
	 */
    public void setScale( double scale) {
        innerPaneScale.set(scale);
    }

	/**
	 * Sets the pivot so the zooming follows the mouse position on the
	 * GraphView.
	 * 
	 * @param x
	 *            The x coordinate of the pivot.
	 * @param y
	 *            The y coordinate of the pivot.
	 * @param scale
	 *            The scale of the InnerPane.
	 */
    public void setPivot( double x, double y, double scale) {
        // note: pivot valueId must be untransformed, i. e. without scaling
    	innerPane.setTranslateX(innerPane.getTranslateX() - x);
    	innerPane.setTranslateY(innerPane.getTranslateY() - y);
    	innerPaneScale.set(scale);
    }

    public double getDeltaY() {
        return deltaY.get();
    }
    
    public void setDeltaY( double dY) {
        deltaY.set(dY);
    }
}
