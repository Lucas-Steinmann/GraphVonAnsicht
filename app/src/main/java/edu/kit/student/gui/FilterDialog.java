package edu.kit.student.gui;

import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.PluginManager;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.util.LanguageManager;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * A dialog for selecting the vertex and edge filter.
 * Accepts a preselected set of filter.
 * Up on selection and removal of a filter by the user
 * through a check box, the filter will be removed
 * from the initial set of filters.
 */
public class FilterDialog extends Dialog<ButtonType> {

    public final List<VertexFilter> selectedVertexFilter;
    public final List<EdgeFilter> selectedEdgeFilter;

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

        selectedVertexFilter = activeVertexFilter;
        selectedEdgeFilter = activeEdgeFilter;

        TabPane tabPane = new TabPane();

        Tab vertexFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_vertices"));
        vertexFilterTab.setClosable(false);
        vertexFilterTab.setContent(createCheckboxSetPane(selectedVertexFilter,
                PluginManager.getPluginManager().getVertexFilter(),
                VertexFilter::getName));

        Tab edgeFilterTab = new Tab(LanguageManager.getInstance().get("wind_filter_edges"));
        edgeFilterTab.setClosable(false);
        edgeFilterTab.setContent(createCheckboxSetPane(selectedEdgeFilter,
                PluginManager.getPluginManager().getEdgeFilter(),
                EdgeFilter::getName));

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

    private <T> GridPane createCheckboxSetPane(List<T> selectedItems,
                                           List<T> allItems,
                                           Function<T, String> itemName) {
        List<CheckBox> boxes = new LinkedList<>();
        int column = 0;
        int row = 0;
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setPadding(new Insets(10, 10, 10, 10));
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(25);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(25);
        ColumnConstraints col3 = new ColumnConstraints();
        col3.setPercentWidth(25);
        ColumnConstraints col4 = new ColumnConstraints();
        col4.setPercentWidth(25);
        pane.getColumnConstraints().addAll(col1, col2, col3, col4);

        for(T item : allItems) {
            if(column == 4) {
                column = 0;
                row++;
            }
            boolean selected = true;
            if(selectedItems.contains(item)) {
                selected = false;
            }
            CheckBox box = new CheckBox(itemName.apply(item));
            box.setSelected(selected);
            box.setOnAction(event -> {
                int filterIndex = boxes.indexOf(box);
                if(!box.isSelected()) {
                    selectedItems.add(allItems.get(filterIndex));
                } else {
                    selectedItems.remove(allItems.get(filterIndex));
                }

            });
            boxes.add(box);
            pane.add(box, column, row);
            column++;
        }
        return pane;

    }
}
