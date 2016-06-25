package edu.kit.student.graphRegex;

import java.util.List;

import edu.kit.student.graphmodel.Graph;

/**
 * A Selector for describing graphs.
 * The Selector describes a graph with a String. It is used by the {@link Engine} to select subgraphs of Graph.
 * A Selector is also able to save Lists of subgraphs so that results can be memorized.
 */
public class Selector {
	private String value;
	private List<Graph> selectedSubgraphs;

	/**
	 * Gets the String which is used to select the subgraph
	 * @return value Selector String
	 */
	public String getValue() {
	 	 return value; 
	}

	/**
	 * Sets the String which is used to select the subgraph
	 * @param value Selector String
     */
	public void setValue(String value) { 
		 this.value = value; 
	}

	/**
	 * Gets the previously selected Subgraphs
	 * @return {@link List} of Subgraphs
     */
	public List<Graph> getSelectedSubgraphs() {
	 	 return selectedSubgraphs; 
	}

	/**
	 * Sets subgraphs for this Selector
	 * @param selectedSubgraphs sets the selected Subgraphs
     */
	void setSelectedSubgraphs(List<Graph> selectedSubgraphs) {
		 this.selectedSubgraphs = selectedSubgraphs; 
	}
}
