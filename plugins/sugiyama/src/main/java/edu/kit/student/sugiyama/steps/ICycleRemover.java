package edu.kit.student.sugiyama.steps;

import java.util.Set;

import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.sugiyama.graph.ICycleRemoverGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph;
import edu.kit.student.sugiyama.graph.SugiyamaGraph.SugiyamaEdge;

/**
 * This interfaces represents a class that generates a DAG(Directed Acyclic Graph) from a {@link DirectedGraph}.
 */
public interface ICycleRemover {


	/**
	 * Searches for a acyclic subgraph in the graph argument and reversed the direction of the edges that
	 * are not part of this subgraph.
	 * 
	 * @param  graph the input graph to remove cycles from
	 * @return       a set of edges whose direction has been reversed in order to remove cycles from the graph
	 */
	public Set<SugiyamaEdge> removeCycles(ICycleRemoverGraph graph);
}
