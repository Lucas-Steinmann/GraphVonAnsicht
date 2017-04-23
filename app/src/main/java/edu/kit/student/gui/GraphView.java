package edu.kit.student.gui;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.PluginManager;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.util.LanguageManager;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

/**
 * A view used for showing and creating a graph in GAns. It supports zooming and
 * other general navigation features.
 * 
 * @author Nicolas
 */
public class GraphView extends Pane {

	private GraphViewSelectionModel selectionModel;
	private GraphViewGraphFactory graphFactory;
	private LayoutOption layout;
	
	private ContextMenu contextMenu;
    private List<MenuItem> dynamicMenuListItems = new LinkedList<>();
    
	private GroupManager groupManager;
	
	private GAnsMediator mediator;

	/**
	 * Constructor.
	 * 
	 * @param mediator to connect with context menu
	 */
	public GraphView(GAnsMediator mediator) {
		this.mediator = mediator;
		this.contextMenu = new ContextMenu();
		setupContextMenu();
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
		groupManager = new GroupManager(graphFactory);

		getChildren().addAll(graphFactory.getGraphicalElements());
	}
	
	public void reloadGraph() {
		graphFactory.refreshGraph();
		
		getChildren().clear();
		getChildren().addAll(graphFactory.getGraphicalElements());
		double maxX = 0;
		double maxY = 0;
		for (Node element : getChildren()) {
		    maxX = maxX > element.getBoundsInParent().getMaxX() ? maxX : element.getBoundsInLocal().getMaxX();
		    maxY = maxY > element.getBoundsInParent().getMaxY() ? maxY : element.getBoundsInLocal().getMaxY();
		}
		setPrefSize(maxX, maxY);
		
		groupManager.applyGroups();
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
	 * @param selectionModel of the GraphView.
	 */
	public void setSelectionModel(GraphViewSelectionModel selectionModel) {
		this.selectionModel = selectionModel;
		selectionModel.setContextMenu(this.contextMenu);
		selectionModel.getSelectedVertexShapes().addListener(onSelectionChanged);
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
		MenuItem group = new MenuItem(LanguageManager.getInstance().get("ctx_group"));
		group.setOnAction(e ->
		{
            Set<ViewableVertex> selectedVertices = new HashSet<>();
            GraphView.this.getSelectionModel().getSelectedVertexShapes().forEach(
                    shape -> selectedVertices.add(graphFactory.getVertexFromShape(shape)));
            if(groupManager.openAddGroupDialog(selectedVertices)) {
                openGroupDialog();
                selectionModel.clear();
            }
        });
		 
		this.contextMenu.getItems().addAll(group);
	}

	/**
	 * Fetches the applicable actions from the current selection and
	 * adds them as menu items.
	 */
    private SetChangeListener<VertexShape> onSelectionChanged = change -> {
        GraphView.this.contextMenu.getItems().removeAll(dynamicMenuListItems);
        dynamicMenuListItems.clear();

        // Map the current selection shapes back to the actual GraphElements
        Set<ViewableVertex> vertices = new HashSet<>();
        for(VertexShape shape : getSelectionModel().getSelectedVertexShapes()) {
            vertices.add(getFactory().getVertexFromShape(shape));
        }

        int menuIdx = 0;
        for (SubGraphAction action : getFactory().getGraph().getSubGraphActions(vertices)) {
            // For every action applicable on the selection create a menu item.
            MenuItem item = new MenuItem(action.getName());
            dynamicMenuListItems.add(item);
            System.out.println(action.getName());
            item.setOnAction(event -> {
                GraphView.this.selectionModel.clear();

                action.handle();
                // TODO: Instead of layouting always, give actions a field
				// where they specify if they need the graph to be layouted again
                GraphView.this.getCurrentLayoutOption().chooseLayout();
                GraphView.this.getCurrentLayoutOption().applyLayout();
                GraphView.this.reloadGraph();
            });
            GraphView.this.contextMenu.getItems().add(menuIdx++, item);
        }
        // Set menu idx to 0 to add following items to the start of the menu.
        menuIdx = 0;
        if (vertices.size() == 1) {
            ViewableVertex vertex = vertices.iterator().next();
            // Display link
            int linkId = vertex.getLink();
            if (linkId != -1) {
                MenuItem item = new MenuItem(LanguageManager.getInstance().get("ctx_open_graph"));
                dynamicMenuListItems.add(item);
                // set action to open new graph
                item.setOnAction(event -> GraphView.this.mediator.openGraph(linkId));
                GraphView.this.contextMenu.getItems().add(menuIdx++, item);

            }
            // Display vertex actions
            for (VertexAction action : getFactory().getGraph().getVertexActions(vertex)) {
                MenuItem item = new MenuItem(action.getName());
                dynamicMenuListItems.add(item);
                item.setOnAction(event -> {
                    action.handle();
                    GraphView.this.getCurrentLayoutOption().chooseLayout();
                    GraphView.this.getCurrentLayoutOption().applyLayout();
                    GraphView.this.reloadGraph();
                });
                GraphView.this.contextMenu.getItems().add(menuIdx++, item);
            }
        }
    };

	public void openGroupDialog() {
		groupManager.openGroupDialog();
	}
	
	public void openFilterDialog() {
		Dialog<ButtonType> dialog = new Dialog<>();

		List<VertexFilter> selectedVertexFilter = new LinkedList<>();
		List<EdgeFilter> selectedEdgeFilter = new LinkedList<>();
		
		TabPane tabPane = new TabPane();
		
		Tab vertexFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_vertices"));
		vertexFilterTab.setClosable(false);
		vertexFilterTab.setContent(setupVertexFilterPane(selectedVertexFilter));
		List<VertexFilter> vertexBackup = new LinkedList<>(selectedVertexFilter);
		
		Tab edgeFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_edges"));
		edgeFilterTab.setClosable(false);
		edgeFilterTab.setContent(setupEdgeFilterPane(selectedEdgeFilter));
		List<EdgeFilter> edgeBackup = new LinkedList<>(selectedEdgeFilter);
		
		tabPane.getTabs().addAll(vertexFilterTab, edgeFilterTab);
		
		dialog.getDialogPane().setContent(tabPane);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
		dialog.setTitle(LanguageManager.getInstance().get("wind_filter_title"));
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setWidth(500);
		dialog.setHeight(500);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
    	stage.getIcons().add(new Image("gans_icon.png"));
		
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.get() == ButtonType.OK &&
		        !(listEqualsNoOrder(vertexBackup, selectedVertexFilter) &&
		        listEqualsNoOrder(edgeBackup, selectedEdgeFilter))) 
		{ 
		    // Only redraw if OK was pressed and if there was a change in the selection.
			this.graphFactory.getGraph().setVertexFilter(selectedVertexFilter);
			this.graphFactory.getGraph().setEdgeFilter(selectedEdgeFilter);
			this.layout.applyLayout();
            reloadGraph();
		}
	}

    public static <T> boolean listEqualsNoOrder(List<T> l1, List<T> l2) {
        final Set<T> s1 = new HashSet<>(l1);
        final Set<T> s2 = new HashSet<>(l2);

        return s1.equals(s2);
    }
	
	private GridPane setupVertexFilterPane(List<VertexFilter> selectedVertexFilter) {
		List<VertexFilter> vertexFilter = PluginManager.getPluginManager().getVertexFilter();
		List<CheckBox> vertexFilterBoxes = new LinkedList<>();
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
			boolean selected = true;
			if(this.graphFactory.getGraph().getActiveVertexFilter().contains(filter)) {
				selectedVertexFilter.add(filter);
				selected = false;
			}
			CheckBox box = new CheckBox(filter.getName());
			box.setSelected(selected);
			box.setOnAction(event -> {
                CheckBox box1 = (CheckBox)event.getSource();
                int filterIndex = vertexFilterBoxes.indexOf(box1);
                if(!box1.isSelected()) {
                    selectedVertexFilter.add(vertexFilter.get(filterIndex));
                } else {
                    selectedVertexFilter.remove(vertexFilter.get(filterIndex));
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
		List<CheckBox> edgeFilterBoxes = new LinkedList<>();
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
			boolean selected = true;
			if(this.graphFactory.getGraph().getActiveEdgeFilter().contains(filter)) {
				selectedEdgeFilter.add(filter);
				selected = false;
			}
			CheckBox box = new CheckBox(filter.getName());
			box.setSelected(selected);
			box.setOnAction(event -> {
                CheckBox box1 = (CheckBox)event.getSource();
                int filterIndex = edgeFilterBoxes.indexOf(box1);
                if(!box1.isSelected()) {
                    selectedEdgeFilter.add(edgeFilter.get(filterIndex));
                } else {
                    selectedEdgeFilter.remove(edgeFilter.get(filterIndex));
                }

            });
			edgeFilterBoxes.add(box);
			edgeFilterPane.add(box, column, row);
			column++;
		}
		return edgeFilterPane;
	}
}
