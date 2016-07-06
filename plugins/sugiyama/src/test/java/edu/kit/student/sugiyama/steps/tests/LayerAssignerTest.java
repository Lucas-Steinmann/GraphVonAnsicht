package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.ISugiyamaVertex;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.LayerAssigner;

import org.junit.Before;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;

/**
 * Created by Sven on 04.07.2016.
 */
public class LayerAssignerTest {
    private LayerAssigner assigner;

    @Before
    public void setUp() throws Exception {
        assigner = new LayerAssigner();
    }

    @Test
    public void assignLayers() throws Exception {
        DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("");
        DefaultVertex v1 = new DefaultVertex("v1", "0"); //use labels to save the desired Layer for Vertex
        DefaultVertex v2 = new DefaultVertex("v2", "0");
        DefaultVertex v3 = new DefaultVertex("v3", "1");
        DefaultVertex v4 = new DefaultVertex("v3", "2");
        DefaultVertex v5 = new DefaultVertex("v3", "2");
        DirectedEdge<DefaultVertex> e1 = new DefaultDirectedEdge<DefaultVertex>("e1","");
        DirectedEdge<DefaultVertex> e2 = new DefaultDirectedEdge<DefaultVertex>("e2","");
        DirectedEdge<DefaultVertex> e3 = new DefaultDirectedEdge<DefaultVertex>("e3","");
        DirectedEdge<DefaultVertex> e4 = new DefaultDirectedEdge<DefaultVertex>("e3","");
        DirectedEdge<DefaultVertex> e5 = new DefaultDirectedEdge<DefaultVertex>("e3","");
        e1.setVertices(v1, v3);
        e2.setVertices(v2, v3);
        e3.setVertices(v2, v5);
        e4.setVertices(v3, v5);
        e5.setVertices(v3, v4);
        DDGraph.addVertex(v1);
        DDGraph.addVertex(v2);
        DDGraph.addVertex(v3);
        DDGraph.addVertex(v4);
        DDGraph.addVertex(v5);
        DDGraph.addEdge(e1);
        DDGraph.addEdge(e2);
        DDGraph.addEdge(e3);
        DDGraph.addEdge(e4);
        DDGraph.addEdge(e5);
        
        SugiyamaGraph sugiyamaGraph = new SugiyamaGraph(DDGraph);

        assigner.assignLayers(sugiyamaGraph);

        Set<ISugiyamaVertex> vertices = sugiyamaGraph.getVertexSet();

        for (ISugiyamaVertex vertex : vertices) {
            Integer layer = vertex.getLayer();
            assertTrue(layer.toString().equals(vertex.getLabel()));
        }
    }
    
    @Test
    public void LayerAssignerTest2(){
    	DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("");
        DefaultVertex v1 = new DefaultVertex("v1", "0");
        DefaultVertex v2 = new DefaultVertex("v2", "0");
        DefaultVertex v3 = new DefaultVertex("v3", "0");
        DefaultVertex v4 = new DefaultVertex("v4", "1");
        DefaultVertex v5 = new DefaultVertex("v5", "1");
        DefaultVertex v6 = new DefaultVertex("v6", "2");
        DefaultVertex v7 = new DefaultVertex("v7", "3");
        DirectedEdge<DefaultVertex> e1 = new DefaultDirectedEdge<DefaultVertex>("e1","");
        DirectedEdge<DefaultVertex> e2 = new DefaultDirectedEdge<DefaultVertex>("e2","");
        DirectedEdge<DefaultVertex> e3 = new DefaultDirectedEdge<DefaultVertex>("e3","");
        DirectedEdge<DefaultVertex> e4 = new DefaultDirectedEdge<DefaultVertex>("e4","");
        DirectedEdge<DefaultVertex> e5 = new DefaultDirectedEdge<DefaultVertex>("e5","");
        DirectedEdge<DefaultVertex> e6 = new DefaultDirectedEdge<DefaultVertex>("e6","");
        DirectedEdge<DefaultVertex> e7 = new DefaultDirectedEdge<DefaultVertex>("e7","");
        DirectedEdge<DefaultVertex> e8 = new DefaultDirectedEdge<DefaultVertex>("e8","");
        DirectedEdge<DefaultVertex> e9 = new DefaultDirectedEdge<DefaultVertex>("e9","");
        DirectedEdge<DefaultVertex> e10 = new DefaultDirectedEdge<DefaultVertex>("e10","");
        e1.setVertices(v1,v4);
        e2.setVertices(v1,v6);
        e3.setVertices(v6,v7);
        e4.setVertices(v5,v7);
        e5.setVertices(v2,v4);
        e6.setVertices(v2,v5);
        e7.setVertices(v4,v6);
        e8.setVertices(v5,v6);
        e9.setVertices(v3,v4);
        e10.setVertices(v3,v5);
        
        DDGraph.addVertex(v1);
        DDGraph.addVertex(v2);
        DDGraph.addVertex(v3);
        DDGraph.addVertex(v4);
        DDGraph.addVertex(v5);
        DDGraph.addVertex(v6);
        DDGraph.addVertex(v7);
        DDGraph.addEdge(e1);
        DDGraph.addEdge(e2);
        DDGraph.addEdge(e3);
        DDGraph.addEdge(e4);
        DDGraph.addEdge(e5);
        DDGraph.addEdge(e6);
        DDGraph.addEdge(e7);
        DDGraph.addEdge(e8);
        DDGraph.addEdge(e9);
        DDGraph.addEdge(e10);

        SugiyamaGraph sugiyamaGraph = new SugiyamaGraph(DDGraph);

        assigner.assignLayers(sugiyamaGraph);

        Set<ISugiyamaVertex> vertices = sugiyamaGraph.getVertexSet();

        for (ISugiyamaVertex vertex : vertices) {
            Integer layer = vertex.getLayer();
            System.out.println(vertex.getID()+","+layer);
            assertTrue(layer.toString().equals(vertex.getLabel()));
        }
    }

}