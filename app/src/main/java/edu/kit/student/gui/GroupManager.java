package edu.kit.student.gui;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import edu.kit.student.graphmodel.ViewableVertex;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class GroupManager {

	private GraphViewGraphFactory factory;
	
	private List<Integer> groupIdsBacking;
	private ObservableList<Integer> groupIds;
	private Map<Integer, VertexGroup> groupMap;
	private List<Integer> removedGroups;

	public GroupManager(GraphViewGraphFactory factory) {
		this.factory = factory;
		groupIdsBacking = new LinkedList<Integer>();
		groupIds = FXCollections.observableList(groupIdsBacking);
		groupMap = new HashMap<Integer, VertexGroup>();
		removedGroups = new LinkedList<Integer>();
	}
	
	public boolean openAddGroupDialog(Set<ViewableVertex> vertices) {
		TextInputDialog dialog = new TextInputDialog("New Group");
    	dialog.setTitle("Add group");
    	dialog.setHeaderText(null);
    	dialog.setGraphic(null);
    	dialog.setContentText("Enter new group name:");
    	Optional<String> result = dialog.showAndWait();
    	if (result.isPresent()){
    		VertexGroup group = new VertexGroup(factory, result.get(), vertices);
    		groupIds.add(group.getId());
    		groupMap.put(group.getId(), group);
    		return true;
    	}
    	return false;
    }

	public void openGroupDialog() {
		Dialog<ButtonType> dialog = new Dialog<ButtonType>();
		ListView<Integer> groupList = new ListView<Integer>();
		groupList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		groupList.setItems(groupIds);
		groupList.setCellFactory(new Callback<ListView<Integer>, ListCell<Integer>>() {
			@Override
			public ListCell<Integer> call(ListView<Integer> list) {
				return new GroupListCell();
			}
		});
		
		Button upButton = new Button("Up");
		upButton.setDisable(true);
		upButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Integer groupId = groupList.getSelectionModel().getSelectedItem();
				int currentPos = groupIds.indexOf(groupId);
				groupIds.remove(groupId);
				groupIds.add(currentPos - 1, groupId);
				groupList.getSelectionModel().select(groupId);
			}
		});
		
		Button downButton = new Button("Down");
		downButton.setDisable(true);
		downButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Integer groupId = groupList.getSelectionModel().getSelectedItem();
				int currentPos = groupIds.indexOf(groupId);
				groupIds.remove(groupId);
				groupIds.add(currentPos + 1, groupId);
				groupList.getSelectionModel().select(groupId);
			}
		});
		
		Button removeButton = new Button("Remove");
		removeButton.setDisable(true);
		removeButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				Integer groupId = groupList.getSelectionModel().getSelectedItem();
				groupList.getSelectionModel().clearSelection();
				groupIds.remove(groupId);
				removedGroups.add(groupId);
				groupList.refresh();
			}
		});
		
		groupList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Integer>() {
			@Override
			public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
				if(newValue == null) {
					upButton.setDisable(true);
					downButton.setDisable(true);
					removeButton.setDisable(true);
				} else {
					int index = groupIds.indexOf(newValue);
					upButton.setDisable(index == 0);
					downButton.setDisable(index == groupIds.size() - 1);
					removeButton.setDisable(false);
				}
			}
		});
		
		HBox buttonBox = new HBox(upButton, downButton, removeButton);
		buttonBox.setSpacing(3);
		VBox root = new VBox(groupList, buttonBox);
		
		dialog.getDialogPane().setContent(root);
		dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK);
		dialog.setTitle("Groups");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);

		LinkedList<Integer> groupIdsAbortBackup = new LinkedList<Integer>(this.groupIds);
		
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.isPresent()) {
			if(result.get() == ButtonType.OK) {
				removedGroups.forEach(groupId -> groupMap.remove(groupId).uncolorVertices());
				removedGroups.clear();
				//TODO: maybe check for made changes and only apply them.
				applyGroups();
			}
		} else {
			removedGroups.clear();
			this.groupIdsBacking = new LinkedList<Integer>(groupIdsAbortBackup);
			this.groupIds = FXCollections.observableList(groupIdsBacking);
		}
	}
	
	public void applyGroups() {
		//TODO: inefficient, could map over all groups and vertices before coloring
		for(int i = groupIds.size() - 1; i > -1; i--) {
			groupMap.get(groupIds.get(i)).colorVertices();
		}
	}
	
	private class GroupListCell extends ListCell<Integer> {
		@Override
		public void updateItem(Integer item, boolean empty) {
			super.updateItem(item, empty);
			VertexGroup group = groupMap.get(item);
			if(!empty && group != null) {
				Region spacer = new Region();
				HBox box = new HBox(group.getLabel(), spacer, group.getPicker());
				HBox.setHgrow(spacer, Priority.ALWAYS);
				box.setSpacing(10);
				setGraphic(box);
			} else {
				setGraphic(null);
			}
		}
	}
}
