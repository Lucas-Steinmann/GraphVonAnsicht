package edu.kit.student.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.PluginManager;
import edu.kit.student.plugin.Workspace;
import edu.kit.student.plugin.WorkspaceOption;
import javafx.application.Application.Parameters;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

/**
 * Main application of GAns.
 * 
 * @author Nicolas
 */
public class GAnsApplication {

	private StructureView structureView;
	private InformationView informationView;
	private TabPane graphViewTabPane;
	private Stage primaryStage;
	private MenuBar menuBar;
	private ContextMenu structureViewContextMenu;
	
	private Workspace workspace;
	private GraphModel model;

	private GraphView currentGraphView;

	private File currentFile;
	private GAnsMediator mediator;
	
	public GAnsApplication(GAnsMediator mediator) {
		this.mediator = mediator;
	}

	public void start(Stage primaryStage, Parameters parameters) throws Exception {
		this.primaryStage = primaryStage;
		primaryStage.setTitle("Graph von Ansicht - Graphviewer");

		VBox rootLayout = new VBox();
		Scene scene = new Scene(rootLayout, 800, 600);

		menuBar = new MenuBar();
		menuBar.setId("Menubar");
		setupMenuBar();
		
		
		this.structureViewContextMenu = new ContextMenu();
		setupContextMenu();

		SplitPane treeInfoLayout = new SplitPane();
		treeInfoLayout.setOrientation(Orientation.VERTICAL);
		treeInfoLayout.setDividerPosition(0, 0.6);
		structureView = new StructureView();
		structureView.setId("StructureView");
		informationView = new InformationView();
		informationView.setId("InformationView");
		treeInfoLayout.getItems().addAll(structureView, informationView);
		
		structureView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		structureView.setContextMenu(structureViewContextMenu);
		structureView.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if(GAnsApplication.this.currentGraphView != null) {
					GAnsApplication.this.currentGraphView.getSelectionModel().clear();
				}
				if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
					openGraph(GAnsApplication.this.structureView.getIdOfSelectedItem());
				} else {
					ViewableGraph graph = model.getGraphFromId(GAnsApplication.this.structureView.getIdOfSelectedItem());
					
					ObservableList<GAnsProperty<?>> statistics = FXCollections.observableList(graph.getStatistics());
					informationView.setInformations(statistics);
					mouseEvent.consume();
				}
			}
		});

		SplitPane mainViewLayout = new SplitPane();
		mainViewLayout.setDividerPosition(0, 0.75);
		graphViewTabPane = new TabPane();
		graphViewTabPane.setId("GraphViewTabPane");
		graphViewTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
			@Override
			public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
				if(newValue == null) {
					GAnsApplication.this.currentGraphView = null;
				} else {
					GAnsApplication.this.currentGraphView = ((GraphViewTab) newValue).getGraphViewPanes().getGraphView();
				}
			}
		});
		mainViewLayout.getItems().addAll(graphViewTabPane, treeInfoLayout);
		rootLayout.getChildren().addAll(menuBar, mainViewLayout);
		VBox.setVgrow(mainViewLayout, Priority.ALWAYS);

		primaryStage.setScene(scene);
		primaryStage.show();
		
		parseCommandLineArguments(parameters);
		
	}
	
	private void parseCommandLineArguments(Parameters params) {
	    Map<String, String> namedParams = params.getNamed();
	    if (namedParams.isEmpty()) return;
	    String filename = "";
	    String layout = "";
	    String ws = "";
	    Importer importer = null;
	    Workspace tempWorkspace = null;
	    
	    for (String key : namedParams.keySet()) {
	        switch (key) {
	          case "in":
	              filename = namedParams.get(key);
	              if (!filename.contains(".")) {
	                  showErrorDialog("Error! The filename " + filename + " is invalid.");
	                  return;
	              }
	              String extension = "*" + filename.substring(filename.lastIndexOf('.'));
	              List<Importer> importerList = PluginManager.getPluginManager().getImporter();
	              for (Importer temp : importerList) {
                      if (extension.equals(temp.getSupportedFileEndings())) {
                          importer = temp;
                          break;
                      }     
	              }
	              break;
	          case "layout":
	              layout = namedParams.get(key);
	              break;
	          case "ws":
	              ws = namedParams.get(key);
	              List<WorkspaceOption> options = PluginManager.getPluginManager().getWorkspaceOptions();
	              for (WorkspaceOption option : options) {
	                  if (ws.equals(option.getId())){
	                      tempWorkspace = option.getInstance();
	                      break;
	                  }
	              }
	              break;
	          default:
	              // Information not specified
	              showErrorDialog("Warning! The argument \"--" + key + "=" + namedParams.get(key) + 
	                      "\" is unspecified and will be ignored.");
	        }
	    }
	    
        if (filename.equals("")) {
            showErrorDialog("Error! Please specify filename with \"--in=<filename>\"");
            return;
        } else if (importer == null) {
            String extension = "*" + filename.substring(filename.lastIndexOf('.'));
            showErrorDialog("Error! The extension " + extension + " is not supported. Please use an other file format.");
            return;
        }
	    
	    //import graph
        currentFile = new File(filename);
        //check if workspace is in arguments
        if (tempWorkspace != null) {
            this.workspace = tempWorkspace;
        } else {
            if (!ws.equals("")) {
                showErrorDialog("Warning! " + ws + " is not a valid workspace. Please select a Workspace.");
            }
            if(!openWorkspaceDialog()) return;
        }
        FileInputStream inputStream;
        try {
            inputStream = new FileInputStream(currentFile);
            importer.importGraph(workspace.getGraphModelBuilder(), inputStream);
            this.model = workspace.getGraphModel();
            ViewableGraph currentGraph = this.model.getRootGraphs().get(0);
            createGraphView();
            
            //check if layout is valid
            boolean validLayout = false;

            List<LayoutOption> options = currentGraph.getRegisteredLayouts();
            for (LayoutOption option : options) {
                if (layout.equals(option.getId())) {
                    //found valid layout and apply layout
                    validLayout = true;
                    option.chooseLayout();
                    currentGraphView.setCurrentLayoutOption(option);
                    option.applyLayout();
                }
            }  

            if (!validLayout) {
                if (layout != "") {
                    showErrorDialog("Warning! The layout " + layout + 
                            " is not a valid layout. The default layout will be selected.");
                }
                LayoutOption defaultOption = currentGraph.getDefaultLayout();
                defaultOption.chooseLayout();
                currentGraphView.setCurrentLayoutOption(defaultOption);
                defaultOption.applyLayout();
            }
            //open graph
            showGraph(currentGraph);
            
            this.structureView.showGraphModel(this.model);
        } catch (ParseException e) {
            showErrorDialog(e.getMessage());
            return;
        } catch (FileNotFoundException e) {
            showErrorDialog(e.getMessage());
        }
		
	}

	private void importClicked() {
		List<Importer> importerList = PluginManager.getPluginManager().getImporter();
		List<String> supportedFileExtensions = new ArrayList<String>();
		importerList.forEach((importer) -> supportedFileExtensions.add(importer.getSupportedFileEndings()));
		ExtensionFilter filter = new ExtensionFilter("Supported file extensions", supportedFileExtensions);
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select a graph file");
		fileChooser.getExtensionFilters().add(filter);
		fileChooser.setSelectedExtensionFilter(filter);
		currentFile = fileChooser.showOpenDialog(primaryStage);
		importFile(currentFile);
	}
	
	public void importFile(File file) {
		if(file == null) return;
		if(!openWorkspaceDialog()) return;
		FileInputStream inputStream;
		try {
			inputStream = new FileInputStream(file);
			String fileName = file.getName();
			String fileExtension = "*" + fileName.substring(fileName.lastIndexOf('.'));
            List<Importer> importerList = PluginManager.getPluginManager().getImporter();
            List<String> supportedFileExtensions = new ArrayList<String>();
            importerList.forEach((importer) -> supportedFileExtensions.add(importer.getSupportedFileEndings()));
			Importer importer = importerList.get(supportedFileExtensions.indexOf(fileExtension));
			importer.importGraph(workspace.getGraphModelBuilder(), inputStream);
			this.graphViewTabPane.getTabs().clear();
			this.informationView.setInformations(FXCollections.observableList(new LinkedList<GAnsProperty<?>>()));
			this.model = workspace.getGraphModel();
			ViewableGraph currentGraph = this.model.getRootGraphs().get(0);
			openGraph(currentGraph);
			this.structureView.showGraphModel(this.model);
			
		} catch (ParseException e) {
			this.graphViewTabPane.getTabs().clear();
			showErrorDialog(e.getMessage());
			return;
		} catch (FileNotFoundException e) {
			this.graphViewTabPane.getTabs().clear();
			showErrorDialog(e.getMessage());
		} 
	    
	}

	private void exportClicked() {
		Map<ExtensionFilter, Exporter> supportedFileExtensions = new HashMap<>();
		for (Exporter exporter : PluginManager.getPluginManager().getExporter()) {
		        StringBuilder description = new StringBuilder(exporter.getFileEndingDescription() + " (");
                exporter.getSupportedFileEndings().forEach((fe) -> description.append("*." + fe + ", "));
                description.delete(description.length() - 2, description.length());
                description.append(")");
		        supportedFileExtensions.put(new FileChooser.ExtensionFilter( 
		                description.toString(), 
		                exporter.getSupportedFileEndings().stream()
		                                                  .map((fe) -> "*." + fe)
		                                                  .collect(Collectors.toList())),
		                exporter);
		}

		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Select an export location");
		supportedFileExtensions.keySet().forEach((filter) -> fileChooser.getExtensionFilters().add(filter));
		File saveFile = fileChooser.showSaveDialog(primaryStage);

		// Wait for user to select file

		if (saveFile != null) {
            String fileExtension = saveFile.getName().substring(saveFile.getName().lastIndexOf('.') + 1);
            try {
                FileOutputStream outputStream = new FileOutputStream(saveFile);
                Exporter exporter = supportedFileExtensions.get(fileChooser.getSelectedExtensionFilter());
                try {
                    exporter.exportGraph(this.currentGraphView.getFactory().serializeGraph(), outputStream, fileExtension);
                } catch (IllegalArgumentException e) {
                    showErrorDialog(e.getMessage());
                } catch (Exception e) {
                    showErrorDialog("The exporter has encounterd a problem and stopped.");
                }
            } catch (FileNotFoundException e) {
                showErrorDialog(e.getMessage());
            }
		}
	}
	
	private void showGraph(ViewableGraph graph) {
		Tab tab = this.graphViewTabPane.getSelectionModel().getSelectedItem();
		tab.setText(graph.getName());
		tab.setId(graph.getID().toString());
		
		currentGraphView.setGraph(graph);
	}
	
	private void createGraphView() {
		GraphView graphView = new GraphView(this.mediator);
		
		GraphViewPanes graphViewPanes = new GraphViewPanes(graphView);
		
		GraphViewSelectionModel selectionModel = new GraphViewSelectionModel(graphViewPanes);
		graphView.setSelectionModel(selectionModel);

		GraphViewTab tab = new GraphViewTab(graphViewPanes);
		graphViewTabPane.getTabs().add(tab);
		graphViewTabPane.getSelectionModel().select(tab);
		
		graphView.getSelectionModel().getSelectedItems().addListener(new SetChangeListener<VertexShape>() {
			public void onChanged(Change<? extends VertexShape> changedItem) {
				ObservableSet<VertexShape> selectedItems = graphView.getSelectionModel().getSelectedItems();
				List<GAnsProperty<?>> tmp = new LinkedList<GAnsProperty<?>>();
				for (VertexShape element : selectedItems) {
					GraphViewGraphFactory factory = currentGraphView.getFactory();
					Vertex vertex = factory.getVertexFromShape(element);
					tmp.addAll(vertex.getProperties());
				}
				
				ObservableList<GAnsProperty<?>> properties = FXCollections.observableList(tmp);
				informationView.setInformations(properties);
			}
		});
	}
	
	private boolean openWorkspaceDialog() {
		List<String> workspaceNames = new ArrayList<String>();
		List<WorkspaceOption> options = PluginManager.getPluginManager().getWorkspaceOptions();
		options.forEach((option) -> workspaceNames.add(option.getName()));
		ChoiceDialog<String> dialog = new ChoiceDialog<String>(workspaceNames.get(0), workspaceNames);
		dialog.setTitle("Workspaces");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setContentText("Choose a workspace:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
		    WorkspaceOption chosenOption = options.get(workspaceNames.indexOf(result.get()));
		    if(ParameterDialogGenerator.showDialog(chosenOption.getSettings())) {
		    	workspace = chosenOption.getInstance();
			    return true;
		    }
		}
		return false;
	}
	
	private boolean openLayoutSelectionDialog(ViewableGraph graph) {
		List<String> layoutNames = new ArrayList<String>();
		List<LayoutOption> options = graph.getRegisteredLayouts();
		options.forEach((option) -> layoutNames.add(option.getName()));
		ChoiceDialog<String> dialog = new ChoiceDialog<String>(layoutNames.get(0), layoutNames);
		dialog.setTitle("Layout Algorithms");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.setContentText("Choose an layout algorithm:");
		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
		    LayoutOption chosenOption = options.get(layoutNames.indexOf(result.get()));
		    chosenOption.chooseLayout();
		    Settings settings = chosenOption.getSettings();
		    if(ParameterDialogGenerator.showDialog(settings)) {
		    	currentGraphView.setCurrentLayoutOption(chosenOption);
		    	chosenOption.applyLayout();
		    }
		    return true;
		}
		return false;
	}
	
	private void openLayoutSettingsDialog(LayoutOption option) {
		Settings settings = option.getSettings();
	    if(ParameterDialogGenerator.showDialog(settings)) {
	    	currentGraphView.setCurrentLayoutOption(option);
	    	option.applyLayout();
	    }
	}

	private void setupMenuBar() {
		Menu menuFile = new Menu("File");
		MenuItem importItem = new MenuItem("Import");
		importItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				importClicked();
			}
		});
		if(PluginManager.getPluginManager().getImporter().isEmpty()) {
			importItem.setDisable(true);
		}
		MenuItem exportItem = new MenuItem("Export");
		exportItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				exportClicked();
			}
		});
		if(PluginManager.getPluginManager().getExporter().isEmpty()) {
			exportItem.setDisable(true);
		}
		MenuItem exitItem = new MenuItem("Exit");
		exitItem.setOnAction(e -> Platform.exit());
		menuFile.getItems().addAll(importItem, exportItem, exitItem);
		
		// disabling the export button if there is no graphView
		menuFile.setOnShowing(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				exportItem.setDisable(GAnsApplication.this.currentGraphView == null);
			}
		});

		Menu menuLayout = new Menu("Layout");
		Menu changeLayoutItem = new Menu("Change algorithms");
		MenuItem layoutPropertiesItem = new MenuItem("Properties");
		layoutPropertiesItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				openLayoutSettingsDialog(currentGraphView.getCurrentLayoutOption());
				currentGraphView.reloadGraph();
			}
		});
		menuLayout.getItems().addAll(changeLayoutItem, layoutPropertiesItem);
		
		menuLayout.setOnShowing(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				// disabling the change and layout properties button if there is no graphview
				if(GAnsApplication.this.currentGraphView == null) {
					changeLayoutItem.setDisable(true);
					layoutPropertiesItem.setDisable(true);
				} else {
					changeLayoutItem.setDisable(false);
					if(currentGraphView.getCurrentLayoutOption().getSettings().size() == 0) {
						layoutPropertiesItem.setDisable(true);
					} else {
						layoutPropertiesItem.setDisable(false);
					}
					changeLayoutItem.getItems().clear();
					for(LayoutOption option: GAnsApplication.this.currentGraphView.getFactory().getGraph().getRegisteredLayouts()) {
						MenuItem item = new MenuItem(option.getName());
						item.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent e) {
								option.chooseLayout();
								openLayoutSettingsDialog(option);
								currentGraphView.reloadGraph();
							}
						});
						changeLayoutItem.getItems().add(item);
					}
				}
			}
		});
		
		Menu menuOther = new Menu("Other");
		MenuItem groupItem = new MenuItem("Edit Groups");
		groupItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				GAnsApplication.this.currentGraphView.openGroupDialog();
			}
		});
		
		MenuItem filterItem = new MenuItem("Edit Filter");
		filterItem.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				GAnsApplication.this.currentGraphView.openFilterDialog();
			}
		});
		
		menuOther.getItems().addAll(groupItem, filterItem);
		
		// disabling the groups button if there is no graphView
		menuOther.setOnShowing(new EventHandler<Event>() {
			@Override
			public void handle(Event e) {
				if(GAnsApplication.this.currentGraphView == null) {
					groupItem.setDisable(true);
					filterItem.setDisable(true);
				} else {
					groupItem.setDisable(false);
					filterItem.setDisable(false);
				}
				
			}
		});

		menuBar.getMenus().addAll(menuFile, menuLayout, menuOther);
	}
	
	private void setupContextMenu() {
		MenuItem openGraph = new MenuItem("Open");
		openGraph.setOnAction(new EventHandler<ActionEvent>() {
		    public void handle(ActionEvent e) {
		    	openGraph(GAnsApplication.this.structureView.getIdOfSelectedItem());
		    }
		});
		
		this.structureViewContextMenu.getItems().add(openGraph);
	}
	
	public void openGraph(Integer id) {
		if(id == -1) return;
		ViewableGraph graph = this.model.getGraphFromId(id);
		if(graph != null) {
			boolean found = false;
			for(Tab tab : GAnsApplication.this.graphViewTabPane.getTabs()) {
				if(tab.getId().compareTo(graph.getID().toString()) == 0) {
					found = true;
					GAnsApplication.this.graphViewTabPane.getSelectionModel().select(tab);
					break;
				}
			}
			if(!found) {
				openGraph(graph);
			}
		}
	}
	
	private void openGraph(ViewableGraph graph) {
		createGraphView();
		LayoutOption defaultOption = graph.getDefaultLayout();
		defaultOption.chooseLayout();
		currentGraphView.setCurrentLayoutOption(defaultOption);
		defaultOption.applyLayout();
		showGraph(graph);
	}
	
	private void showErrorDialog(String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
}
