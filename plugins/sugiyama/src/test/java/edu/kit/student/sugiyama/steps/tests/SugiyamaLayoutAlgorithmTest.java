package edu.kit.student.sugiyama.steps.tests;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.sugiyama.SugiyamaLayoutAlgorithm;
import edu.kit.student.sugiyama.steps.*;
import javafx.application.Application;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;


public class SugiyamaLayoutAlgorithmTest {

	private SugiyamaLayoutAlgorithm<LayeredGraph> alg;

	public static class AsNonApp extends Application {
		@Override
		public void start(Stage primaryStage) throws Exception {
			// noop
		}
	}

	@BeforeClass
	public static void initJFX() {
		Thread t = new Thread("JavaFX Init Thread") {
			public void run() {
				Application.launch(AsNonApp.class, new String[0]);
			}
		};
		t.setDaemon(true);
		t.start();
	}
	
	@Before
	public void setUp(){
		this.alg = new SugiyamaLayoutAlgorithm<LayeredGraph>();
		alg.setCycleRemover(new CycleRemover());
		alg.setLayerAssigner(new LayerAssigner());
		alg.setCrossMinimizer(new CrossMinimizer());
		alg.setVertexPositioner(new VertexPositioner());
		alg.setEdgeDrawer(new EdgeDrawer());
	}
	
	@Test
	public void testSmallGraph(){
//		MethodGraph MGraph = new MethodGraph("",0);
		DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<>();
		DefaultVertex v1 = new DefaultVertex("v1", "");
		DefaultVertex v2 = new DefaultVertex("v2", "");
		DefaultVertex v3 = new DefaultVertex("v3", "");
		DefaultVertex v4 = new DefaultVertex("v4", "");
		DirectedEdge e1 = new DefaultDirectedEdge<DefaultVertex>("e1","", v1, v2);
		DirectedEdge e2 = new DefaultDirectedEdge<DefaultVertex>("e2","", v2, v3);
		DirectedEdge e3 = new DefaultDirectedEdge<DefaultVertex>("e3","", v3, v4);
		DirectedEdge e4 = new DefaultDirectedEdge<DefaultVertex>("e4","", v4, v1);
		DirectedEdge e5 = new DefaultDirectedEdge<DefaultVertex>("e5","", v2, v4);
		DDGraph.addVertex(v1);
		DDGraph.addVertex(v2);
		DDGraph.addVertex(v3);
		DDGraph.addVertex(v4);
		DDGraph.addEdge(e1);
		DDGraph.addEdge(e2);
		DDGraph.addEdge(e3);
		DDGraph.addEdge(e4);
		DDGraph.addEdge(e5);
		this.alg.layout(DDGraph);
	}

	@Test
	public void testIsolatedGraph() {
		DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = new DefaultDirectedGraph<>();
		DefaultVertex v1 = new DefaultVertex("v1", "");
		DefaultVertex v2 = new DefaultVertex("v2", "");
		DefaultVertex v3 = new DefaultVertex("v3", "");
		DefaultVertex v4 = new DefaultVertex("v4", "");
		DirectedEdge e1 = new DefaultDirectedEdge<DefaultVertex>("e1","", v1, v2);
		DirectedEdge e2 = new DefaultDirectedEdge<DefaultVertex>("e3","", v3, v4);
		DDGraph.addVertex(v1);
		DDGraph.addVertex(v2);
		DDGraph.addVertex(v3);
		DDGraph.addVertex(v4);
		DDGraph.addEdge(e1);
		DDGraph.addEdge(e2);
		this.alg.layout(DDGraph);
	}

	@Test
	public void testRandomGraph() {
		for (int i = 0; i < 3; i++) {
			long timeBefore = (new Date()).getTime();
			DefaultDirectedGraph<DefaultVertex, DirectedEdge> DDGraph = GraphUtil.generateGraph(100, 0.04f, true);
			this.alg.layout(DDGraph);
			System.out.println("runs in " + ((new Date()).getTime() - timeBefore) + "ms");
		}
	}
}
