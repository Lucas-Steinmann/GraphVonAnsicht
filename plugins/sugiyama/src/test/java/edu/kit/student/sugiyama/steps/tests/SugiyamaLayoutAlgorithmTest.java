package edu.kit.student.sugiyama.steps.tests;

import org.junit.Before;
import org.junit.Test;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;
import edu.kit.student.sugiyama.steps.CycleRemover;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.sugiyama.steps.CrossMinimizer;
import edu.kit.student.sugiyama.steps.VertexPositioner;
import edu.kit.student.sugiyama.steps.EdgeDrawer;


public class SugiyamaLayoutAlgorithmTest {

	private SugiyamaLayoutAlgorithm alg;
	
	@Before
	public void setUp(){
		this.alg = new SugiyamaLayoutAlgorithm();
		alg.setCycleRemover(new CycleRemover());
		alg.setLayerAssigner(new LayerAssigner());
		alg.setCrossMinimizer(new CrossMinimizer());
		alg.setVertexPositioner(new VertexPositioner());
		alg.setEdgeDrawer(new EdgeDrawer());
	}
	
	@Test
	public void testSmallGraph(){
//		MethodGraph MGraph = new MethodGraph("",0);
		DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("");
		DefaultVertex v1 = new DefaultVertex("v1", "");
		DefaultVertex v2 = new DefaultVertex("v2", "");
		DefaultVertex v3 = new DefaultVertex("v3", "");
		DefaultVertex v4 = new DefaultVertex("v4", "");
		DirectedEdge<DefaultVertex> e1 = new DirectedEdge<DefaultVertex>("e1","");
		DirectedEdge<DefaultVertex> e2 = new DirectedEdge<DefaultVertex>("e2","");
		DirectedEdge<DefaultVertex> e3 = new DirectedEdge<DefaultVertex>("e3","");
		DirectedEdge<DefaultVertex> e4 = new DirectedEdge<DefaultVertex>("e4","");
		DirectedEdge<DefaultVertex> e5 = new DirectedEdge<DefaultVertex>("e5","");
		e1.setVertices(v1, v2);
		e2.setVertices(v2, v3);
		e3.setVertices(v3, v4);
		e4.setVertices(v4, v1);
		e5.setVertices(v2, v4);
		DDGraph.addVertex(v1);
		DDGraph.addVertex(v2);
		DDGraph.addVertex(v3);
		DDGraph.addVertex(v4);
		DDGraph.addEdge(e1);
		DDGraph.addEdge(e2);
		DDGraph.addEdge(e3);
		DDGraph.addEdge(e4);
		DDGraph.addEdge(e5);
//		this.alg.layout(DDGraph);  does not work with DefalltDirectedGraph, just with DirectedGraph
	}
}
