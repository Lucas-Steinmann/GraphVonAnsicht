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

import java.util.Date;
import java.util.Random;

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
        DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("");
        DefaultVertex v1 = new DefaultVertex("v1", "0");
        DefaultVertex v2 = new DefaultVertex("v2", "0");
        DefaultVertex v3 = new DefaultVertex("v3", "0");
        DefaultVertex v4 = new DefaultVertex("v4", "0");
        DefaultVertex v5 = new DefaultVertex("v5", "1");
        DefaultVertex v6 = new DefaultVertex("v6", "1");
        DefaultVertex v7 = new DefaultVertex("v7", "1");
        DefaultVertex v8 = new DefaultVertex("v8", "2");
        DefaultVertex v9 = new DefaultVertex("v9", "2");
        DirectedEdge<DefaultVertex> e1 = new DefaultDirectedEdge<DefaultVertex>("e1","");
        DirectedEdge<DefaultVertex> e2 = new DefaultDirectedEdge<DefaultVertex>("e2","");
        DirectedEdge<DefaultVertex> e3 = new DefaultDirectedEdge<DefaultVertex>("e3","");
        DirectedEdge<DefaultVertex> e4 = new DefaultDirectedEdge<DefaultVertex>("e4","");
        DirectedEdge<DefaultVertex> e5 = new DefaultDirectedEdge<DefaultVertex>("e5","");
        DirectedEdge<DefaultVertex> e6 = new DefaultDirectedEdge<DefaultVertex>("e6","");
        DirectedEdge<DefaultVertex> e7 = new DefaultDirectedEdge<DefaultVertex>("e7","");
        DirectedEdge<DefaultVertex> e8 = new DefaultDirectedEdge<DefaultVertex>("e8","");
//        e1.setVertices(v1, v5);	old edges
//        e2.setVertices(v1, v6);
//        e3.setVertices(v2, v6);
//        e4.setVertices(v3, v7);
//        e5.setVertices(v4, v7);
//        e6.setVertices(v6, v8);
//        e7.setVertices(v6, v9);
//        e8.setVertices(v7, v9);
        
        e1.setVertices(v1, v9);
        e2.setVertices(v1, v6);
        e3.setVertices(v2, v6);
        e4.setVertices(v3, v7);
        e5.setVertices(v4, v7);
        e6.setVertices(v6, v8);
        e7.setVertices(v6, v9);
        e8.setVertices(v7, v9);
        
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
        minimizer.minimizeCrossings(sugiyamaGraph);
    }

    @Test
    public void randomTests() {
        for (int i = 10; i < 30; i++) {
            SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(i, 2, 8, true, (new Random()).nextLong());
            long timeStart = (new Date()).getTime();
            minimizer.minimizeCrossings(sugiyamaGraph);
            System.out.println("time for run with " + i + " random vertices: " + ((new Date()).getTime() - timeStart) + "ms");
            System.out.println("");
        }
    }


    public void performanceTest() {
        for (int i = 0; i < 100; i++) {
            SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(75, 2, 8, true, (new Random()).nextLong());
            long timeStart = (new Date()).getTime();
            minimizer.minimizeCrossings(sugiyamaGraph);
            System.out.println("run " + i + " time : " + ((new Date()).getTime() - timeStart) + "ms");
            System.out.println("");
        }
    }

    public void hugeTest() {
        SugiyamaGraph sugiyamaGraph = GraphUtil.generateSugiyamaGraph(250, 3, 9, true, (new Random()).nextLong());
        long timeStart = (new Date()).getTime();
        minimizer.minimizeCrossings(sugiyamaGraph);
        System.out.println("time for run with " + 250 + " random vertices: " + ((new Date()).getTime() - timeStart) + "ms");
    }
}