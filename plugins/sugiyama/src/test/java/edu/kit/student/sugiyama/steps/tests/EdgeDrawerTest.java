package edu.kit.student.sugiyama.steps.tests;

import org.junit.Before;
import org.junit.Test;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.steps.CrossMinimizer;
import edu.kit.student.sugiyama.steps.CycleRemover;
import edu.kit.student.sugiyama.steps.EdgeDrawer;
import edu.kit.student.sugiyama.steps.LayerAssigner;
import edu.kit.student.sugiyama.steps.VertexPositioner;

public class EdgeDrawerTest {
	CycleRemover remover;
	LayerAssigner assigner;
	CrossMinimizer minimizer;
	VertexPositioner positioner;
	EdgeDrawer drawer;
	
	
	
	@Before
	public void init(){
		this.remover = new CycleRemover();
		this.assigner = new LayerAssigner();
		this.minimizer = new CrossMinimizer();
		this.positioner = new VertexPositioner();
		this.drawer = new EdgeDrawer();
	}
	
	@Test
	public void compileTest(){
		DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<>();
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
		
		this.remover.removeCycles(SGraph);
		this.assigner.assignLayers(SGraph);
		this.minimizer.minimizeCrossings(SGraph);
		this.positioner.positionVertices(SGraph);
		this.drawer.drawEdges(SGraph);
	}
}
