package edu.kit.student.gui;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.SetChangeListener;
import javafx.scene.shape.Shape;
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
 * @author Nicolas Boltz, Lucas Steinmann
 */
class GraphViewSelectionModel {

    private final Logger logger = LoggerFactory.getLogger(GraphViewSelectionModel.class);
	private ObservableSet<VertexShape> selectedVertexShapes;
	private ObservableSet<EdgeShape> selectedEdgeShapes;
	private ObservableSet<GAnsGraphElement> selectedShapes;
	private SelectionGestures rubberband;

	GraphViewSelectionModel(GraphViewPaneStack viewPanes) {
		selectedVertexShapes = FXCollections.observableSet(new HashSet<VertexShape>());
		selectedEdgeShapes = FXCollections.observableSet(new HashSet<EdgeShape>());
		selectedShapes = FXCollections.observableSet(new HashSet<GAnsGraphElement>());

		// Sync super set with individual sets.
		SetChangeListener<GAnsGraphElement> syncSetListener = change -> {
			selectedShapes.add(change.getElementAdded());
			selectedShapes.remove(change.getElementRemoved());
		};
		selectedVertexShapes.addListener(syncSetListener);
		selectedEdgeShapes.addListener(syncSetListener);

		rubberband = new SelectionGestures(viewPanes);
	}

	private void addVertex(VertexShape node) {
		node.setStyle("-fx-effect: dropshadow(three-pass-box, red, 4, 4, 0, 0);");
		selectedVertexShapes.add(node);
	}

	private void addEdge(EdgeShape edge) {
		edge.setStyle("-fx-effect: dropshadow(three-pass-box, red, 4, 4, 0, 0);");
		selectedEdgeShapes.add(edge);
	}

	private void addAllVertices(Collection<VertexShape> nodes) {
		for (VertexShape node : nodes) {
			addVertex(node);
		}
	}

	private void addAllEdges(Collection<EdgeShape> edges) {
		for (EdgeShape edge : edges) {
			addEdge(edge);
		}
	}

	private void removeVertex(VertexShape node) {
		node.setStyle(node.getVertexStyle());
		selectedVertexShapes.remove(node);
	}

	private void removeEdge(EdgeShape edge) {
		edge.setStyle(edge.getEdgeStyle());
		selectedEdgeShapes.remove(edge);
	}

	void clear() {
		while (!selectedVertexShapes.isEmpty()) {
			removeVertex(selectedVertexShapes.iterator().next());
		}
		while (!selectedEdgeShapes.isEmpty()) {
			removeEdge(selectedEdgeShapes.iterator().next());
		}
	}

	boolean contains(VertexShape node) {
		return selectedVertexShapes.contains(node);
	}

	boolean contains(EdgeShape edge) {
		return selectedEdgeShapes.contains(edge);
	}

	/**
	 * Returns true if this {@link GraphViewSelectionModel} doesn't contain
	 * any {@link VertexShape}s or {@link EdgeShape}s.
	 * @return true if no {@link VertexShape}s or {@link EdgeShape}s are in this selection model, false otherwise.
	 */
	boolean isEmpty() {
		return selectedVertexShapes.isEmpty() && selectedEdgeShapes.isEmpty();
	}

	private void log() {
	    logger.debug("Selected rectangle " + rubberband.rect.toString());
        logger.debug("Items in model: Vertices {" + Arrays.asList(selectedVertexShapes.toArray()) + "}" +
				" Edges {" + Arrays.asList(selectedEdgeShapes.toArray()) + "}");
	}

	ObservableSet<VertexShape> getSelectedVertexShapes() {
		return selectedVertexShapes;
	}

	ObservableSet<EdgeShape> getSelectedEdgeShapes() {
		return selectedEdgeShapes;
	}

	ObservableSet<GAnsGraphElement> getSelectedShapes() {
		return selectedShapes;
	}

	void setContextMenu(ContextMenu menu) {
		rubberband.setContextMenu(menu);
	}

	private class SelectionGestures {
		// Stores the state of the current dragging action (start point etc.)
		private final DragContext dragContext = new DragContext();
		// The rectangle which is drawn to visualize the area to select
		private final Rectangle rect;
		private final GraphViewPaneStack viewPanes;
		private final GraphView view;
		private ContextMenu menu;

		private SelectionGestures(GraphViewPaneStack viewPanes) {
			this.viewPanes = viewPanes;
			this.view = viewPanes.getGraphView();

			rect = new Rectangle(0, 0, 0, 0);
			rect.setStroke(Color.BLUE);
			rect.setStrokeWidth(1);
			rect.setStrokeLineCap(StrokeLineCap.ROUND);
			rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

			// Hide menu when zooming (looks weird when it is drawn while graph is scaled)
			viewPanes.getScaleProperty().addListener((ob, n, o) -> menu.hide());

			viewPanes.getRoot().addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
			viewPanes.getRoot().addEventFilter(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
			viewPanes.getRoot().addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);
		}

		void setContextMenu(ContextMenu menu) {
			this.menu = menu;
		}


		private void showRect() {
		    viewPanes.getRoot().getChildren().add(rect);
		}

		private void hideRect() {
			viewPanes.getRoot().getChildren().remove(rect);
		}

		EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				menu.hide();
				dragContext.mouseAnchorX = event.getX();
				dragContext.mouseAnchorY = event.getY();

				if (event.getButton() == MouseButton.PRIMARY) {
					// If primary is pressed a new selection is started
					// => show empty rectangle at mouse cursor position
                    rect.setX(dragContext.mouseAnchorX);
                    rect.setY(dragContext.mouseAnchorY);
                    rect.setWidth(0);
                    rect.setHeight(0);
					showRect();
				} else if (event.getButton() == MouseButton.SECONDARY) {
					// Open menu on right mouse button click.
					if (!event.isControlDown()) {
						// Instead of rectangle use a small hit box at cursor position to intersect elements.
						// Area must be greater than zero as the area of the intersection is used to determine if
						// a collision is taking place.
						final BoundingBox clickBound = new BoundingBox(event.getSceneX(),event.getSceneY(),1,1);

						// Get all intersected elements in the graph
						// should mostly contain one item (if there are nodes on top of each other there
						// can be more items contained)
						final Set<VertexShape> vertexShapes = intersectedVertexShapes(clickBound);
						final Set<EdgeShape> edgeShapes = intersectedEdgeShapes(clickBound);

						if (vertexShapes.isEmpty() && edgeShapes.isEmpty()) {
							// If user draws empty rectangle => clear selection (even if control is pressed)
							clear();
						} else {
							// If the selected element(s) is not in the set of previously selected elements
							// clear the set
                            if (!(getSelectedVertexShapes().containsAll(vertexShapes)
									&& getSelectedEdgeShapes().containsAll(edgeShapes))) {
								clear();
							}

							// Add all intersected elements to the selection
							addAllVertices(vertexShapes);
							addAllEdges(edgeShapes);
							menu.show(viewPanes.getRoot(), event.getScreenX(),event.getScreenY());
						}
					}
				}
			}
		};

		EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.PRIMARY) {
				    // Offset from initial anchor point where the mouse has been clicked
					// NOT the delta between last and this event.
					final double offsetX = event.getX() - dragContext.mouseAnchorX;
					final double offsetY = event.getY() - dragContext.mouseAnchorY;

					if (offsetX >= 0) {
						rect.setWidth(offsetX);
					} else {
						rect.setX(event.getX());
						rect.setWidth(dragContext.mouseAnchorX - rect.getX());
					}

					if (offsetY >= 0) {
						rect.setHeight(offsetY);
					} else {
						rect.setY(event.getY());
						rect.setHeight(dragContext.mouseAnchorY - rect.getY());
					}
					event.consume();
				}
			}
		};

		EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				// selection like in explorer
				if (event.getButton() == MouseButton.PRIMARY) {
					if (!event.isControlDown() && !GraphViewSelectionModel.this.isEmpty()) {
						if (event.getClickCount() == 2){
						    // When double clicked on element
							// => launch action of first menu item.
							if (menu.getItems().size() > 0) {
								menu.getItems().iterator().next().fire();
							}
						}

						// selection needs to be cleared before adding new shapes
						GraphViewSelectionModel.this.clear();
					}

					// Get all intersected elements in the graph
                    Bounds selectionBounds = rect.localToScene(rect.getBoundsInLocal());
					final Set<VertexShape> vertexShapes = intersectedVertexShapes(selectionBounds);
					final Set<EdgeShape> edgeShapes = intersectedEdgeShapes(selectionBounds);

					if (vertexShapes.isEmpty() && edgeShapes.isEmpty()) {
					    // If user draws empty rectangle => clear selection (even if control is pressed)
						GraphViewSelectionModel.this.clear();
					} else {
					    // Add all intersected elements to the selection
						// or remove them when control is pressed and they are already selected.
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
					hideRect();

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

		/**
		 * Checks if there is a non empty intersection between the
		 * specified {@link Bounds} in scene space and a given {@link GAnsGraphElement}.
		 * This uses the actual shape of element and not its bounding box.
		 * @param bounds the {@link Bounds} to intersect
		 * @param element the {@link GAnsGraphElement} to intersect
		 * @return true if the element intersects with the bounds.
		 */
		private boolean intersects(Bounds bounds, GAnsGraphElement element) {
			Shape intersect = Shape.intersect(element.getElementShape(),
					new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
			return (intersect.getBoundsInLocal().getWidth() != -1 || intersect.getBoundsInLocal().getHeight() != -1);
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
