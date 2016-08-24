package edu.kit.student.tests;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import edu.kit.student.joana.JoanaGraphModel;
import edu.kit.student.joana.JoanaPlugin;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.VertexFilter;

public class FilterJoanaTest {

    private static List<JoanaGraphModel> model;
    private static JoanaPlugin plugin;

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
         model = JoanaGraphTester.getGraphModelGenerator();
         plugin = new JoanaPlugin();
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void filterCallGraphTest() {
        model.stream().map(m -> m.getCallGraph())
        .forEach(cg ->
        {
            int initialVertexCount = cg.getVertexSet().size();
            int initialEdgeCount = cg.getEdgeSet().size();
            
            EdgeFilter callEdgeFilter = plugin.getEdgeFilter().stream()
                                                              .filter(filter -> filter.getName().equals("CL"))
                                                              .findFirst().get();
            VertexFilter callVertexFilter = plugin.getVertexFilter().stream()
                                                                  .filter(filter -> filter.getName().equals("ENTR"))
                                                                  .findFirst().get();
            // Check matching filter separately
            cg.addEdgeFilter(callEdgeFilter);
            Assert.assertEquals(0, cg.getEdgeSet().size());
            cg.removeEdgeFilter(callEdgeFilter);
            Assert.assertEquals(initialEdgeCount, cg.getEdgeSet().size());

            cg.addVertexFilter(callVertexFilter);
            Assert.assertEquals(0, cg.getVertexSet().size());
            cg.removeVertexFilter(callVertexFilter);
            Assert.assertEquals(initialVertexCount, cg.getVertexSet().size());

            // Check matching filter simultaneously
            cg.addEdgeFilter(callEdgeFilter);
            cg.addVertexFilter(callVertexFilter);
            Assert.assertEquals(0, cg.getEdgeSet().size());
            Assert.assertEquals(0, cg.getVertexSet().size());
            cg.removeEdgeFilter(callEdgeFilter);
            cg.removeVertexFilter(callVertexFilter);
            Assert.assertEquals(initialEdgeCount, cg.getEdgeSet().size());
            Assert.assertEquals(initialVertexCount, cg.getVertexSet().size());
            
            // Check not matching filter
            List<EdgeFilter> notMatchingEFilters = plugin.getEdgeFilter().stream()
                                                                        .filter(filter -> !filter.getName().equals("CL"))
                                                                        .collect(Collectors.toList());

            List<VertexFilter> notMatchingVFilters = plugin.getVertexFilter().stream()
                                                                             .filter(filter -> !filter.getName().equals("ENTR"))
                                                                             .collect(Collectors.toList());
            
            for (EdgeFilter ef : notMatchingEFilters) {
                cg.addEdgeFilter(ef);
                Assert.assertEquals(initialVertexCount, cg.getVertexSet().size());
                Assert.assertEquals(initialEdgeCount, cg.getEdgeSet().size());
            }

            for (VertexFilter vf : notMatchingVFilters) {
                cg.addVertexFilter(vf);
                Assert.assertEquals(initialVertexCount, cg.getVertexSet().size());
                Assert.assertEquals(initialEdgeCount, cg.getEdgeSet().size());
            }
        });
    }

    @Test
    public void filterMethodGraphTest() {

        model.stream().map(m -> m.getMethodGraphs())
        .forEach(mgs ->
        {
            List<Integer> initialVertexCounts = mgs.stream()
                                                    .map(mg -> mg.getVertexSet().size())
                                                    .collect(Collectors.toList());
            List<Integer> initialEdgeCounts = mgs.stream()
                                                 .map(mg -> mg.getEdgeSet().size())
                                                 .collect(Collectors.toList());

            // Check not matching filter
            List<EdgeFilter> edgeFilters = plugin.getEdgeFilter().stream()
                                                                 .collect(Collectors.toList());

            List<VertexFilter> vertexFilters = plugin.getVertexFilter().stream()
                                                                       .collect(Collectors.toList());

            int mgIndex = 0;
            for (MethodGraph mg : mgs) {
                // Add all edge filters
                for (EdgeFilter ef : edgeFilters) { 
                    mg.addEdgeFilter(ef);
                }
                Assert.assertEquals(0, mg.getEdgeSet().size());
                Assert.assertEquals(initialVertexCounts.get(mgIndex).intValue(), mg.getVertexSet().size());
                for (EdgeFilter ef : edgeFilters) { 
                    mg.removeEdgeFilter(ef);
                }
                Assert.assertEquals(initialEdgeCounts.get(mgIndex).intValue(), mg.getEdgeSet().size());
                

                // Add all vertex filters
                for (VertexFilter vf : vertexFilters) {
                    mg.addVertexFilter(vf);
                }
                Assert.assertEquals(0, mg.getEdgeSet().size());
                Assert.assertEquals(0, mg.getVertexSet().size());

                for (VertexFilter vf : vertexFilters) {
                    mg.removeVertexFilter(vf);
                }
                Assert.assertEquals(initialEdgeCounts.get(mgIndex).intValue(), mg.getEdgeSet().size());
                Assert.assertEquals(initialVertexCounts.get(mgIndex).intValue(), mg.getVertexSet().size());
                mgIndex++;
            }
        });
    }
}
