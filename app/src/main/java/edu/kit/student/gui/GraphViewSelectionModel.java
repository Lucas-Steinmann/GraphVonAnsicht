package edu.kit.student.gui;

import edu.kit.student.objectproperty.GAnsProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.MultipleSelectionModel;

/**
 * The selection model for the {@link GraphView}, that supports multiple
 * selection of vertices and edges.
 * 
 * @author Nicolas
 */
public class GraphViewSelectionModel extends MultipleSelectionModel<GAnsGraphElement> {

	/**
	 * Returns the {@link GAnsProperty} of all selected items.
	 * 
	 * @return A list with all the {@link GAnsProperty} of all selected items.
	 */
	public ObservableList<GAnsProperty> getSelectedItemsProperties() {
		// TODO: diese Funktion wird vom listener aufgerufen der auf ein changed
		// des Selectionmodel hoert und �bergibt diese liste an die
		// informationview
		// TODO: Falscher Listentyp!
		return new SimpleListProperty<GAnsProperty>();
	}
	
	@Override
	public ObservableList<Integer> getSelectedIndices() {
		// TODO: Falscher Listentyp!
		return new SimpleListProperty<Integer>();
	}

	@Override
	public ObservableList<GAnsGraphElement> getSelectedItems() {
		// TODO: Falscher Listentyp!
		return new SimpleListProperty<GAnsGraphElement>();
	}

	@Override
	public void selectIndices(int index, int... indices) {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectAll() {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectFirst() {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectLast() {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearAndSelect(int index) {
		// TODO Auto-generated method stub
	}

	@Override
	public void select(int index) {
		// TODO Auto-generated method stub
	}

	@Override
	public void select(GAnsGraphElement obj) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearSelection(int index) {
		// TODO Auto-generated method stub
	}

	@Override
	public void clearSelection() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isSelected(int index) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void selectPrevious() {
		// TODO Auto-generated method stub
	}

	@Override
	public void selectNext() {
		// TODO Auto-generated method stub
	}
}