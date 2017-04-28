package edu.kit.student.gui;

import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.PluginManager;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A dialog for selecting the vertex and edge filter.
 * Accepts a preselected set of filter.
 * Up on selection and removal of a filter by the user
 * through a check box, the filter will be removed
 * from the initial set of filters.
 *
 * @author Lucas Steinmann, Nicolas Boltz
 */
public class FilterDialog extends Dialog<ButtonType> {


    private final Logger logger = LoggerFactory.getLogger(GraphViewSelectionModel.class);

    /**
     * Constructs a new dialog for selecting filter.
     * <p>
     * The filters passed as arguments should be the filters
     * which are currently active.
     * These filter will be preselected when the dialog is opened.
     * </p>
     *
     * @param activeVertexFilter the currently active vertex filter
     * @param activeEdgeFilter the currently active edge filter
     */
    public FilterDialog(List<VertexFilter> activeVertexFilter, List<EdgeFilter> activeEdgeFilter) {
        super();

        TabPane tabPane = new TabPane();

        Tab vertexFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_vertices"));
        vertexFilterTab.setClosable(false);
        vertexFilterTab.setContent(createCheckboxSetPane(activeVertexFilter,
                PluginManager.getPluginManager().getVertexFilter(),
                VertexFilter::getName, VertexFilter::getGroup, 4));

        Tab edgeFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_edges"));
        edgeFilterTab.setClosable(false);
        edgeFilterTab.setContent(createCheckboxSetPane(activeEdgeFilter,
                PluginManager.getPluginManager().getEdgeFilter(),
                EdgeFilter::getName, EdgeFilter::getGroup, 4));

        tabPane.getTabs().addAll(vertexFilterTab, edgeFilterTab);

        this.getDialogPane().setContent(tabPane);
        this.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        this.setTitle(LanguageManager.getInstance().get("wind_filter_title"));
        this.setHeaderText(null);
        this.setGraphic(null);
        this.setWidth(500);
        this.setHeight(500);
        Stage stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.getIcons().add(new Image("gans_icon.png"));
    }

    private <T> Pane createCheckboxSetPane(List<T> selectedItems,
                                           List<T> allItems,
                                           Function<T, String> itemName,
                                           Function<T, String> groupMap,
                                           int columnCount) {
        VBox pane = new VBox(10);
        pane.setPadding(new Insets(20,0,0,0));
        // Group items after the specified group mapping
        Map<String, List<T>> groupAll =
                allItems.stream().collect(
                    Collectors.groupingBy(groupMap));

        // For every group create one checkbox group
        for (String group : groupAll.keySet()) {
            pane.getChildren().add(createCheckboxGroup(groupAll.get(group),
                    selectedItems,
                    itemName, group, columnCount));
        }
        return pane;
    }

    private <T> Node createCheckboxGroup(List<T> allItems,
                                         List<T> selectedItems,
                                         Function<T, String> itemName,
                                         String groupName, int columnCount) {

        GridPane gridPane = new GridPane();
        List<CheckBox> boxes = new LinkedList<>();

        // Create group box which toggles all boxes inside the group
        final CheckBox groupCheckBox = new CheckBox(groupName);
        groupCheckBox.setAllowIndeterminate(false);

        int column = 0;
        int row = 0;
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        gridPane.setPadding(new Insets(10, 10, 10, 10));

        // Create equally spaced columns
        for (int i = 0; i < columnCount; i++) {
            ColumnConstraints constraint = new ColumnConstraints();
            constraint.setPercentWidth(100.0/columnCount);
            gridPane.getColumnConstraints().add(constraint);
        }

        for(T item : allItems) {
            if(column == columnCount) {
                column = 0;
                row++;
            }
            boolean selected = true;
            if(selectedItems.contains(item)) {
                selected = false;
            }
            CheckBox box = new CheckBox(itemName.apply(item));
            box.setSelected(selected);

            // When checkbox is toggled update the set of filters
            // and update the checkbox representing the whole group
            box.setOnAction(event -> {
                int filterIndex = boxes.indexOf(box);
                if(!box.isSelected()) {
                    selectedItems.add(allItems.get(filterIndex));
                } else {
                    selectedItems.remove(allItems.get(filterIndex));
                }
                updateGroupState(boxes, groupCheckBox);
            });
            boxes.add(box);
            gridPane.add(box, column, row);
            column++;
        }

        // Initialize the state of the group box
        // depending on the selection state of the items in the group
        updateGroupState(boxes, groupCheckBox);

        // Connect the group box with the items in the box
        groupCheckBox.setOnAction(event -> {
            boxes.forEach(b -> b.setSelected(groupCheckBox.isSelected()));
            if (!groupCheckBox.isSelected()) {
                selectedItems.addAll(allItems);
            } else {
                selectedItems.removeAll(allItems);
            }
        });
        return createBorderedGroup(gridPane, groupCheckBox);
    }

    private Pane createBorderedGroup(Node content, Region inTitle) {

        StackPane pane = new StackPane() {

            private final static int CheckBoxPadding = 4;

            {
                getChildren().add(content);
                inTitle.setPadding(new Insets(0, 0, 0, CheckBoxPadding));
                inTitle.setStyle("-fx-background-color: -fx-background");
                getChildren().add(inTitle);
            }

            @Override
            protected void layoutChildren() {
                super.layoutChildren();
                final double groupCbHeight = inTitle.prefHeight(-1);
                final double groupCbWidth = inTitle.prefWidth(groupCbHeight) + CheckBoxPadding;
                inTitle.resize(groupCbWidth, groupCbHeight);

                // Move checkbox a bit right from the top left corner
                inTitle.relocate(CheckBoxPadding * 2, -groupCbHeight / 2.0 - 1);
            }
        };
        pane.setPadding(new Insets(10, 0, 10, 0));
        pane.setBorder(new Border(new BorderStroke(Color.GRAY, BorderStrokeStyle.SOLID,
                new CornerRadii(4), new BorderWidths(1))));

        return pane;
    }

    /**
     * Synchronizes the state of the representing group checkbox with the current state of
     * the checkboxes in the group
     * @param checkBoxes the checkboxes in the group
     * @param groupCheckBox the checkbox representing the whole group
     */
    private void updateGroupState(Collection<CheckBox> checkBoxes, CheckBox groupCheckBox) {
        boolean allSelected = checkBoxes.stream().allMatch(CheckBox::isSelected);
        boolean allNotSelected = checkBoxes.stream().noneMatch(CheckBox::isSelected);
        if (allSelected) {
            groupCheckBox.setSelected(true);
            groupCheckBox.setIndeterminate(false);
        } else if (allNotSelected) {
            groupCheckBox.setSelected(false);
            groupCheckBox.setIndeterminate(false);
        } else {
            groupCheckBox.setIndeterminate(true);
        }
    }
}
