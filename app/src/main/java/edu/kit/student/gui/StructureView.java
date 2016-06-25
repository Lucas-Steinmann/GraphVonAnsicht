package edu.kit.student.gui;

import java.util.HashMap;

import edu.kit.student.graphmodel.Graph;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

/**
 * The StructureView regulates the access and representation of the elements in
 * the StructureView of GAns.
 * 
 * @author Nicolas
 */
public class StructureView extends TreeView<String> {

	private HashMap<TreeItem<String>, String> itemMap;

	/**
	 * Constructor.
	 */
	public StructureView() {
		itemMap = new HashMap<TreeItem<String>, String>();
	}

	/**
	 * Creates a tree like representation from a given graph and its subgraphs.
	 * Should be called, before calling other methods, because there could be a
	 * dummy root-node in the View.
	 * 
	 * @param graph
	 *            The graph which should be represented.
	 */
	public void showTree(Graph graph) {
		// Erstellen der TreeItems anhand des uebergebenen Graphen in der
		// internen Representation
		TreeItem<String> root = new TreeItem<String>(/* Text des RootGraphen */);
		// Erstellen der Items anhand der Subgraphen von graph.
		setRoot(root);
	}
	
	/**
	 * Returns the id of the selected graph.
	 * @return The id of the selected graph.
	 */
	public String getIdOfSelectedItem() {
		return itemMap.get(getSelectionModel().getSelectedItem());
	}
}
