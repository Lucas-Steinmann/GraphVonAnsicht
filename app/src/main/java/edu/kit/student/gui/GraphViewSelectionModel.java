package edu.kit.student.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.scene.Group;
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

    private final Logger logger = LoggerFactory.getLogger(GraphViewSelectionModel.class);
	private ObservableSet<VertexShape> selectedVertexShapes;
	private ObservableSet<EdgeShape> selectedEdgeShapes;
	private SceneGesturesAndSelection rubberband;

	public GraphViewSelectionModel(GraphViewPanes viewPanes) {
		selectedVertexShapes = FXCollections.observableSet(new HashSet<VertexShape>());
		selectedEdgeShapes = FXCollections.observableSet(new HashSet<EdgeShape>());
		rubberband = new SceneGesturesAndSelection(viewPanes);
	}

	public void addVertex(VertexShape node) {
		node.setStyle("-fx-effect: dropshadow(three-pass-box, red, 4, 4, 0, 0);");
		selectedVertexShapes.add(node);
	}

	public void addEdge(EdgeShape edge) {
		edge.setStyle("-fx-effect: dropshadow(three-pass-box, red, 4, 4, 0, 0);");
		selectedEdgeShapes.add(edge);
	}

	public void addAllVertices(Collection<VertexShape> nodes) {
		for (VertexShape node : nodes) {
			addVertex(node);
		}
	}

	public void addAllEdges(Collection<EdgeShape> edges) {
		for (EdgeShape edge : edges) {
			addEdge(edge);
		}
	}

	public void removeVertex(VertexShape node) {
		node.setStyle(node.getVertexStyle());
		selectedVertexShapes.remove(node);
	}

	public void removeEdge(EdgeShape edge) {
		edge.setStyle(edge.getStyle());
		selectedEdgeShapes.remove(edge);
	}

	public void clear() {
		while (!selectedVertexShapes.isEmpty()) {
			removeVertex(selectedVertexShapes.iterator().next());
		}
		while (!selectedEdgeShapes.isEmpty()) {
			removeEdge(selectedEdgeShapes.iterator().next());
		}
		log();
	}

	public boolean contains(VertexShape node) {
		return selectedVertexShapes.contains(node);
	}

	public boolean contains(EdgeShape edge) {
		return selectedEdgeShapes.contains(edge);
	}

	/**
	 * Returns true if this {@link GraphViewSelectionModel} doesn't contain
	 * any {@link VertexShape}s or {@link EdgeShape}s.
	 * @return true if no {@link VertexShape}s or {@link EdgeShape}s are in this selection model, false otherwise.
	 */
	public boolean isEmpty() {
		return selectedVertexShapes.isEmpty() && selectedEdgeShapes.isEmpty();
	}

	public void log() {
	    logger.debug("Selected rectangle " + rubberband.rect.toString());
        logger.debug("Items in model: Vertices {" + Arrays.asList(selectedVertexShapes.toArray()) + "}" +
				" Edges {" + Arrays.asList(selectedEdgeShapes.toArray()) + "}");
	}

	public ObservableSet<VertexShape> getSelectedVertexShapes() {
		return selectedVertexShapes;
	}

	public ObservableSet<EdgeShape> getSelectedEdgeShapes() {
		return selectedEdgeShapes;
	}

	public void setContextMenu(ContextMenu menu) {
		rubberband.setContextMenu(menu);
	}

	private class SceneGesturesAndSelection {
		final DragContext dragContext = new DragContext();
		private Rectangle rect;
		private GraphViewPanes viewPanes;
		private GraphView view;
		private ContextMenu menu;

		public SceneGesturesAndSelection(GraphViewPanes viewPanes) {
			this.viewPanes = viewPanes;
			this.view = viewPanes.getGraphView();

			rect = new Rectangle(0, 0, 0, 0);
			rect.setStroke(Color.BLUE);
			rect.setStrokeWidth(1);
			rect.setStrokeLineCap(StrokeLineCap.ROUND);
			rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

			viewPanes.getCanvas().addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
			viewPanes.getCanvas().addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
			viewPanes.getCanvas().addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
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

				if (event.getButton() == MouseButton.PRIMARY) {
					rect.setX(dragContext.mouseAnchorX);
					rect.setY(dragContext.mouseAnchorY);
					rect.setWidth(0);
					rect.setHeight(0);

					view.getChildren().add(rect);
				}

				event.consume();
			}
		};

		EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				//Rubberband selection
				if (event.getButton() == MouseButton.PRIMARY) {
					double offsetX = event.getX() - dragContext.mouseAnchorX;
					double offsetY = event.getY() - dragContext.mouseAnchorY;

					if (offsetX > 0) {
						rect.setWidth(offsetX);
					} else {
						rect.setX(event.getX());
						rect.setWidth(dragContext.mouseAnchorX - rect.getX());
					}

					if (offsetY > 0) {
						rect.setHeight(offsetY);
					} else {
						rect.setY(event.getY());
						rect.setHeight(dragContext.mouseAnchorY - rect.getY());
					}
				}

				event.consume();
			}
		};

		EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// selection like in explorer
				if (event.getButton() == MouseButton.PRIMARY) {
					// selection needs to be cleared before adding new shapes
					if (!event.isControlDown() && !GraphViewSelectionModel.this.isEmpty()) {
						if (event.getClickCount() == 2){
							if (menu.getItems().size() > 0) {
								menu.getItems().iterator().next().fire();
							}
						}
						GraphViewSelectionModel.this.clear();
					}
					
					Set<VertexShape> vertexShapes = intersectedVertexShapes(rect.getBoundsInParent());
					Set<EdgeShape> edgeShapes = new HashSet<>(); //intersectedEdgeShapes(rect.getBoundsInParent());
					if (vertexShapes.isEmpty() && edgeShapes.isEmpty()) {
						GraphViewSelectionModel.this.clear();
					} else {
						for (VertexShape shape : vertexShapes) {
							if (event.isControlDown() && GraphViewSelectionModel.this.contains(shape)) {
                                GraphViewSelectionModel.this.removeVertex(shape);
							} else {
								GraphViewSelectionModel.this.addVertex(shape);
							}
						}
						for (EdgeShape shape : edgeShapes) {
							if (event.isControlDown() && GraphViewSelectionModel.this.contains(shape)) {
								GraphViewSelectionModel.this.removeEdge(shape);
							} else {
								GraphViewSelectionModel.this.addEdge(shape);
							}
						}
					}

					GraphViewSelectionModel.this.log();

					rect.setX(0);
					rect.setY(0);
					rect.setWidth(0);
					rect.setHeight(0);

					view.getChildren().remove(rect);
				} else if (event.getButton() == MouseButton.SECONDARY) {
					if (!event.isControlDown()) {
						BoundingBox clickBound = new BoundingBox(event.getX(),event.getY(),0,0);
						//should mostly contain one item (if there are nodes on top of each other there
						// can be more items contained)
						Set<VertexShape> vertexShapes = intersectedVertexShapes(clickBound);
						Set<EdgeShape> edgeShapes = new HashSet<>(); //intersectedEdgeShapes(clickBound);
						if (vertexShapes.isEmpty() && edgeShapes.isEmpty()) {
							GraphViewSelectionModel.this.clear();
						} else {
							if (!(GraphViewSelectionModel.this.selectedVertexShapes.containsAll(vertexShapes)
									&& GraphViewSelectionModel.this.selectedEdgeShapes.containsAll(edgeShapes))) {
								GraphViewSelectionModel.this.clear();
								GraphViewSelectionModel.this.addAllVertices(vertexShapes);
								GraphViewSelectionModel.this.addAllEdges(edgeShapes);
							}
							menu.show(viewPanes.getContentGroup(), event.getScreenX(),event.getScreenY());
						} 
					}
					
					GraphViewSelectionModel.this.log();
				}

				event.consume();
			}
		};
		
        /**
         * Returns the {@link VertexShape}s, that are intersected by the specified bound.
		 *
         * @param bounds the bounds to intersect the {@link VertexShape}s with.
         * @return the set of {@link VertexShape}s, which intersect with the bounds
         */
		private Set<VertexShape> intersectedVertexShapes(Bounds bounds) {
            return view.getFactory().getVertexShapes().stream()
                    .filter(v -> intersects(bounds, v))
                    .collect(Collectors.toSet());
		}

		/**
		 * Returns the {@link EdgeShape}s, that are intersected by the specified bound.
		 *
		 * @param bounds the bounds to intersect the {@link EdgeShape}s with.
		 * @return the set of {@link EdgeShape}s, which intersect with the bounds
		 */
		private Set<EdgeShape> intersectedEdgeShapes(Bounds bounds) {
			return view.getFactory().getEdgeShapes().stream()
					.filter(v -> intersects(bounds, v))
					.collect(Collectors.toSet());
		}

		private boolean intersects(Bounds bounds, GAnsGraphElement element) {
			//mapping the position inside of the graphView to the relative position in the scrollPane
			double x = (element.getBoundsInParent().getMinX() * viewPanes.getScale()) +
					viewPanes.getContentGroup().getBoundsInParent().getMinX();
			double y = (element.getBoundsInParent().getMinY() * viewPanes.getScale()) +
					viewPanes.getContentGroup().getBoundsInParent().getMinY();
			double w = element.getBoundsInParent().getWidth() * viewPanes.getScale();
			double h = element.getBoundsInParent().getHeight() * viewPanes.getScale();
			System.out.println(viewPanes.getContentGroup().parentToLocal(rect.getBoundsInParent()));
			BoundingBox shapeBounds = new BoundingBox(x,y,w,h);

			return shapeBounds.intersects(bounds);
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
