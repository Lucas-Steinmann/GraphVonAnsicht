package edu.kit.student.plugin;

import java.io.FileOutputStream;
import java.util.List;

import edu.kit.student.graphmodel.serialize.SerializedGraph;

/**
 * The exporter interface is implemented to export a graph 
 * from it's internal representation into a specific file.
 * For every graph structure given as 
 * SerializedGraph/SerializedVertex/SerializedEdge interfaces the 
 * implementing class translates it into a FileOutputStream for 
 * the given file type, by {@code getSupportedFileEnding}.
 */
public interface Exporter {

    /**
     * Gets all filetypes which this exporter can parse.
     *
     * @return the supported file ending.
     */
    public List<String> getSupportedFileEndings();
    
    
    /**
     * A very short description of the file types.
     * Examples: Image, Vector-Graphic, GraphML
     * @return the description
     */
    public String getFileEndingDescription();

    /**
     * This method writes an {@link SerializedGraph} into an FileOutputStream.
     * The {@link SerializedGraph} enables us to read all attributes as Strings.
     * To write the contained Information into the file stream is the task of this method
     * 
     * @param graph         serializedGraph that contains the information to write to a file
     * @param filestream    to write the information into
     * @param fileExtension the file extension of the file to write the information to
     * @throws Exception 
     */
    public void exportGraph(SerializedGraph graph, FileOutputStream filestream, String fileExtension) throws Exception; 

}
