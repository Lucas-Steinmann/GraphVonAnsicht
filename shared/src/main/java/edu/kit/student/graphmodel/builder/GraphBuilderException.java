/**
 * 
 */
package edu.kit.student.graphmodel.builder;

/**
 * This exception should be raised by a builder, 
 * if with the given input the graph could not be build.
 * Importer can catch this and add additional information about the 
 * location of the error in the input file, or pass the error as is for
 * display in the client.
 * 
 * @author Lucas Steinmann
 */
public class GraphBuilderException extends Exception {
	

	private static final long serialVersionUID = 8132068537105475547L;

	public GraphBuilderException(BuilderType type, String message) {
		super(type.toString() + ": " + message);
	}

	@Override
	public String toString() {
		return "GraphBuilderException: " + this.getMessage();
	}
	
	public enum BuilderType { MODEL, GRAPH, VERTEX, EDGE }

}
