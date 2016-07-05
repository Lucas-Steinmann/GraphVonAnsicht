package edu.kit.student.sugiyama.steps.tests;

import org.junit.Before;

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
}
