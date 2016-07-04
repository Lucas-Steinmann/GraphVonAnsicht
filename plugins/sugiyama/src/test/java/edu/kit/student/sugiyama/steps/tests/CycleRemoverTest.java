package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.steps.CycleRemover;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertTrue;


/**
 * A test class for cycle removing, first step in sugiyama framework
 */
public class CycleRemoverTest {

	@Test
	public void testSimpleCycle(){
//		MethodGraph MGraph = new MethodGraph("",0);	also works with a method graph if necessary
		DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("");
		DefaultVertex v1 = new DefaultVertex("v1", "");
		DefaultVertex v2 = new DefaultVertex("v2", "");
		DefaultVertex v3 = new DefaultVertex("v3", "");
		DirectedEdge e1 = new DirectedEdge("e1","");
		DirectedEdge e2 = new DirectedEdge("e2","");
		DirectedEdge e3 = new DirectedEdge("e3","");
		e1.setVertices(v1, v2);
		e2.setVertices(v2, v3);
		e3.setVertices(v3, v1);
		DDGraph.addVertex(v1);
		DDGraph.addVertex(v2);
		DDGraph.addVertex(v3);
		DDGraph.addEdge(e1);
		DDGraph.addEdge(e2);
		DDGraph.addEdge(e3);
		SugiyamaGraph SGraph = new SugiyamaGraph(DDGraph);
		
		CycleRemover cr = new CycleRemover();
		Set<SugiyamaEdge> set = cr.removeCycles(SGraph);
		assertTrue(set.size()==1);
	}
	
	@Test
	public void testDoubleCycle(){
		DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>>("",0);
		DefaultVertex v1 = new DefaultVertex("v1", "", 1);
		DefaultVertex v2 = new DefaultVertex("v2", "", 2);
		DefaultVertex v3 = new DefaultVertex("v3", "", 3);
		DefaultVertex v4 = new DefaultVertex("v4", "", 4);
		DirectedEdge e1 = new DirectedEdge("e1","",5);
		DirectedEdge e2 = new DirectedEdge("e2","",6);
		DirectedEdge e3 = new DirectedEdge("e3","",7);
		DirectedEdge e4 = new DirectedEdge("e4","",8);
		DirectedEdge e5 = new DirectedEdge("e5","",9);
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
		SugiyamaGraph SGraph = new SugiyamaGraph(DDGraph);
		
		CycleRemover cr = new CycleRemover();
		Set<SugiyamaEdge> set = cr.removeCycles(SGraph);
		set.forEach(edge->System.out.println(edge.getName()));
	}
}
