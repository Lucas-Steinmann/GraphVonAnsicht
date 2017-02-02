package edu.kit.student.plugin;

import edu.kit.student.graphmodel.builder.IGraphModelBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

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
     * @return the supported file ending without a dot at the beginning.
     */
    String getSupportedFileEndings();

    /**
     * This method parses an FileInputStream into an {@link IGraphModelBuilder}.
     * It has to ensure that all information is transfered to a correct graphmodel builder.
     * 
     * @param builder that the values are parsed into
     * @param filestream from which the values are parsed
     * @throws ParseException if the file could not be parsed, and the parse process was aborted
     */
    void importGraph(IGraphModelBuilder builder, FileInputStream filestream) throws ParseException, IOException;

}
