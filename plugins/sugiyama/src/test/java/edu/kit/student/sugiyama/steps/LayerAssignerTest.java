package edu.kit.student.sugiyama.steps;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
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
        DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("",0);
        DefaultVertex v1 = new DefaultVertex("v1", "0", 0); //use labels to save the desired Layer for Vertex
        DefaultVertex v2 = new DefaultVertex("v2", "0", 1);
        DefaultVertex v3 = new DefaultVertex("v3", "1", 2);
        DefaultVertex v4 = new DefaultVertex("v3", "2", 3);
        DefaultVertex v5 = new DefaultVertex("v3", "2", 4);
        DirectedEdge e1 = new DirectedEdge("e1","",5);
        DirectedEdge e2 = new DirectedEdge("e2","",6);
        DirectedEdge e3 = new DirectedEdge("e3","",7);
        DirectedEdge e4 = new DirectedEdge("e3","",8);
        DirectedEdge e5 = new DirectedEdge("e3","",9);
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

        Set<SugiyamaGraph.SugiyamaVertex> vertices = sugiyamaGraph.getVertexSet();

        for (SugiyamaGraph.SugiyamaVertex vertex : vertices) {
            Integer layer = vertex.getLayer();
            assertTrue(layer.toString().equals(vertex.getLabel()));
        }
    }

}