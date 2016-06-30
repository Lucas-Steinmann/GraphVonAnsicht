package edu.kit.student.joana.tests;

import static org.junit.Assert.assertTrue;

import java.util.Set;

import org.junit.Test;

import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;
import edu.kit.student.sugiyama.steps.CycleRemover;



/**
 * A test class for cycle removing, first step in sugiyama framework
 */
public class CycleRemoverTest {

	@Test
	public void testSimpleCycle(){
		MethodGraph MGraph = new MethodGraph("",0);
		DefaultDirectedGraph<JoanaVertex, JoanaEdge> DDGraph = new DefaultDirectedGraph<JoanaVertex, JoanaEdge>("",0);
		JoanaVertex v1 = new JoanaVertex("v1", "", 1);
		JoanaVertex v2 = new JoanaVertex("v2", "", 2);
		JoanaVertex v3 = new JoanaVertex("v3", "", 3);
		JoanaEdge e1 = new JoanaEdge("e1","",4);
		JoanaEdge e2 = new JoanaEdge("e2","",5);
		JoanaEdge e3 = new JoanaEdge("e3","",6);
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
}
