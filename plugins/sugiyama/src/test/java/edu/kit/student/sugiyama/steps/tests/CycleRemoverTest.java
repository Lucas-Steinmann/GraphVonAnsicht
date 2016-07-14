package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.CycleRemover;
import org.junit.Test;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertTrue;


/**
 * A test class for cycle removing, first step in sugiyama framework
 */
public class CycleRemoverTest {

	@Test
	public void testSimpleCycle(){
//		MethodGraph MGraph = new MethodGraph("",0);
		DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge>("");
		DefaultVertex v1 = new DefaultVertex("v1", "v1");
		DefaultVertex v2 = new DefaultVertex("v2", "v2");
		DefaultVertex v3 = new DefaultVertex("v3", "v3");
		DirectedEdge e1 = new DefaultDirectedEdge<DefaultVertex>("e1","e1",v1, v2);
		DirectedEdge e2 = new DefaultDirectedEdge<DefaultVertex>("e2","e2",v2, v3);
		DirectedEdge e3 = new DefaultDirectedEdge<DefaultVertex>("e3","e3",v3, v1);
		DDGraph.addVertex(v1);
		DDGraph.addVertex(v2);
		DDGraph.addVertex(v3);
		DDGraph.addEdge(e1);
		DDGraph.addEdge(e2);
		DDGraph.addEdge(e3);
		SugiyamaGraph SGraph = new SugiyamaGraph(DDGraph);

		CycleRemover cr = new CycleRemover();
		cr.removeCycles(SGraph);
		
		for (DirectedEdge e : SGraph.getEdgeSet()) {
		    System.out.println(graphicPrint(e));
		}

		assertTrue(isAcyclic(SGraph));
	}
	public String graphicPrint(DirectedEdge edge) {
	    return edge.getSource().getLabel() + " --" + edge.getLabel() + "-> " + edge.getTarget().getLabel();
	}
	
	@Test
	public void testDoubleCycle(){
		DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<DefaultVertex, DirectedEdge>("");
		DefaultVertex v1 = new DefaultVertex("v1", "");
		DefaultVertex v2 = new DefaultVertex("v2", "");
		DefaultVertex v3 = new DefaultVertex("v3", "");
		DefaultVertex v4 = new DefaultVertex("v4", "");
		DirectedEdge e1 = new DefaultDirectedEdge<DefaultVertex>("e1","",v1, v2);
		DirectedEdge e2 = new DefaultDirectedEdge<DefaultVertex>("e2","",v2, v3);
		DirectedEdge e3 = new DefaultDirectedEdge<DefaultVertex>("e3","",v3, v4);
		DirectedEdge e4 = new DefaultDirectedEdge<DefaultVertex>("e4","",v4, v1);
		DirectedEdge e5 = new DefaultDirectedEdge<DefaultVertex>("e5","",v2, v4);
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
		
		assertTrue(isAcyclic(SGraph));
	}

	@Test
	public void RandomGraphsTest() {
		for (int i = 0; i < 20; i++) {
			SugiyamaGraph testGraph = GraphUtil.generateSugiyamaGraph(i*2, (float) Math.pow(0.95, i), true, false);

			CycleRemover cr = new CycleRemover();
			cr.removeCycles(testGraph);
			assertTrue(isAcyclic(testGraph));
		}
	}
	
	@Test
	public void SingleRandomTest(){
		SugiyamaGraph testGraph = GraphUtil.generateSugiyamaGraph(10, 0.2f, true, false);
		CycleRemover cr = new CycleRemover();
//		System.out.println(testGraph.toString());
		cr.removeCycles(testGraph);
//		System.out.println(testGraph.toString());
		assertTrue(isAcyclic(testGraph));
	}
	
	/**
	 * Helper method for calculating whether the inserted graph contains a cycle or not.
	 * 
	 * @param graph input graph
	 * @return the set of vertices after finishing the algorithm. If it is empty, the input graph contained no cycle(s)
	 */
	private boolean isAcyclic(DirectedGraph graph){
		if (graph.getVertexSet().size() <= 1) {
			return true;
		}

		Set<DirectedEdge> edges = new HashSet<>();
		for (Vertex v : graph.getVertexSet()) {
		    System.out.println(graph.incomingEdgesOf(v).size());
		}
		Set<Vertex> vertices = graph.getVertexSet().stream().filter(vertex -> graph.incomingEdgesOf(vertex).size() == 0).collect(Collectors.toSet());

		while (vertices.size() > 0) {
			Vertex vertex = getRandom(vertices);
			vertices.remove(vertex);

			Set<DirectedEdge> newEdges = getCorrectedOutcomingEdges(vertex, edges, graph);

			for (DirectedEdge edge : newEdges) {
				edges.add(edge);

				if (getCorrectedIncomingEdges(edge.getTarget(), edges, graph).size() == 0) {
					vertices.add(edge.getTarget());
				}
			}
		}

		return graph.getEdgeSet().size() == edges.size();
	}

	private Set<DirectedEdge> getCorrectedOutcomingEdges(Vertex vertex, Set<DirectedEdge> graphEdges, DirectedGraph graph) {
		return graph.outgoingEdgesOf(vertex).stream().filter(edge -> !graphEdges.contains(edge)).collect(Collectors.toSet());
	}

	private Set<DirectedEdge> getCorrectedIncomingEdges(Vertex vertex, Set<DirectedEdge> graphEdges, DirectedGraph graph) {
		return graph.incomingEdgesOf(vertex).stream().filter(edge -> !graphEdges.contains(edge)).collect(Collectors.toSet());
	}

	private Vertex getRandom(Set<Vertex> vertices) {
		int size = vertices.size();
		int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
		int i = 0;

		for(Vertex obj : vertices)
		{
			if (i == item) {
				return obj;
			}
			i = i + 1;
		}

		return null;
	}
}
