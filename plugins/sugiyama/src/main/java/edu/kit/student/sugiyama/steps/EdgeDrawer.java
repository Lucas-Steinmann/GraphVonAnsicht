package edu.kit.student.sugiyama.steps;

import java.util.Set;

import edu.kit.student.sugiyama.graph.IEdgeDrawerGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;

/**
 * This class takes a directed graph, as a {@link SugiyamaClass}.
 * It removes dummy vertices and reverses previously reversed edges.
 * Afterwards it assigns every edge points it must run through.
 * 
 * @param <G> the type of the directed graph
 * @param <V> the type of the vertices the graph contains
 * @param <E> the type of the directed edges the graph contains
 */
public class EdgeDrawer implements IEdgeDrawer {

	@Override
	public void drawEdges(IEdgeDrawerGraph graph) {
		restoreReversedEdges(graph);
	}
	
	/**
	 * Restores the reversed edges in the parameter graph.
	 * 
	 * @param graph the graph to reverse the reversed edges from
	 */
	private void restoreReversedEdges(IEdgeDrawerGraph graph){
		Set<SugiyamaEdge> reversedEdges = graph.getReversedEdges();
		for(SugiyamaEdge edge : reversedEdges){
			graph.reverseEdge(edge);
		}
	}
}
