package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Vertex;

/**
 * This Class represents a filter for vertex types. 
 * The type of the vertex can be specified through different parameters.
 * 
 *
 */
public class VertexFilter<V extends Vertex> {

	private String name;

	/**
	 * Getter of name
	 */
	public String getName() {
	 	 return name; 
	}

	/**
	 * Setter of name
	 */
	public void setName(String name) { 
		 this.name = name; 
	}

	/**
	 * This method checks if an vertex matches this Filter.
	 * It will compare specified parameters of the vertex with the defined parameters of this filter.
	 * 
	 * @param toMatch the vertex which should be checked
	 * @return true if the edge matches this Filter, otherwise false
	 */
	public boolean matches(V toMatch) { 
		// TODO Auto-generated method
		return false;
	 } 

}
