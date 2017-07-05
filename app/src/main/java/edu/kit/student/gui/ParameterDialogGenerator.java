package edu.kit.student.gui;

import edu.kit.student.parameter.BooleanParameter;
import edu.kit.student.parameter.DoubleParameter;
import edu.kit.student.parameter.IntegerParameter;
import edu.kit.student.parameter.MultipleChoiceParameter;
import edu.kit.student.parameter.Parameter;
import edu.kit.student.parameter.ParameterVisitor;
import edu.kit.student.parameter.Settings;
import edu.kit.student.parameter.StringParameter;
import edu.kit.student.property.BeansGAnsProperty;
import edu.kit.student.util.LanguageManager;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Callback;
import javafx.util.StringConverter;


/**
 * Generates a parameter dialog given a parent node and a set of parameters.
 * 
 * @author Lucas Steinmann
 */
public class ParameterDialogGenerator extends ParameterVisitor {

    private GridPane grid;
    private int rowPointer = 0;

    private StringConverter<Integer> intConverter;
    private StringConverter<Double> doubleConverter;

    private List<Parameter<?>> parameters;
    private List<BeansGAnsProperty<?>> parameterWrappers = new LinkedList<>();
    private Settings settings;

    /**
     * Constructs a new ParameterDialogGenerator, and sets the settings
     * which should are used as model for the GUI.
     *
     * @param settings the settings to generate the GUI from
     */
    ParameterDialogGenerator(Settings settings) {
        this.parameters = settings.getParameters();
        this.settings = settings;
    }

    Node build() {
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 20, 10, 10));

        ColumnConstraints c1 = new ColumnConstraints();
        ColumnConstraints c2 = new ColumnConstraints();
        c1.setPercentWidth(50);
        c2.setPercentWidth(50);
        grid.getColumnConstraints().add(c1);
        grid.getColumnConstraints().add(c2);

        initIntConverter();
        initDoubleConverter();

        for (Parameter<?> p : parameters) {
            p.accept(this);
        }
        // If there are parameters before the sub setting, save some extra space
        boolean first = !parameters.isEmpty();
        for (Settings subSetting : settings.getSubSettings()) {
            ParameterDialogGenerator subgen = new ParameterDialogGenerator(subSetting);
            Pane pane = new BorderedPane(subgen.build(), new Label(subSetting.getName()));
            GridPane.setColumnSpan(pane, GridPane.REMAINING);
            if (first) {
                GridPane.setMargin(pane, new Insets(10, 0, 10, 0));
                first = false;
            }
            grid.add(pane, 0, rowPointer++);
        }
        return grid;
    }


    @Override
    public void visit(BooleanParameter parameter) {
        CheckBox box = new CheckBox(parameter.getName());
        box.setSelected(parameter.getValue());
        BeansGAnsProperty<Boolean> beansProperty = BeansGAnsProperty.wrap(parameter);
        beansProperty.bind(box.selectedProperty());
        parameterWrappers.add(beansProperty);

        BeansGAnsProperty<Boolean> disabledProperty = BeansGAnsProperty.wrap(parameter.disabledProperty());
        box.disableProperty().bind(disabledProperty);
        parameterWrappers.add(disabledProperty);

        parameter.cacheCurrentValue();

        grid.add(box, 0, rowPointer++);
    }

    @Override
    public void visit(IntegerParameter parameter) {
        SpinnerValueFactory.IntegerSpinnerValueFactory factory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        parameter.getMin(),
                        parameter.getMax(),
                        parameter.getValue());

        factory.setConverter(this.intConverter);

        Spinner<Integer> spinner = new Spinner<>(factory);
        spinner.setEditable(true);
        BeansGAnsProperty<Integer> beansProperty = BeansGAnsProperty.wrap(parameter);
        beansProperty.bind(spinner.valueProperty());
        parameterWrappers.add(beansProperty);

        BeansGAnsProperty<Boolean> disabledProperty = BeansGAnsProperty.wrap(parameter.disabledProperty());
        spinner.disableProperty().bind(disabledProperty);
        parameterWrappers.add(disabledProperty);

        parameter.cacheCurrentValue();

        grid.add(new Text(parameter.getName()), 0, rowPointer);
        grid.add(spinner, 1, rowPointer++);
    }

    @Override
    public void visit(DoubleParameter parameter) {
        // The factories purpose is so that 3 decimals can
        // be shown in the spinner, also it prevents illegal input.
        SpinnerValueFactory.DoubleSpinnerValueFactory factory =
                new SpinnerValueFactory.DoubleSpinnerValueFactory(
                        parameter.getMin(),
                        parameter.getMax(),
                        parameter.getValue(),
                        parameter.getAmountPerStep());

        factory.setConverter(this.doubleConverter);

        Spinner<Double> spinner = new Spinner<>(factory);
        spinner.setEditable(true);
        BeansGAnsProperty<Double> beansProperty = BeansGAnsProperty.wrap(parameter);
        beansProperty.bind(spinner.valueProperty());
        parameterWrappers.add(beansProperty);

        BeansGAnsProperty<Boolean> disabledProperty = BeansGAnsProperty.wrap(parameter.disabledProperty());
        spinner.disableProperty().bind(disabledProperty);
        parameterWrappers.add(disabledProperty);

        parameter.cacheCurrentValue();

        grid.add(new Text(parameter.getName()), 0, rowPointer);
        grid.add(spinner, 1, rowPointer++);
    }

    @Override
    public void visit(StringParameter parameter) {
        TextField field = new TextField(parameter.getValue());
        BeansGAnsProperty<String> beansProperty = BeansGAnsProperty.wrap(parameter);
        beansProperty.bind(field.textProperty());
        parameterWrappers.add(beansProperty);

        BeansGAnsProperty<Boolean> disabledProperty = BeansGAnsProperty.wrap(parameter.disabledProperty());
        field.disableProperty().bind(disabledProperty);
        parameterWrappers.add(disabledProperty);

        parameter.cacheCurrentValue();

        grid.add(new Text(parameter.getName()), 0, rowPointer);
        grid.add(field, 1, rowPointer++);
    }

    @Override
    public void visit(MultipleChoiceParameter parameter) {
        grid.add(new Text(parameter.getName()), 0, rowPointer);
        ComboBox<String> cmb = new ComboBox<>();
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
        BeansGAnsProperty<String> beansProperty = BeansGAnsProperty.wrap(parameter);
        beansProperty.bind(cmb.valueProperty());
        parameterWrappers.add(beansProperty);

        BeansGAnsProperty<Boolean> disabledProperty = BeansGAnsProperty.wrap(parameter.disabledProperty());
        cmb.disableProperty().bind(disabledProperty);
        parameterWrappers.add(disabledProperty);

        parameter.cacheCurrentValue();

        grid.add(cmb, 1, rowPointer++);
    }

    /**
     * Creates a ParameterDialog for the supplied Settings.
     *
     * @param settings
     *            The settings for which the dialog will be created.
     * @return true: Dialog was accepted, false: Dialog was aborted.
     */
    static boolean showDialog(Window owner, Settings settings) {
        if (settings.size() == 0) {
            // if there are no settings to be shown the dialog will
            // automatically be accepted
            return true;
        } else {
            Alert dialog = new Alert(AlertType.CONFIRMATION);

            ParameterDialogGenerator gen = new ParameterDialogGenerator(settings);
            dialog.getDialogPane().setContent(gen.build());

            dialog.initOwner(owner);
            dialog.setTitle(LanguageManager.getInstance().get(
                    "wind_prop_title") + ": " + settings.getName());
            dialog.setHeaderText(null);
            dialog.setGraphic(null);

            Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
            stage.getIcons().add(new Image("gans_icon.png"));

            ButtonType result = dialog.showAndWait().orElse(ButtonType.CANCEL);
            if (result != ButtonType.OK) {
                gen.resetParameters();
                return false;
            }
            return true;
        }
    }

    private void resetParameters() {
        for (BeansGAnsProperty p : parameterWrappers) {
            p.unbind();
        }
        for (Parameter p : parameters) {
            p.reset();
        }
    }

    private void initIntConverter() {
        this.intConverter = new StringConverter<Integer>() {

            @Override
            public String toString(Integer value) {
                return value == null ? "" : value.toString();
            }

            @Override
            public Integer fromString(String value) {
                try {
                    if (value == null) {
                        return null;
                    }

                    value = value.trim();

                    if (value.length() < 1) {
                        return null;
                    }

                    return Integer.parseInt(value);
                } catch (NumberFormatException ex) {
                    return 0;
                }

            }
        };
    }

    private void initDoubleConverter() {
        this.doubleConverter = new StringConverter<Double>() {
            private final DecimalFormat df = new DecimalFormat("#.###");

            @Override
            public String toString(Double value) {
                // If the specified value is null, return a zero-length String
                if (value == null) {
                    return "";
                }
                return df.format(value);
            }

            @Override
            public Double fromString(String value) {
                try {
                    // If the specified value is null or zero-length, return
                    // null
                    if (value == null) {
                        return null;
                    }

                    value = value.trim();

                    if (value.length() < 1) {
                        return null;
                    }

                    // Perform the requested parsing
                    return df.parse(value).doubleValue();
                } catch (ParseException ex) {
                    return 0.0d;
                }
            }
        };
    }
}
