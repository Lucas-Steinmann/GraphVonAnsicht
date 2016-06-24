package edu.kit.student.plugin;

import java.io.FileInputStream;

import edu.kit.student.graphmodel.IGraphModelBuilder;

/**
 * The importer interface is implemented when writting a class that can import files.
 * This will enable Plugins that import specific files to be build.
 * The main task of a class implementing this interface is to parse a FileInputStream  into the Interface of an {@link IGraphModelBuilder}.
 * The {@link IGraphModelBuilder} will then build the representation.
 */
public interface Importer {

	/**
	 * Gets the filetype which this importer can parse.
	 * 
	 * @return the supported file ending.
	 */
	public String getSupportedFileEndings();

	/**
	 * Gets the name of this importer.
	 * 
	 * @return name of this importer
	 */
	public String getName();

	/**
	 * This method parses an FileInputStream into an {@link IGraphModelBuilder}.
	 * It has to ensure that all information is transfered to a correct graphmodelbuilder.
	 * 
	 * @param builder that the values are parsed into
	 * @param filestream from which the values are parsed
	 */
	public void importGraph(IGraphModelBuilder builder, FileInputStream filestream); 

}
