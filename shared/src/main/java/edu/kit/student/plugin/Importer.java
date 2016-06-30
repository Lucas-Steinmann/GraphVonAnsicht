package edu.kit.student.plugin;

import edu.kit.student.graphmodel.builder.IGraphModelBuilder;

import java.io.FileInputStream;

/**
 * The importer interface is implemented when writing a class that can import files.
 * This will enable plugins that import specific files to be build.
 * The main task of a class implementing this interface is 
 * to parse a FileInputStream  into the Interface of an {@link IGraphModelBuilder}.
 * The {@link IGraphModelBuilder} will then build the representation.
 */
public interface Importer {

    /**
     * Gets the file type which this importer can parse.
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
     * It has to ensure that all information is transfered to a correct graphmodel builder.
     * 
     * @param builder that the values are parsed into
     * @param filestream from which the values are parsed
     */
    public void importGraph(IGraphModelBuilder builder, FileInputStream filestream); 

}
