package edu.kit.student.graphRegex;

import java.util.List;

import edu.kit.student.graphmodel.Graph;

/**
 * The Engine which selects Subgraphs of a Graph for a given {@link Selector}.
 */
public class Engine {

	/**
	 * Finds the Subgraphs for a {@link Selector} in a Graph.
	 * If there are no Subgraphs found for the {@link Selector} or the Graph changed after the last selection
	 * this method selects the subgraphs and saves them into the Selector. Then it returns the List
	 * If Subgraphs for a Selector have already been found and the graph did not undergo changes since then
	 * it returns the found results.
	 * @param graph graph to select subgraphs in
	 * @param selector selector to select subgraphs by
	 * @return {@link List} of selected subgraphs
	 */
	public List<Graph> findSubgraphs(Graph graph, Selector selector) {
		// TODO Auto-generated method
		return null;
	 } 

}
