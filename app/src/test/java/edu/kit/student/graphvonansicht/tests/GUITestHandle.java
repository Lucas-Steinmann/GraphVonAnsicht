package edu.kit.student.graphvonansicht.tests;

import java.io.File;
import java.util.List;

import org.testfx.api.FxToolkit;

import edu.kit.student.gui.GAnsMediator;
import edu.kit.student.util.DoublePoint;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

public class GUITestHandle {

    GAnsMediator app = new GAnsMediator();
    Stage appStage;

    MenuBar menuBar;
    Menu fileMenu, layoutMenu, otherMenu;
    MenuItem importItem, exportItem, exitItem;

    Menu changeLayoutMenu; 
    ObservableList<MenuItem> availableLayouts;
    MenuItem propertiesItem;

    MenuItem groupItem, filterItem;

    public void start(String... args) throws Exception {

        appStage = FxToolkit.registerPrimaryStage();
        app = (GAnsMediator) FxToolkit.setupApplication(GAnsMediator.class, args);

        

        menuBar = (MenuBar) appStage.getScene().lookup("#Menubar");
        for (Menu menu : menuBar.getMenus()) {
             if (menu.getText().equals("File")) {
                 fileMenu = menu;
                 for (MenuItem subMenu : fileMenu.getItems()) {
                     if (subMenu.getText().equals("Import")) {
                         importItem = subMenu;
                     } else if (subMenu.getText().equals("Export")) {
                         exportItem = subMenu;
                     } else if (subMenu.getText().equals("Exit")) {
                         exitItem = subMenu;
                     }
                 }
             } else if (menu.getText().equals("Layout")) {
                 layoutMenu = menu;
                 for (MenuItem subMenu : layoutMenu.getItems()) {
                     if (subMenu.getText().equals("Change algorithms")) {
                         changeLayoutMenu = (Menu) subMenu;
                         availableLayouts = changeLayoutMenu.getItems();
                     } else if (subMenu.getText().equals("Properties")) {
                         propertiesItem = subMenu;
                     }
                 }
             } else if (menu.getText().equals("Other")) {
                 otherMenu = menu;
                 for (MenuItem subMenu : otherMenu.getItems()) {
                     if (subMenu.getText().equals("Edit Filter")) {
                         filterItem = subMenu;
                     } else if (subMenu.getText().equals("Edit Groups")) {
                         groupItem = subMenu;
                     }
                 }
             }
        }
    }
    
    
    // Helper functions for gui testing
    public void importGraph(File file) {
        //TODO: Implement
    }

    public void exportGraph() {
        //TODO: Implement
    }
    
    public void changeLayout(String name) {
        //TODO: Implement
    }

    public void openGraph(Integer id) {
        //TODO: Implement
    }
    
    public void closeTab() {
        //TODO: Implement
    }
    
    public void applyVertexFilter(String name) {
        //TODO: Implement
    }
    
    public void applyEdgeFilter(String name) {
        //TODO: Implement
    }
    
    public void selectRange(DoublePoint start, DoublePoint end) {
        //TODO: Implement
    }

    public void selectPoint(DoublePoint point) {
        //TODO: Implement
    }
    
    public List<String> getActions(DoublePoint point) {
        //TODO: Implement
        return null;
    }

    public void exit() {
        exitItem.fire();
    }

    public Stage getAppStage() {
        return appStage;
    }

    public MenuBar getMenuBar() {
        return menuBar;
    }

    public Menu getFileMenu() {
        return fileMenu;
    }

    public Menu getLayoutMenu() {
        return layoutMenu;
    }

    public Menu getOtherMenu() {
        return otherMenu;
    }

    public MenuItem getImportItem() {
        return importItem;
    }

    public MenuItem getExportItem() {
        return exportItem;
    }

    public MenuItem getExitItem() {
        return exitItem;
    }

    public Menu getChangeLayoutMenu() {
        return changeLayoutMenu;
    }

    public ObservableList<MenuItem> getAvailableLayouts() {
        return availableLayouts;
    }

    public MenuItem getPropertiesItem() {
        return propertiesItem;
    }

    public MenuItem getGroupItem() {
        return groupItem;
    }

    public MenuItem getFilterItem() {
        return filterItem;
    }
}
