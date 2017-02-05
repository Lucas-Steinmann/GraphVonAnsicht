package edu.kit.student.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.control.ContextMenu;
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

    final Logger logger = LoggerFactory.getLogger(GraphViewSelectionModel.class);
	private ObservableSet<VertexShape> selection;
	private SceneGesturesAndSelection rubberband;

	public GraphViewSelectionModel(GraphViewPanes viewPanes) {
		selection = FXCollections.observableSet(new HashSet<VertexShape>());
		rubberband = new SceneGesturesAndSelection(viewPanes);
	}

	public void add(VertexShape node) {
		node.setStyle("-fx-effect: dropshadow(three-pass-box, red, 4, 4, 0, 0);");
		selection.add(node);
	}
	
	public void addAll(Collection<VertexShape> nodes) {
		for(VertexShape node : nodes) {
			add(node);
		}
	}

	public void remove(VertexShape node) {
		node.setStyle(node.getVertexStyle());
		selection.remove(node);
	}

	public void clear() {
		while (!selection.isEmpty()) {
			remove(selection.iterator().next());
		}
		log();
	}

	public boolean contains(VertexShape node) {
		return selection.contains(node);
	}
	
	public boolean isEmpty() {
		return selection.isEmpty();
	}

	public void log() {
        logger.debug("Items in model: " + Arrays.asList(selection.toArray()));
	}

	public ObservableSet<VertexShape> getSelectedItems() {
		return selection;
	}
	
	public void setContexMenu(ContextMenu menu) {
		rubberband.setContextMenu(menu);
	}

	private class SceneGesturesAndSelection {
		final DragContext dragContext = new DragContext();
		private Rectangle rect;
		private GraphViewPanes viewPanes;
		private Pane outerPane;
		private GraphView view;
		private ContextMenu menu;

		public SceneGesturesAndSelection(GraphViewPanes viewPanes) {
			this.viewPanes = viewPanes;
			this.outerPane = viewPanes.getOuterPane();
			this.view = viewPanes.getGraphView();

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
		
		public void setContextMenu(ContextMenu menu) {
			this.menu = menu;
		}

		EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				menu.hide();
				dragContext.mouseAnchorX = event.getX();
				dragContext.mouseAnchorY = event.getY();

				if(event.getButton() == MouseButton.PRIMARY) {
					rect.setX(dragContext.mouseAnchorX);
					rect.setY(dragContext.mouseAnchorY);
					rect.setWidth(0);
					rect.setHeight(0);
	
					outerPane.getChildren().add(rect);
				} else if(event.getButton() == MouseButton.SECONDARY) {
					if(event.isControlDown()) {
						dragContext.translateAnchorX = viewPanes.getInnerPane().getTranslateX();
						dragContext.translateAnchorY = viewPanes.getInnerPane().getTranslateY();
					}
				}

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
					if(event.isControlDown()) {
						viewPanes.getInnerPane().setTranslateX(dragContext.translateAnchorX + event.getX() - dragContext.mouseAnchorX);
						viewPanes.getInnerPane().setTranslateY(dragContext.translateAnchorY + event.getY() - dragContext.mouseAnchorY);
					}
				}

				event.consume();
			}
		};

		EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// selection like in explorer
				if(event.getButton() == MouseButton.PRIMARY) {
					// selection needs to be cleared before adding new shapes
					if(!event.isControlDown() && !GraphViewSelectionModel.this.isEmpty()) {
						if(event.getClickCount() == 2){
							if (menu.getItems().size() > 0) {
								menu.getItems().iterator().next().fire();;
								
							}
						}
						GraphViewSelectionModel.this.clear();
					}
					
					Set<VertexShape> shapes = intersectedShapes(rect.getBoundsInParent());
					if(shapes.isEmpty()) {
						GraphViewSelectionModel.this.clear();
					} else {
						for(VertexShape shape : shapes) {
							if(event.isControlDown()) { 
								if(GraphViewSelectionModel.this.contains(shape)) {
									GraphViewSelectionModel.this.remove(shape);
								} else {
									GraphViewSelectionModel.this.add(shape);
								}
							} else {
								GraphViewSelectionModel.this.add(shape);
							}
						}
					}
					
					GraphViewSelectionModel.this.log();

					rect.setX(0);
					rect.setY(0);
					rect.setWidth(0);
					rect.setHeight(0);

					outerPane.getChildren().remove(rect);
				} else if (event.getButton() == MouseButton.SECONDARY) {
					if(!event.isControlDown()) {
						BoundingBox clickBound = new BoundingBox(event.getX(),event.getY(),0,0);
						//should mostly contain one item(if there are nodes on top of each other there can be more items contained)
						Set<VertexShape> selection = intersectedShapes(clickBound);
						if(selection.isEmpty()) {
							GraphViewSelectionModel.this.clear();
						} else {
							if(!GraphViewSelectionModel.this.selection.containsAll(selection)) {
								GraphViewSelectionModel.this.clear();
								GraphViewSelectionModel.this.addAll(selection);
							}
							menu.show(viewPanes.getInnerPane(), event.getScreenX(),event.getScreenY());
						} 
					}
					
					GraphViewSelectionModel.this.log();
				}

				event.consume();
			}
		};
		
		// Mouse wheel handler: zoom to pivot point.
		EventHandler<ScrollEvent> onScrollEventHandler = new EventHandler<ScrollEvent>() {
			@Override
			public void handle(ScrollEvent event) {
				if(event.isControlDown()) { 
					//Enable Zooming
					menu.hide();
					double delta = 1.2d;

	                double scale = viewPanes.getScale(); // currently we only use Y, same valueId is used for X
	                double oldScale = scale;

	                viewPanes.setDeltaY(event.getDeltaY()); 
	                if (viewPanes.getDeltaY() < 0) {
	                    scale /= delta;
	                } else {
	                    scale *= delta;
	                }

	                scale = clamp(scale, MIN_SCALE, MAX_SCALE);
	                
	                double f = (scale / oldScale) - 1;

	                double dx = (event.getX() - (viewPanes.getInnerPane().getBoundsInParent().getWidth() / 2 + 
	                		viewPanes.getInnerPane().getBoundsInParent().getMinX()));
	                double dy = (event.getY() - (viewPanes.getInnerPane().getBoundsInParent().getHeight() / 2 + 
	                		viewPanes.getInnerPane().getBoundsInParent().getMinY()));

	                viewPanes.setPivot(f * dx, f * dy, scale);
				} else { 
					//Scrolling the scrollpane if ctrl is not pressed
					if (event.getDeltaY() > 0)
						viewPanes.getScrollPane().setVvalue(viewPanes.getScrollPane().getVvalue() - 0.1);
	                else
	                	viewPanes.getScrollPane().setVvalue(viewPanes.getScrollPane().getVvalue() + 0.1);
				}
				
				event.consume();
			}
		};
		
		// Returns the VertexShapes that are intersected by the given bound
		private Set<VertexShape> intersectedShapes(Bounds bound) {
			Set<VertexShape> shapes = new HashSet<VertexShape>();
			for(VertexShape shape : view.getFactory().getVertexShapes()) {
				//mapping the position inside of the graphView to the relative position in the outerPane
				double x = (shape.getBoundsInParent().getMinX() * viewPanes.getScale()) + 
						viewPanes.getInnerPane().getBoundsInParent().getMinX();
				double y = (shape.getBoundsInParent().getMinY() * viewPanes.getScale()) + 
						viewPanes.getInnerPane().getBoundsInParent().getMinY();
				double w = shape.getBoundsInParent().getWidth() * viewPanes.getScale();
				double h = shape.getBoundsInParent().getHeight() * viewPanes.getScale();
				BoundingBox shapeBounds = new BoundingBox(x,y,w,h);
				
				if(shapeBounds.intersects(bound)) {
					shapes.add(shape);
				} 
			}
			return shapes;
		}
		
		// Clamps the given valueId between the given min and max
		private double clamp(double value, double min, double max) {
			if (Double.compare(value, min) < 0)
				return min;

			if (Double.compare(value, max) > 0)
				return max;

			return value;
		}

		private final class DragContext {
			//Anchor for the mouse position
			double mouseAnchorX;
			double mouseAnchorY;

			//Anchor for the translate position of the view
			double translateAnchorX;
			double translateAnchorY;
			
			@Override
			public String toString() {
				return "mouseX: " + mouseAnchorX + " mouseY: " + 
						mouseAnchorY + " translateX: " + translateAnchorX + 
						" translateY: " + translateAnchorY;
			}
		}
	}
}