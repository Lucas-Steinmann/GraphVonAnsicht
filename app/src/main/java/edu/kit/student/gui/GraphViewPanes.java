package edu.kit.student.gui;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;

public class GraphViewPanes {
	
	private ScrollPane scrollPane = new ScrollPane();
	private Pane outerPane = new Pane();
	private Pane innerPane = new Pane();
	private GraphView graphView;
	
	private DoubleProperty innerPaneScale = new SimpleDoubleProperty(1.0);
    private DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
	
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
	
	public double getScale() {
        return innerPaneScale.get();
    }

    public void setScale( double scale) {
        innerPaneScale.set(scale);
    }

    public void setPivot( double x, double y, double scale) {
        // note: pivot value must be untransformed, i. e. without scaling
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
