package edu.kit.student.gui;

import java.util.Arrays;
import java.util.HashSet;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;

/**
 * The selection model for the {@link GraphView}, that supports multiple
 * selection of vertices and edges.
 * 
 * @author Nicolas
 */
public class GraphViewSelectionModel {
	
	private static final double MAX_SCALE = 10.0d;
	private static final double MIN_SCALE = .1d;

	private ObservableSet<VertexShape> selection;

	public GraphViewSelectionModel(Pane outerPane, GraphView view) {
		selection = FXCollections.observableSet(new HashSet<VertexShape>());
		new RubberBandSelection(outerPane, view);
	}

	public void add(VertexShape node) {
		node.setStyle("-fx-effect: dropshadow(three-pass-box, red, 10, 10, 0, 0);");
		selection.add(node);
	}

	public void remove(VertexShape node) {
		node.setStyle("-fx-effect: null");
		selection.remove(node);
	}

	public void clear() {
		while (!selection.isEmpty()) {
			remove(selection.iterator().next());
		}
	}

	public boolean contains(VertexShape node) {
		return selection.contains(node);
	}
	
	public boolean isEmpty() {
		return selection.isEmpty();
	}

	public void log() {
		System.out.println("Items in model: " + Arrays.asList(selection.toArray()));
	}

	public ObservableSet<VertexShape> getSelectedItems() {
		return selection;
	}

	private class RubberBandSelection {
		final DragContext dragContext = new DragContext();
		Rectangle rect;
		Pane outerPane;
		GraphView view;

		public RubberBandSelection(Pane outerPane, GraphView view) {
			this.outerPane = outerPane;
			this.view = view;

			rect = new Rectangle(0, 0, 0, 0);
			rect.setStroke(Color.BLUE);
			rect.setStrokeWidth(1);
			rect.setStrokeLineCap(StrokeLineCap.ROUND);
			rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

			this.outerPane.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
			this.outerPane.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
			this.outerPane.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
			this.outerPane.addEventHandler(ScrollEvent.ANY, onScrollEventHandler);
		}

		EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				dragContext.mouseAnchorX = event.getX();
				dragContext.mouseAnchorY = event.getY();

				if(event.getButton() == MouseButton.PRIMARY) {
					System.out.println("Primary clicked!");
					rect.setX(dragContext.mouseAnchorX);
					rect.setY(dragContext.mouseAnchorY);
					rect.setWidth(0);
					rect.setHeight(0);
	
					outerPane.getChildren().add(rect);
				} else if(event.getButton() == MouseButton.SECONDARY) {
					System.out.println("Secondary clicked!");
					dragContext.translateAnchorX = view.getTranslateX();
					dragContext.translateAnchorY = view.getTranslateY();
				}
				
				System.out.println(dragContext.toString());

				event.consume();
			}
		};
		
		EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//Rubberband selection
				if(event.getButton() == MouseButton.PRIMARY) { 
					double offsetX = event.getX() - dragContext.mouseAnchorX;
					double offsetY = event.getY() - dragContext.mouseAnchorY;

					if(offsetX > 0) {
						rect.setWidth(offsetX);
					} else {
						rect.setX(event.getX());
						rect.setWidth(dragContext.mouseAnchorX - rect.getX());
					}

					if(offsetY > 0) {
						rect.setHeight(offsetY);
					} else {
						rect.setY(event.getY());
						rect.setHeight(dragContext.mouseAnchorY - rect.getY());
					}
				//Moving the view
				} else if(event.getButton() == MouseButton.SECONDARY) { 
					//Done
					System.out.println("Secondary dragged!");
					view.setTranslateX(dragContext.translateAnchorX + event.getSceneX() - dragContext.mouseAnchorX);
					view.setTranslateY(dragContext.translateAnchorY + event.getSceneY() - dragContext.mouseAnchorY);
				}

				event.consume();
			}
		};

		EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
			//Done
			@Override
			public void handle(MouseEvent event) {
				// selection like in explorer
				if(event.getButton() == MouseButton.PRIMARY) {
					// selection needs to be cleared before adding new shapes
					if(!event.isControlDown() && !GraphViewSelectionModel.this.isEmpty()) {
						GraphViewSelectionModel.this.clear();
					}
					
					boolean shapeSelected = false;
					for (VertexShape shape : view.getFactory().getVertexShapes()) {
						//mapping the position inside of the graphView to the relative position in the outerPane
						double x = shape.getBoundsInParent().getMinX() + view.getBoundsInParent().getMinX();
						double y = shape.getBoundsInParent().getMinY() + view.getBoundsInParent().getMinY();
						double w = shape.getBoundsInParent().getWidth() * view.getScale();
						double h = shape.getBoundsInParent().getHeight() * view.getScale();
						BoundingBox shapeBounds = new BoundingBox(x,y,w,h);
						
						if(shapeBounds.intersects(rect.getBoundsInParent())) {
							if(event.isControlDown()) { 
								if(GraphViewSelectionModel.this.contains(shape)) {
									GraphViewSelectionModel.this.remove(shape);
								} else {
									GraphViewSelectionModel.this.add(shape);
								}
							} else {
								GraphViewSelectionModel.this.add(shape);
							}
							shapeSelected = true;
						} 
					}
					if(!shapeSelected) {
						GraphViewSelectionModel.this.clear();
					}
					
					GraphViewSelectionModel.this.log();

					rect.setX(0);
					rect.setY(0);
					rect.setWidth(0);
					rect.setHeight(0);

					outerPane.getChildren().remove(rect);
				}

				event.consume();
			}
		};
		
		// Mouse wheel handler: zoom to pivot point.
		EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
			//Done
			@Override
			public void handle(ScrollEvent event) {
				if(event.isControlDown()) { //Enable Scrolling
					double delta = 1.2;
					double scale = view.getScale(); // currently we only use Y, same
														// value is used for X
					double oldScale = scale;

					if (event.getDeltaY() < 0)
						scale /= delta;
					else
						scale *= delta;

					scale = clamp(scale, MIN_SCALE, MAX_SCALE);

					double f = (scale / oldScale) - 1;
					double dx = (event.getSceneX()
							- (view.getBoundsInParent().getWidth() / 2 + view.getBoundsInParent().getMinX()));
					double dy = (event.getSceneY()
							- (view.getBoundsInParent().getHeight() / 2 + view.getBoundsInParent().getMinY()));

					view.setScale(scale);
					// note: pivot value must be untransformed, i. e. without scaling
					view.setPivot(f * dx, f * dy);
				}
				
				event.consume();
			}
		};
		
		private double clamp(double value, double min, double max) {
			if (Double.compare(value, min) < 0)
				return min;

			if (Double.compare(value, max) > 0)
				return max;

			return value;
		}

		private final class DragContext {
			double mouseAnchorX;
			double mouseAnchorY;

			double translateAnchorX;
			double translateAnchorY;
			
			@Override
			public String toString() {
				return "mouseX: " + mouseAnchorX + " mouseY: " + mouseAnchorY + " translateX: " + translateAnchorX + " translateY: " + translateAnchorY;
			}
		}
	}
}