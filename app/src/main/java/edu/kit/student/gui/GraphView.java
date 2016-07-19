package edu.kit.student.gui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.PluginManager;
import edu.kit.student.plugin.VertexFilter;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * A view used for showing and creating a graph in GAns. It supports zooming and
 * other general navigation features.
 * 
 * @author Nicolas
 */
public class GraphView extends Pane {

	private DoubleProperty myScale = new SimpleDoubleProperty(1.0);
	private GraphViewSelectionModel selectionModel;
	private GraphViewGraphFactory graphFactory;
	private LayoutOption layout;
	
	private ContextMenu contextMenu;
	private List<VertexGroup> groups = new LinkedList<VertexGroup>();
	
	private GAnsMediator mediator;

	/**
	 * Constructor.
	 */
	public GraphView(GAnsMediator mediator) {
		this.mediator = mediator;
		 setPrefSize(600, 600);
//		 setStyle("-fx-background-color: lightgrey;");

		// add scale transform
		scaleXProperty().bind(myScale);
		scaleYProperty().bind(myScale);
		
		this.contextMenu = new ContextMenu();
		setupContextMenu();
	}

	/**
	 * Adds a grid to the GraphView, on which the dragging can be mapped.
	 */
	public void addGrid() {
		double w = getBoundsInLocal().getWidth();
		double h = getBoundsInLocal().getHeight();

		// add grid
		Canvas grid = new Canvas(w, h);
		// don't catch mouse events
		grid.setMouseTransparent(true);

		GraphicsContext gc = grid.getGraphicsContext2D();
		gc.setStroke(Color.GRAY);
		gc.setLineWidth(1);

		// draw grid lines
		double offset = 50;
		for (double i = offset; i < w; i += offset) {
			gc.strokeLine(i, 0, i, h);
			gc.strokeLine(0, i, w, i);
		}

		getChildren().add(grid);

		grid.toBack();
	}

	/**
	 * Returns the scale on which the GraphView currently is.
	 * 
	 * @return The scale of the GraphView.
	 */
	public double getScale() {
		return myScale.get();
	}

	/**
	 * Sets the scale of the GraphView.
	 * 
	 * @param scale
	 *            The scale of the GraphView.
	 */
	public void setScale(double scale) {
		myScale.set(scale);
	}

	/**
	 * Sets the pivot so the scrolling follows the mouse position on the
	 * GraphView.
	 * 
	 * @param x
	 *            The x coordinate of the pivot.
	 * @param y
	 *            The y coordinate of the pivot.
	 */
	public void setPivot(double x, double y) {
		setTranslateX(getTranslateX() - x);
		setTranslateY(getTranslateY() - y);
	}

	/**
	 * Sets a graph. Every element in the graph will be generated and then
	 * shown.
	 * 
	 * @param graph
	 *            The graph to be visualized in the view.
	 */
	public void setGraph(ViewableGraph graph) {
		graphFactory = new GraphViewGraphFactory(graph);

		getChildren().addAll(graphFactory.getGraphicalElements());
	}
	
	public void reloadGraph() {
		graphFactory = new GraphViewGraphFactory(graphFactory.getGraph());

		getChildren().clear();
		getChildren().addAll(graphFactory.getGraphicalElements());
		double maxX = 0;
		double maxY = 0;
		for (Node element : getChildren()) {
		    maxX = maxX > element.getBoundsInParent().getMaxX() ? maxX : element.getBoundsInLocal().getMaxX();
		    maxY = maxY > element.getBoundsInParent().getMaxY() ? maxY : element.getBoundsInLocal().getMaxY();
		}
		setPrefSize(maxX, maxY);
	}

	/**
	 * Returns the current {@link GraphViewGraphFactory} from the view.
	 * 
	 * @return The current {@link GraphViewGraphFactory}.
	 */
	public GraphViewGraphFactory getFactory() {
		return graphFactory;
	}
	
	/**
	 * Sets the selection model of the GraphView.
	 * 
	 * @param The selection model of the GraphView.
	 */
	public void setSelectionModel(GraphViewSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		selectionModel.setContexMenu(this.contextMenu);
		dynamicContextMenu();
	}

	/**
	 * Returns the selection model of the GraphView.
	 * 
	 * @return The selection model of the GraphView.
	 */
	public GraphViewSelectionModel getSelectionModel() {
		return this.selectionModel;
	}
	
	public void setCurrentLayoutOption(LayoutOption layout) {
		this.layout = layout;
	}
	
	public LayoutOption getCurrentLayoutOption() {
		return this.layout;
	}
	
	private void setupContextMenu() {
//		MenuItem collapse = new MenuItem("Collapse");
//		collapse.setOnAction(new EventHandler<ActionEvent>() {
//		    public void handle(ActionEvent e) {
//		    	GraphViewGraphFactory factory = GraphView.this.getFactory();
//		    	Set<Vertex> selectedVertices = new HashSet<Vertex>();
//		    	for(VertexShape shape : GraphView.this.getSelectionModel().getSelectedItems()) {
//		    		selectedVertices.add(factory.getVertexFromShape(shape));
//		    	}
//		    	
//		    	//selected vertices will be collapsed and thereby removed from the graph
//		    	//selection must be cleared before collapse(...) is called
//		    	GraphView.this.selectionModel.clear();
//		    	
//		    	factory.getGraph().collapse(selectedVertices);
//		    	GraphView.this.getCurrentLayoutOption().chooseLayout();
//		    	GraphView.this.getCurrentLayoutOption().applyLayout();
//		    	GraphView.this.reloadGraph();
//		    }
//		});
//		
//		MenuItem expand = new MenuItem("Expand");
//		expand.setOnAction(new EventHandler<ActionEvent>() {
//		    public void handle(ActionEvent e) {
//		    	GraphViewGraphFactory factory = GraphView.this.getFactory();
//		    	
//		    	//MenuItem is disabled when there are more than one vertex selected
//		    	VertexShape shape = GraphView.this.getSelectionModel().getSelectedItems().iterator().next();
//		    	
//		    	//selected vertices will be expanded and thereby removed from the graph
//		    	//selection must be cleared before expand(...) is called
//		    	GraphView.this.selectionModel.clear();
//		    	
//		    	//MenuItem is disabled when there is no CollapsedVertex selected
//	    		factory.getGraph().expand((CollapsedVertex)factory.getVertexFromShape(shape));
//		    	GraphView.this.getCurrentLayoutOption().chooseLayout();
//		    	GraphView.this.getCurrentLayoutOption().applyLayout();
//		    	GraphView.this.reloadGraph();
//		    	System.out.println("EdgeCount: " + factory.getGraph().getEdgeSet().size());
//		    }
//		});
		
		MenuItem group = new MenuItem("Add to group");
		group.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	openAddGroupDialog();
		    	openGroupDialog();
		    }
		});
		
		this.contextMenu.getItems().addAll(/*collapse, expand,*/ group);
//		this.contextMenu.setOnShowing(new EventHandler<WindowEvent>() {
//			@Override
//			public void handle(WindowEvent e) {
//				if(selectionModel.getSelectedItems().size() < 2) {
//					collapse.setDisable(true);
//					
//					//only one item can be selected 
//					VertexShape shape = GraphView.this.getSelectionModel().getSelectedItems().iterator().next();
//		    		Vertex vertex = GraphView.this.graphFactory.getVertexFromShape(shape);
//		    		//if the selected vertex is collapsed, expanding is enabled
//	    			expand.setDisable(!(GraphView.this.graphFactory.getGraph().isCollapsed(vertex)));
//					
//				} else {
//					collapse.setDisable(false);
//					expand.setDisable(true);
//				}
//				
//			}
//		});
	}
	
    private List<MenuItem> dynamicMenuListItems = new LinkedList<>();

	private void dynamicContextMenu() {
	    this.getSelectionModel().getSelectedItems().addListener(new SetChangeListener<VertexShape>() {

            @Override
            public void onChanged(SetChangeListener.Change<? extends VertexShape> change) {
                GraphView.this.contextMenu.getItems().removeAll(dynamicMenuListItems);
                dynamicMenuListItems.clear();

                Set<ViewableVertex> vertices = new HashSet<>();
                // Display subgraph actions
                for(VertexShape shape : getSelectionModel().getSelectedItems()) {
                    vertices.add(getFactory().getVertexFromShape(shape));
                }
                for (SubGraphAction action : getFactory().getGraph().getSubGraphActions(vertices)) {
                    MenuItem item = new MenuItem(action.getName());
                    dynamicMenuListItems.add(item);
                    item.setOnAction(new EventHandler<ActionEvent>() {

                        @Override
                        public void handle(ActionEvent event) {
                        	GraphView.this.selectionModel.clear();
		    	
                            action.handle();
                            GraphView.this.getCurrentLayoutOption().chooseLayout();
                            GraphView.this.getCurrentLayoutOption().applyLayout();
                            GraphView.this.reloadGraph();
                        }
                    });
                    GraphView.this.contextMenu.getItems().add(item);
                }
                if (vertices.size() == 1) {
                    // Display vertex actions;
                    for (VertexAction action : getFactory().getGraph().getVertexActions(vertices.iterator().next())) {
                        MenuItem item = new MenuItem(action.getName());
                        dynamicMenuListItems.add(item);
                        item.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                                action.handle();
                                GraphView.this.getCurrentLayoutOption().chooseLayout();
                                GraphView.this.getCurrentLayoutOption().applyLayout();
                                GraphView.this.reloadGraph();
                            }
                        });
                        GraphView.this.contextMenu.getItems().add(item);
                    }
                    // Display links
                    int linkId = vertices.iterator().next().getLink();
                    if (linkId != -1) {
                        MenuItem item = new MenuItem("Open Graph");
                        dynamicMenuListItems.add(item);
                        // set action to open new graph
                        item.setOnAction(new EventHandler<ActionEvent>() {

                            @Override
                            public void handle(ActionEvent event) {
                            	GraphView.this.mediator.openGraph(linkId);
                            }
                        });
                        GraphView.this.contextMenu.getItems().add(item);
                        
                    }
                }
            }
        });
	}
	
	private void openAddGroupDialog() {
		TextInputDialog dialog = new TextInputDialog("New Group");
    	dialog.setTitle("Add group");
    	dialog.setHeaderText(null);
    	dialog.setGraphic(null);
    	dialog.setContentText("Enter new group name:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    	    VertexGroup newGroup = new VertexGroup(result.get(), GraphView.this.getSelectionModel().getSelectedItems());
    	    groups.add(newGroup);
    	}
    }
	
	public void openGroupDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(10, 10, 10, 10));
		
		for(int i = 0; i < groups.size(); i++) {
			VertexGroup group = groups.get(i);
			Label groupName = new Label(group.getName());
			grid.add(groupName, 0, i);
			grid.add(group.getPicker(), 1, i);
		}
		//spacer maybe not needed
		Region spacer = new Region();
		GridPane.setHgrow(spacer, Priority.ALWAYS);
		
		Button removeButton = new Button("Remove");
		VBox root = new VBox(grid, removeButton);
		
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		    	if(!groups.isEmpty()) {
		    		List<String> groupNames = new LinkedList<String>();
			    	groups.forEach((group) -> groupNames.add(group.getName()));
			    	ChoiceDialog<String> dialog = new ChoiceDialog<String>(groupNames.get(0), groupNames);
			    	dialog.setTitle("Remove group");
			    	dialog.setHeaderText(null);
			    	dialog.setGraphic(null);
			    	dialog.setContentText("Select a group:");
			    	Optional<String> result = dialog.showAndWait();
			    	if(result.isPresent()) {
			    		int index = groupNames.indexOf(result.get());
			    	    VertexGroup removedGroup = groups.remove(index);
			    	    removedGroup.uncolorVertices();
			    	    grid.getChildren().remove(index * 2);
			    	    grid.getChildren().remove(removedGroup.getPicker());
			    	}
		    	}
		    }
		});
		
		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		dialog.setTitle("Groups");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.showAndWait();
	}
	
	public void openFilterDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();

		List<VertexFilter> selectedVertexFilter = new LinkedList<VertexFilter>();
		List<EdgeFilter> selectedEdgeFilter = new LinkedList<EdgeFilter>();
		
		TabPane tabPane = new TabPane();
		
		Tab vertexFilterTab = new Tab("Vertices");
		vertexFilterTab.setClosable(false);
		vertexFilterTab.setContent(setupVertexFilterPane(selectedVertexFilter));
		
		Tab edgeFilterTab = new Tab("Edges");
		edgeFilterTab.setClosable(false);
		edgeFilterTab.setContent(setupEdgeFilterPane(selectedEdgeFilter));
		
		tabPane.getTabs().addAll(vertexFilterTab, edgeFilterTab);
		
		dialog.getDialogPane().setContent(tabPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.setTitle("Select Filter");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setWidth(500);
		dialog.setHeight(500);
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.get() == ButtonType.OK) { 
			this.graphFactory.getGraph().setVertexFilter(selectedVertexFilter);
			this.graphFactory.getGraph().setEdgeFilter(selectedEdgeFilter);
		}
		reloadGraph();
	}
	
	private GridPane setupVertexFilterPane(List<VertexFilter> selectedVertexFilter) {
		List<VertexFilter> vertexFilter = PluginManager.getPluginManager().getVertexFilter();
		List<CheckBox> vertexFilterBoxes = new LinkedList<CheckBox>();
		int column = 0;
		int row = 0;
		GridPane vertexFilterPane = new GridPane();
		vertexFilterPane.setHgap(10);
		vertexFilterPane.setVgap(10);
		vertexFilterPane.setPadding(new Insets(10, 10, 10, 10));
		ColumnConstraints vertexCol1 = new ColumnConstraints();
		vertexCol1.setPercentWidth(25);
	    ColumnConstraints vertexCol2 = new ColumnConstraints();
	    vertexCol2.setPercentWidth(25);
	    ColumnConstraints vertexCol3 = new ColumnConstraints();
	    vertexCol3.setPercentWidth(25);
	    ColumnConstraints vertexCol4 = new ColumnConstraints();
	    vertexCol4.setPercentWidth(25);
	    vertexFilterPane.getColumnConstraints().addAll(vertexCol1,vertexCol2,vertexCol3,vertexCol4);
		for(VertexFilter filter : vertexFilter) {
			if(column == 4) {
				column = 0;
				row++;
			}
			boolean selected = false;
			if(this.graphFactory.getGraph().getActiveVertexFilter().contains(filter)) {
				selectedVertexFilter.add(filter);
				selected = true;
			}
			CheckBox box = new CheckBox(filter.getName());
			box.setSelected(selected);
			box.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					CheckBox box = (CheckBox)event.getSource();
					int filterIndex = vertexFilterBoxes.indexOf(box);
					if(box.isSelected()) {
						selectedVertexFilter.add(vertexFilter.get(filterIndex));
					} else {
						selectedVertexFilter.remove(vertexFilter.get(filterIndex));
					}
					
				}
			});
			vertexFilterBoxes.add(box);
			vertexFilterPane.add(box, column, row);
			column++;
		}
		return vertexFilterPane;
	}
	
	//Basically a copy of setupVertexFilterPane, since there is no mutual parent class
	private GridPane setupEdgeFilterPane(List<EdgeFilter> selectedEdgeFilter) {
		List<EdgeFilter> edgeFilter = PluginManager.getPluginManager().getEdgeFilter();
		List<CheckBox> edgeFilterBoxes = new LinkedList<CheckBox>();
		int column = 0;
		int row = 0;
		GridPane edgeFilterPane = new GridPane();
		edgeFilterPane.setHgap(10);
		edgeFilterPane.setVgap(10);
		edgeFilterPane.setPadding(new Insets(10, 10, 10, 10));
		ColumnConstraints edgeCol1 = new ColumnConstraints();
		edgeCol1.setPercentWidth(25);
	    ColumnConstraints edgeCol2 = new ColumnConstraints();
	    edgeCol2.setPercentWidth(25);
	    ColumnConstraints edgeCol3 = new ColumnConstraints();
	    edgeCol3.setPercentWidth(25);
	    ColumnConstraints edgeCol4 = new ColumnConstraints();
	    edgeCol4.setPercentWidth(25);
	    edgeFilterPane.getColumnConstraints().addAll(edgeCol1,edgeCol2,edgeCol3,edgeCol4);
		for(EdgeFilter filter : edgeFilter) {
			if(column == 4) {
				column = 0;
				row++;
			}
			boolean selected = false;
			if(this.graphFactory.getGraph().getActiveEdgeFilter().contains(filter)) {
				selectedEdgeFilter.add(filter);
				selected = true;
			}
			CheckBox box = new CheckBox(filter.getName());
			box.setSelected(selected);
			box.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					CheckBox box = (CheckBox)event.getSource();
					int filterIndex = edgeFilterBoxes.indexOf(box);
					if(box.isSelected()) {
						selectedEdgeFilter.add(edgeFilter.get(filterIndex));
					} else {
						selectedEdgeFilter.remove(edgeFilter.get(filterIndex));
					}
					
				}
			});
			edgeFilterBoxes.add(box);
			edgeFilterPane.add(box, column, row);
			column++;
		}
		return edgeFilterPane;
	}
}
