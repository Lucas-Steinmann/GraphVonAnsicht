package edu.kit.student.plugin;

import java.io.FileOutputStream;

import edu.kit.student.graphmodel.SerializedGraph;

/**
 * The exporter interface is implemented to export a graph from it's internal representation into a specific file.
 * For every graph structure given as SerializedGraph/SerializedVertex/SerializedEdge interfaces the 
 * implementing class translates it into a FileOutputStream for the given file type, by {@code getSupportedFileEnding}.
 */
public interface Exporter {

	/**
	 * Gets all filetypes which this exporter can parse.
	 *
	 * @return the supported file ending.
	 */
	public String getSupportedFileEnding();

	/**
	 * Gets the name of this importer.
	 * 
	 * @return name of this exporter
	 */
	public String getName();

	/**
	 * This method writes an {@link SerializedGraph} into an FileOutputStream.
	 * The {@link SerializedGraph} enables us to read all attributes as Strings. To write the contained Information
	 * into the filestream is the task of this method
	 * 
	 * @param graph serializedGraph that contains the information to write to a file
	 * @param filestream to write the information into
	 */
	public void exportGraph(SerializedGraph graph, FileOutputStream filestream); 

}
