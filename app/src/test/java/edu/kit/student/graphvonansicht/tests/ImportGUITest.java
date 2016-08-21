package edu.kit.student.graphvonansicht.tests;

import java.util.concurrent.TimeoutException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.testfx.api.FxToolkit;

import javafx.stage.Stage;

public class ImportGUITest {

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
    public void importAll() {
//        File resources = new File("../plugins/joana/src/test/resources");
//
//        System.out.println(resources.getAbsolutePath());
//        List<File> files = Arrays.asList(resources.listFiles()).stream()
//                                                    .filter((file)-> file.getName().endsWith(".graphml"))
//                                                    .collect(Collectors.toList());
//        for (File file : files) {
//            handle.importGraph(file);
//        }

    }
}
