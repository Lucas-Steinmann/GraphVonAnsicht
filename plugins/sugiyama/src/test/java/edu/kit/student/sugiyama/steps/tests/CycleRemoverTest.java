package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.steps.CycleRemover;
import org.junit.Test;

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
		DirectedEdge<DefaultVertex> e1 = new DirectedEdge<DefaultVertex>("e1","");
		DirectedEdge<DefaultVertex> e2 = new DirectedEdge<DefaultVertex>("e2","");
		DirectedEdge<DefaultVertex> e3 = new DirectedEdge<DefaultVertex>("e3","");
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
		cr.removeCycles(SGraph);
		int reversedCount = 0;
		for(SugiyamaEdge edge : SGraph.getEdgeSet()){
			if(SGraph.isReversed(edge)){
				reversedCount++;
			}
		}
		assertTrue(reversedCount==1);
	}
	
	@Test
	public void testDoubleCycle(){
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
		SugiyamaGraph SGraph = new SugiyamaGraph(DDGraph);
		
		CycleRemover cr = new CycleRemover();
		cr.removeCycles(SGraph);
		int reversedCount = 0;
		for(SugiyamaEdge edge : SGraph.getEdgeSet()){
			if(SGraph.isReversed(edge)){
				reversedCount++;
			}
		}
		assertTrue(reversedCount==1||reversedCount==2);
	}
}
