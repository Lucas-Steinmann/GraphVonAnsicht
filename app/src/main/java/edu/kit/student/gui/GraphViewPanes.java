package edu.kit.student.gui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The GraphViewPanes class encapsulates all graphical elements needed to make the GraphView behave as specified.
 * The GraphView is encapsulated in three other Panes.
 * 
 * |-----------------------------------------------|
 * |                  AnchorPane                   |
 * |   |---------------------------------------|   |
 * |   |              ScrollPane               |   |
 * |   |   |-------------------------------|   |   |
 * |   |   |            Group          |   |   |   |
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
 *
 */
public class GraphViewPanes {


    private final ScrollPane scrollPane;
    private final Group group = new Group();
	private final AnchorPane wrapper;
	private final PanAndZoomPane panAndZoomPane = new PanAndZoomPane();

	private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
	private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);

    private final Logger logger = LoggerFactory.getLogger(GraphViewPanes.class);

	private GraphView graphView;
	
    /**
	 * Constructor
	 * 
	 * @param graphView
	 *            The GraphView placed inside of the panes.
	 */
	GraphViewPanes(GraphView graphView) {
		this.graphView = graphView;

        scrollPane = new ScrollPane();
        scrollPane.setPannable(false);
		scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
		AnchorPane.setTopAnchor(scrollPane, 10.0d);
		AnchorPane.setRightAnchor(scrollPane, 10.0d);
		AnchorPane.setBottomAnchor(scrollPane, 10.0d);
		AnchorPane.setLeftAnchor(scrollPane, 10.0d);


		group.getChildren().add(this.graphView);

		zoomProperty.bind(panAndZoomPane.myScale);
		deltaY.bind(panAndZoomPane.deltaY);
		panAndZoomPane.getChildren().add(group);


		SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);
		scrollPane.setContent(panAndZoomPane);
		panAndZoomPane.toBack();

        wrapper = new AnchorPane();
        wrapper.setFocusTraversable(false);
        wrapper.getChildren().addAll(scrollPane);

		scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMouseClickedEventHandler());
		scrollPane.addEventFilter(MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
		scrollPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
		scrollPane.addEventFilter(ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
	}
	

	GraphView getGraphView() {
		return graphView;
	}
	
	/**
	 * Returns the scale on which the InnerPane currently is.
	 * 
	 * @return The scale of the InnerPane.
	 */
	double getScale() {
        return zoomProperty.get();
    }

	/**
	 * Sets the scale of the InnerPane.
	 * 
	 * @param scale
	 *            The scale of the InnerPane.
	 */
    public void setScale( double scale) {
        zoomProperty.set(scale);
    }

	Group getContentGroup() {
		return group;
	}

	Node getScrollPane() {
		return scrollPane;
	}

	Pane getRoot() {
        return wrapper;
    }

	Pane getCanvas() {
        return panAndZoomPane;
    }

    public double getDeltaY() {
        return deltaY.get();
    }
    
    public void setDeltaY( double dY) {
        deltaY.set(dY);
    }

	private class PanAndZoomPane extends Pane {

		private static final double DEFAULT_DELTA = 1.5d;
		private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
		private DoubleProperty deltaY = new SimpleDoubleProperty(0.0);
		private Timeline timeline;


		private PanAndZoomPane() {

			this.timeline = new Timeline(60);

			// add scale transform
			scaleXProperty().bind(myScale);
			scaleYProperty().bind(myScale);
		}


		private double getScale() {
			return myScale.get();
		}

		private void setScale(double scale) {
			myScale.set(scale);
		}

		private void setPivot(double x, double y, double scale) {
			// note: pivot value must be untransformed, i. e. without scaling
			// timeline that scales and moves the node
			timeline.getKeyFrames().clear();
			timeline.getKeyFrames().addAll(
					new KeyFrame(Duration.millis(100), new KeyValue(translateXProperty(), getTranslateX() - x)),
					new KeyFrame(Duration.millis(100), new KeyValue(translateYProperty(), getTranslateY() - y)),
					new KeyFrame(Duration.millis(100), new KeyValue(myScale, scale))
			);
			timeline.play();

		}

		private void fitWidth() {
			double scale = getParent().getLayoutBounds().getMaxX() / getLayoutBounds().getMaxX();
			double oldScale = getScale();

			double f = scale - oldScale;

			double dx = getTranslateX() - getBoundsInParent().getMinX() - getBoundsInParent().getWidth() / 2;
			double dy = getTranslateY() - getBoundsInParent().getMinY() - getBoundsInParent().getHeight() / 2;

			double newX = f * dx + getBoundsInParent().getMinX();
			double newY = f * dy + getBoundsInParent().getMinY();

			setPivot(newX, newY, scale);

		}

		private void resetZoom() {
			double scale = 1.0d;

			double x = getTranslateX();
			double y = getTranslateY();

			setPivot(x, y, scale);
		}

		private double getDeltaY() {
			return deltaY.get();
		}

		private void setDeltaY(double dY) {
			deltaY.set(dY);
		}
	}


	/**
	 * Mouse drag context used for scene and nodes.
	 */
	class DragContext {

		double mouseAnchorX;
		double mouseAnchorY;

		double translateAnchorX;
		double translateAnchorY;

	}

	/**
	 * Listeners for making the scene's canvas draggable and zoomable
	 */
	public class SceneGestures {

		private DragContext sceneDragContext = new DragContext();

		PanAndZoomPane panAndZoomPane;

		private SceneGestures(PanAndZoomPane canvas) {
			this.panAndZoomPane = canvas;
		}

		private EventHandler<MouseEvent> getOnMouseClickedEventHandler() {
			return onMouseClickedEventHandler;
		}

		private EventHandler<MouseEvent> getOnMousePressedEventHandler() {
			return onMousePressedEventHandler;
		}

		private EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
			return onMouseDraggedEventHandler;
		}

		private EventHandler<ScrollEvent> getOnScrollEventHandler() {
			return onScrollEventHandler;
		}

		private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

			public void handle(MouseEvent event) {
                if (event.isControlDown() && event.getButton() == MouseButton.SECONDARY) {
                    sceneDragContext.mouseAnchorX = event.getX();
                    sceneDragContext.mouseAnchorY = event.getY();

                    sceneDragContext.translateAnchorX = panAndZoomPane.getTranslateX();
                    sceneDragContext.translateAnchorY = panAndZoomPane.getTranslateY();
                }
			}

		};

		private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event) {

			    if (event.isControlDown() && event.getButton() == MouseButton.SECONDARY) {
                    panAndZoomPane.setTranslateX(sceneDragContext.translateAnchorX + event.getX() - sceneDragContext.mouseAnchorX);
                    panAndZoomPane.setTranslateY(sceneDragContext.translateAnchorY + event.getY() - sceneDragContext.mouseAnchorY);

                    event.consume();
                }
			}
		};

		/**
		 * Mouse wheel handler: zoom to pivot point
		 */
		private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {

				double delta = PanAndZoomPane.DEFAULT_DELTA;

				double scale = panAndZoomPane.getScale();
				double oldScale = scale;

				panAndZoomPane.setDeltaY(event.getDeltaY());
				if (panAndZoomPane.deltaY.get() < 0) {
					scale /= delta;
				} else {
					scale *= delta;
				}

				double f = (scale / oldScale) - 1;

				double dx = (event.getX() - (panAndZoomPane.getBoundsInParent().getWidth() / 2 + panAndZoomPane.getBoundsInParent().getMinX()));
				double dy = (event.getY() - (panAndZoomPane.getBoundsInParent().getHeight() / 2 + panAndZoomPane.getBoundsInParent().getMinY()));

				panAndZoomPane.setPivot(f * dx, f * dy, scale);

				event.consume();

			}
		};

		/**
		 * Mouse click handler
		 */
		private EventHandler<MouseEvent> onMouseClickedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				//if (event.getButton().equals(MouseButton.PRIMARY)) {
				//	if (event.getClickCount() == 2) {
				//		panAndZoomPane.resetZoom();
				//	}
				//}
				if (event.getButton().equals(MouseButton.SECONDARY)) {
					if (event.getClickCount() == 2) {
						panAndZoomPane.fitWidth();
					}
				}
			}
		};
	}
}
