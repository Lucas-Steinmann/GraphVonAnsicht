package edu.kit.student.gui;

import edu.kit.student.parameter.BooleanParameter;
import edu.kit.student.parameter.DoubleParameter;
import edu.kit.student.parameter.IntegerParameter;
import edu.kit.student.parameter.MultipleChoiceParameter;
import edu.kit.student.parameter.Parameter;
import edu.kit.student.parameter.ParameterVisitor;
import edu.kit.student.parameter.Settings;
import edu.kit.student.parameter.StringParameter;
import javafx.geometry.Insets;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
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
		parent.setPadding(new Insets(0, 10, 0, 10));

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
}
