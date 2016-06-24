package edu.kit.student.gui;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;

/**
 * GraphViewEventHandler provides listeners for making the {@link GraphView}
 * draggable and zoomable.
 * 
 * @author Nicolas
 */
public class GraphViewEventHandler {

	private static final double MAX_SCALE = 10.0d;
	private static final double MIN_SCALE = .1d;

	private DragContext sceneDragContext = new DragContext();
	private GraphView canvas;

	/**
	 * Constructor.
	 * 
	 * @param canvas
	 *            The {@link GraphView} which later on will get the EventHandler
	 *            set.
	 */
	public GraphViewEventHandler(GraphView canvas) {
		this.canvas = canvas;
	}

	/**
	 * Returns an EventHandler which handles pressing the mouse inside the
	 * {@link GraphView}.
	 * 
	 * @return An EventHandler which handles pressing the mouse.
	 */
	public EventHandler<MouseEvent> getOnMousePressedEventHandler() {
		return onMousePressedEventHandler;
	}

	/**
	 * Returns an EventHandler which handles dragging inside the
	 * {@link GraphView}.
	 * 
	 * @return An EventHandler which handles dragging.
	 */
	public EventHandler<MouseEvent> getOnMouseDraggedEventHandler() {
		return onMouseDraggedEventHandler;
	}

	/**
	 * Returns an EventHandler which maps scrolling the mousewheel to zooming on
	 * the {@link GraphView}.
	 * 
	 * @return An EventHandler which maps scrolling the mousewheel to zooming.
	 */
	public EventHandler<ScrollEvent> getOnScrollEventHandler() {
		return onScrollEventHandler;
	}

	// Mouse pressed handler.
	private EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			// right mouse button => panning
			if (!event.isSecondaryButtonDown())
				return;

			sceneDragContext.mouseAnchorX = event.getSceneX();
			sceneDragContext.mouseAnchorY = event.getSceneY();

			sceneDragContext.translateAnchorX = canvas.getTranslateX();
			sceneDragContext.translateAnchorY = canvas.getTranslateY();
		}
	};

	// Mouse drag handler.
	private EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
		public void handle(MouseEvent event) {
			// right mouse button => panning
			if (!event.isSecondaryButtonDown())
				return;

			canvas.setTranslateX(sceneDragContext.translateAnchorX + event.getSceneX() - sceneDragContext.mouseAnchorX);
			canvas.setTranslateY(sceneDragContext.translateAnchorY + event.getSceneY() - sceneDragContext.mouseAnchorY);

			event.consume();
		}
	};

	// Mouse wheel handler: zoom to pivot point.
	private EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
		@Override
		public void handle(ScrollEvent event) {
			double delta = 1.2;
			double scale = canvas.getScale(); // currently we only use Y, same
												// value is used for X
			double oldScale = scale;

			if (event.getDeltaY() < 0)
				scale /= delta;
			else
				scale *= delta;

			scale = clamp(scale, MIN_SCALE, MAX_SCALE);

			double f = (scale / oldScale) - 1;
			double dx = (event.getSceneX()
					- (canvas.getBoundsInParent().getWidth() / 2 + canvas.getBoundsInParent().getMinX()));
			double dy = (event.getSceneY()
					- (canvas.getBoundsInParent().getHeight() / 2 + canvas.getBoundsInParent().getMinY()));

			canvas.setScale(scale);
			// note: pivot value must be untransformed, i. e. without scaling
			canvas.setPivot(f * dx, f * dy);

			event.consume();
		}
	};

	private static double clamp(double value, double min, double max) {
		if (Double.compare(value, min) < 0)
			return min;

		if (Double.compare(value, max) > 0)
			return max;

		return value;
	}

	private class DragContext {
		double mouseAnchorX;
		double mouseAnchorY;

		double translateAnchorX;
		double translateAnchorY;
	}
}
