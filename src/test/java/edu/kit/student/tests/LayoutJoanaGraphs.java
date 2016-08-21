package edu.kit.student.tests;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.student.joana.JoanaGraphModel;
import edu.kit.student.joana.JoanaPlugin;
import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.callgraph.CallGraphLayout;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphLayout;

public class LayoutJoanaGraphs {

    private static JoanaGraphModel model;
    private static JoanaPlugin plugin;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
         model = JoanaGraphTester.generateGraphModel();
         plugin = new JoanaPlugin();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void methodLayout() {
        List<MethodGraph> mgs = model.getMethodGraphs();
        MethodGraphLayout mgl = new MethodGraphLayout();
        for (MethodGraph mg : mgs) {
            mgl.layout(mg);
        }
    }

    @Test
    public void callLayout() {
        CallGraph cg = model.getCallGraph();
        CallGraphLayout cgl = new CallGraphLayout();
        cgl.layout(cg);
    }
}
