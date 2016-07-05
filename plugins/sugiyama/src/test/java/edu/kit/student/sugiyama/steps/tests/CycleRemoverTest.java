package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.CycleRemover;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static org.junit.Assert.assertTrue;


/**
 * A test class for cycle removing, first step in sugiyama framework
 */
public class CycleRemoverTest {

	@Test
	public void testSimpleCycle(){
//		MethodGraph MGraph = new MethodGraph("",0);
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

		assertTrue(isAcyclic(SGraph));
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
		
		assertTrue(isAcyclic(SGraph));
	}

	@Test
	public void RandomGraphsTest() {
		for (int i = 0; i < 20; i++) {
			DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> diGraph = generateTestGraph(i*2, 0.7f);
			SugiyamaGraph testGraph = new SugiyamaGraph(diGraph);

			CycleRemover cr = new CycleRemover();
			cr.removeCycles(testGraph);
			assertTrue(isAcyclic(testGraph));
		}
	}
	
	@Test
	public void SingleRandomTest(){
		DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> diGraph = generateTestGraph(5, 0.7f);
		SugiyamaGraph testGraph = new SugiyamaGraph(diGraph);

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
		Vertex temp = getSink(graph);
		
		while(temp!=null){
			graph.getVertexSet().remove(temp);
			graph.getEdgeSet().removeAll(graph.incomingEdgesOf(temp));
			temp = getSink(graph);
		}

		return graph.getVertexSet().isEmpty();
	}
	
	/**
	 * returns a vertex that has no outgoing vertices, null otherwise.
	 * 
	 * @param graph input graph
	 * @return a sugiyama vertex with out degree 0, null otherwise
	 */
	private Vertex getSink(DirectedGraph graph){
		for(Object v : graph.getVertexSet()){
			Vertex vertex = (Vertex) v;

			if(graph.outdegreeOf(vertex)==0){
				return vertex;
			}
		}
		return null;
	}

	private DefaultDirectedGraph<DefaultVertex, DirectedEdge<DefaultVertex>> generateTestGraph(int vertexCount, float density) {
		DefaultDirectedGraph graph = new DefaultDirectedGraph("randomGraph");
		density = Math.min(Math.max(density, 0f), 1f);
		int edgeCount = (int) (density/2 * vertexCount * (vertexCount - 1));
		List<Vertex> vertices = new LinkedList<>();
		List<DirectedEdge> edges = new LinkedList<>();
		Random random = new Random();

		for (int i = 0; i < vertexCount; i++) {
			DefaultVertex vertex = new DefaultVertex("v" + Integer.toString(i), "");
			vertices.add(vertex);
			graph.addVertex(vertex);

			for (int j = 0; j < i; j++) {
				DirectedEdge edge = new DirectedEdge("e(v" + Integer.toString(i) + ", v" + Integer.toString(j) + ")", "");

				if (random.nextBoolean()) {
					edge.setVertices(vertex, vertices.get(j));
				} else {
					edge.setVertices(vertices.get(j), vertex);
				}

				edges.add(edge);
			}
		}

		while (edgeCount < edges.size()) {
			int removeNumber = random.nextInt(edges.size());
			edges.remove(removeNumber);
		}

		for (DirectedEdge edge : edges) {
			graph.addEdge(edge);
		}

		return graph;
	}
}
