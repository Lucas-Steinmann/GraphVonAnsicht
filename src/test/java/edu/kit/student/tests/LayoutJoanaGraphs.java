package edu.kit.student.tests;

import java.util.List;

import javafx.application.Application;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.student.joana.JoanaGraphModel;
import edu.kit.student.joana.JoanaPlugin;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.callgraph.CallGraphLayout;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphLayout;
import edu.kit.student.sugiyama.steps.tests.SugiyamaLayoutAlgorithmTest.AsNonApp;

public class LayoutJoanaGraphs {

    private static List<JoanaGraphModel> models;
    private static JoanaPlugin plugin;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
         models = JoanaGraphTester.getGraphModelGenerator();
         plugin = new JoanaPlugin();
         
         Thread t = new Thread("JavaFX Init Thread") {
             public void run() {
                 Application.launch(AsNonApp.class, new String[0]);
             }
         };
         t.setDaemon(true);
         t.start();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void methodLayout() {
        for (JoanaGraphModel model : models) {
            List<MethodGraph> mgs = model.getMethodGraphs();
            MethodGraphLayout mgl = new MethodGraphLayout();
            for (MethodGraph mg : mgs) {
                if (mg.getVertexSet().size() < 700) {
                    mgl.layout(mg);
                }
            }
        }
    }

    @Test
    public void callLayout() {
        for (JoanaGraphModel model : models) {
            CallGraph cg = model.getCallGraph();
            CallGraphLayout cgl = new CallGraphLayout();
            cgl.layout(cg);
        }
    }
}
