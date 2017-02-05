package edu.kit.student.gui;

import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.LanguageManager;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * The InformationView shows a given set of properties from the selected
 * vertices in the {@link GraphView}.
 * 
 * @author Nicolas
 */
public class InformationView extends TableView<GAnsProperty<?>> {

	/**
	 * Sets the properties which should be shown in the InformationView. The
	 * {@link GAnsProperty} are being processed in an internal factory, which
	 * automatically creates the tablecells. The function will be called
	 * whenever the selection of the {@link GraphView} changes.
	 * 
	 * @param information
	 *            List with {@link GAnsProperty} elements which define the
	 *            content of the InformationView
	 */
	public void setInformation(ObservableList<GAnsProperty<?>> information) {
		setItems(information);
		// Implementation in diesem Stil: (siehe
		// https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/TableView.html)
		TableColumn<GAnsProperty<?>, String> propertyNameCol = new TableColumn<>(LanguageManager.getInstance().get("inf_prop"));
		propertyNameCol.setCellValueFactory(p -> p.getValue().propertyNameProperty());
		TableColumn<GAnsProperty<?>, String> propertyValueCol = new TableColumn<>(LanguageManager.getInstance().get("inf_value"));
        propertyValueCol.setCellValueFactory(p -> p.getValue().propertyValueAsString());

		propertyNameCol.prefWidthProperty().bind(this.widthProperty().divide(2));
        propertyValueCol.prefWidthProperty().bind(this.widthProperty().divide(2));

        getColumns().add(propertyNameCol);
        getColumns().add(propertyValueCol);

		refresh();
	}

}
