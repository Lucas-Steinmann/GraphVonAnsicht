package edu.kit.student.graphvonansicht.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test; 

public class OpenApplicationTest {

    static GUITestHandle handle;

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
    }

    @After
    public void tearDown() throws Exception {
        handle.exit();
        Assert.assertNull(handle.getAppStage().getOwner());
    }
}
