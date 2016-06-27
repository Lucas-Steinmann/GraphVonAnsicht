package edu.kit.student.graphml;


import edu.kit.student.graphmodel.IGraphModelBuilder;
import edu.kit.student.plugin.Importer;

import java.io.FileInputStream;

/**
 * GraphMLImporter writes the data from a GraphML stream to a graph.
 * @author
 */
public class GraphmlImporter implements Importer {

    /**
     * Input the GraphML stream data into the graph.
     * 
     * @param builder the builder to build the graph
     * @param graphmlInputStream the input stream defining the graphml graph
     */
    public void importGraph(IGraphModelBuilder builder, FileInputStream graphmlInputStream) {
        // TODO Auto-generated method
    }

    @Override
    public String getSupportedFileEndings() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }


}