package edu.kit.student.graphvonansicht.tests;

import java.io.File;
import java.util.concurrent.TimeoutException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import javafx.application.Platform;
import javafx.stage.Stage; 

public class OpenApplicationTest {

    static GUITestHandle handle;
    static Stage primaryStage;
    
    @BeforeClass
    public static void setUpBeforeClass() throws TimeoutException {
        primaryStage = FxToolkit.registerPrimaryStage();
    }

    @Before
    public void setUp() throws Exception {
        handle = new GUITestHandle();
        handle.start(new String[]{});
    }
    
    
    @Test
    public void checkMenu() {
        Assert.assertSame("Graph von Ansicht - Graphviewer", handle.getAppStage().getTitle());
        Assert.assertNotNull(handle.getMenuBar());
        Assert.assertNotNull(handle.getFileMenu());
        Assert.assertNotNull(handle.getImportItem());
        Assert.assertNotNull(handle.getExportItem());
        Assert.assertNotNull(handle.getExitItem());

        Assert.assertNotNull(handle.getLayoutMenu());
        Assert.assertNotNull(handle.getChangeLayoutMenu());
        Assert.assertNotNull(handle.getAvailableLayouts());
        Assert.assertNotNull(handle.getPropertiesItem());

        Assert.assertNotNull(handle.getOtherMenu());
        Assert.assertNotNull(handle.getGroupItem());
        Assert.assertNotNull(handle.getFilterItem());

        Assert.assertNotNull(handle.getGraphViewTabs());
        Assert.assertNotNull(handle.getStructureView());
        Assert.assertNotNull(handle.getInformationView());
    }
    
    @Test
    public void importGraph() {
        handle.importGraph(new File(""));
        Assert.assertNotNull(System.in);
    }

    @After
    public void tearDown() throws Exception {
        Platform.setImplicitExit(false);
        handle.exit();
        Assert.assertNull(handle.getAppStage().getOwner());
        //handle.getAppStage().close();
    }
}
