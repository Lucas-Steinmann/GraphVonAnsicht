package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.CrossMinimizer;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertTrue;

/**
 * Created by Sven on 04.07.2016.
 */
public class CrossMinimizerTest {
    CrossMinimizer minimizer;

    @Before
    public void setUp() throws Exception {
        this.minimizer = new CrossMinimizer();
    }

    
    public void minimizeCrossings() throws Exception {
        DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<>();
        DefaultVertex v1 = new DefaultVertex("v1", "0");
        DefaultVertex v2 = new DefaultVertex("v2", "0");
        DefaultVertex v3 = new DefaultVertex("v3", "0");
        DefaultVertex v4 = new DefaultVertex("v4", "0");
        DefaultVertex v5 = new DefaultVertex("v5", "1");
        DefaultVertex v6 = new DefaultVertex("v6", "1");
        DefaultVertex v7 = new DefaultVertex("v7", "1");
        DefaultVertex v8 = new DefaultVertex("v8", "2");
        DefaultVertex v9 = new DefaultVertex("v9", "2");
        DirectedEdge e1 = new DefaultDirectedEdge<DefaultVertex>("e1","", v1, v9);
        DirectedEdge e2 = new DefaultDirectedEdge<DefaultVertex>("e2","", v1, v6);
        DirectedEdge e3 = new DefaultDirectedEdge<DefaultVertex>("e3","", v2, v6);
        DirectedEdge e4 = new DefaultDirectedEdge<DefaultVertex>("e4","", v3, v7);
        DirectedEdge e5 = new DefaultDirectedEdge<DefaultVertex>("e5","", v4, v7);
        DirectedEdge e6 = new DefaultDirectedEdge<DefaultVertex>("e6","", v6, v8);
        DirectedEdge e7 = new DefaultDirectedEdge<DefaultVertex>("e7","", v6, v9);
        DirectedEdge e8 = new DefaultDirectedEdge<DefaultVertex>("e8","", v7, v9);
//        e1.setVertices(v1, v5);	old edges
//        e2.setVertices(v1, v6);
//        e3.setVertices(v2, v6);
//        e4.setVertices(v3, v7);
//        e5.setVertices(v4, v7);
//        e6.setVertices(v6, v8);
//        e7.setVertices(v6, v9);
//        e8.setVertices(v7, v9);
        
        
        DDGraph.addVertex(v9);
        DDGraph.addVertex(v2);
        DDGraph.addVertex(v7);
        DDGraph.addVertex(v4);
        DDGraph.addVertex(v6);
        DDGraph.addVertex(v8);
        DDGraph.addVertex(v5);
        DDGraph.addVertex(v3);
        DDGraph.addVertex(v1);
        DDGraph.addEdge(e1);
        DDGraph.addEdge(e2);
        DDGraph.addEdge(e3);
        DDGraph.addEdge(e4);
        DDGraph.addEdge(e5);
        DDGraph.addEdge(e6);
        DDGraph.addEdge(e7);
        DDGraph.addEdge(e8);

        SugiyamaGraph SGraph = new SugiyamaGraph(DDGraph);

        for (ISugiyamaVertex vertex : SGraph.getVertexSet()) {
            SGraph.assignToLayer(vertex, Integer.parseInt(vertex.getLabel()));
        }
        minimizer.minimizeCrossings(SGraph);
    }

    @Test
    public void singleRandomTest(){
    	SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(20, 3, 4, true, (new Random()).nextLong());
        int crossingsBefore = CrossMinimizer.crossings(sugiyamaGraph);
        minimizer.minimizeCrossings(sugiyamaGraph);
        int crossingAfter = CrossMinimizer.crossings(sugiyamaGraph);
        assertTrue("crossingsBefore should be smaller or equal to crossingsAfter", crossingsBefore <= crossingAfter);
    }

    @Test
    public void randomTests() {
        for (int i = 10; i < 30; i++) {
            SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(i, 2, 8, true, (new Random()).nextLong());
            int crossingsBefore = CrossMinimizer.crossings(sugiyamaGraph);
            minimizer.minimizeCrossings(sugiyamaGraph);
            int crossingAfter = CrossMinimizer.crossings(sugiyamaGraph);
            assertTrue("crossingsBefore should be smaller or equal to crossingsAfter", crossingsBefore <= crossingAfter);
        }
    }

    @Test
    public void performanceTest() {
        for (int i = 0; i < 20; i++) {
            SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(75, 2, 8, true, (new Random()).nextLong());
            int crossingsBefore = CrossMinimizer.crossings(sugiyamaGraph);
            minimizer.minimizeCrossings(sugiyamaGraph);
            int crossingAfter = CrossMinimizer.crossings(sugiyamaGraph);
            assertTrue("crossingsBefore should be smaller or equal to crossingsAfter", crossingsBefore <= crossingAfter);
        }
    }

    @Test
    public void hugeTest() {
        SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(250, 2, 6, true, (new Random()).nextLong());
        int crossingsBefore = CrossMinimizer.crossings(sugiyamaGraph);
        minimizer.minimizeCrossings(sugiyamaGraph);
        int crossingAfter = CrossMinimizer.crossings(sugiyamaGraph);
        assertTrue("crossingsBefore should be smaller or equal to crossingsAfter", crossingsBefore <= crossingAfter);
    }
}