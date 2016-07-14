package edu.kit.student.gui;

import java.util.Optional;

import edu.kit.student.parameter.BooleanParameter;
import edu.kit.student.parameter.DoubleParameter;
import edu.kit.student.parameter.IntegerParameter;
import edu.kit.student.parameter.MultipleChoiceParameter;
import edu.kit.student.parameter.Parameter;
import edu.kit.student.parameter.ParameterVisitor;
import edu.kit.student.parameter.Settings;
import edu.kit.student.parameter.StringParameter;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * Generates a parameter dialog given a parent node and a set of parameters.
 * 
 * @author Lucas
 */
public class ParameterDialogGenerator extends ParameterVisitor {
	private GridPane parent;
	private int parameterCount = 0;

	/**
	 * Constructs a new ParameterDialogGenerator and sets the parent, where all
	 * parameter GUI-Elements are placed in afterwards.
	 */
	public ParameterDialogGenerator(GridPane parent, Settings settings) {
		this.parent = parent;
		parent.setHgap(10);
		parent.setVgap(10);
		parent.setPadding(new Insets(10, 20, 10, 10));

		for (Parameter<?, ?> p : settings.values()) {
			p.accept(this);
		}
	}
	
	@Override
	public void visit(BooleanParameter parameter) {
		parent.add(new CheckBox(parameter.getName()), 0, parameterCount);
		parameterCount++;
	}

	@Override
	public void visit(IntegerParameter parameter) {
		parent.add(new Text(parameter.getName()), 0, parameterCount);
		parent.add(new Spinner<Integer>(parameter.getMin(), parameter.getMax(), parameter.getValue()), 1,
				parameterCount);
		parameterCount++;
	}
	
	@Override
	public void visit(DoubleParameter parameter) {
		parent.add(new Text(parameter.getName()), 0, parameterCount);
		parent.add(new Spinner<Double>(parameter.getMin(), parameter.getMax(), parameter.getValue()), 1,
				parameterCount);
		parameterCount++;
	}
	
	@Override
	public void visit(StringParameter parameter) {
		parent.add(new Text(parameter.getName()), 0, parameterCount);
		parent.add(new TextField(parameter.getName()), 1, parameterCount);
		parameterCount++;
	}

	@Override
	public void visit(MultipleChoiceParameter parameter) {
		parent.add(new Text(parameter.getName()), 0, parameterCount);
		ComboBox<String> cmb = new ComboBox<String>();
		cmb.getItems().addAll(parameter.getChoices());

		cmb.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
			@Override
			public ListCell<String> call(ListView<String> p) {
				return new ListCell<String>() {
					{
					}

					@Override
					protected void updateItem(String item, boolean empty) {
						super.updateItem(item, empty);
						this.setText(item);
					}
				};
			}
		});
		cmb.getSelectionModel().select(parameter.getSelectedIndex());
		parent.add(cmb, 1, parameterCount);
		parameterCount++;
	}
	
	/**
	 * Creates a ParameterDialog for the supplied Settings.
	 * @param settings The settings for which the dialog will be created.
	 * @return true: Dialog was accepted, false: Dialog was aborted.
	 */
	public static boolean showDialog(Settings settings) {
		GridPane root = new GridPane();
		ColumnConstraints c1 = new ColumnConstraints();
		ColumnConstraints c2 = new ColumnConstraints();
		c1.setPercentWidth(50);
		c2.setPercentWidth(50);
		root.getColumnConstraints().add(c1);
		root.getColumnConstraints().add(c2);
		new ParameterDialogGenerator(root, settings);
		Alert dialog = new Alert(AlertType.CONFIRMATION);
		dialog.setTitle("Settings");
		dialog.setHeaderText(null);
		dialog.setGraphic(null);
		dialog.getDialogPane().setContent(root);
		Optional<ButtonType> result = dialog.showAndWait();
		if(result.get() != ButtonType.OK) {
			return false;
		}
		return true;
	}
}
