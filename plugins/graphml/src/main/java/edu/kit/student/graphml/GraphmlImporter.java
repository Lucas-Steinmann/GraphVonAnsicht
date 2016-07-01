package edu.kit.student.graphml;


import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.plugin.Importer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

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
        //Build Document from FileInputStream
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;
        //TODO: catch exceptions and handle it or forward?
        try {
            docBuilder = factory.newDocumentBuilder();
            document = docBuilder.parse(graphmlInputStream);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            // TODO Write catch
            e.printStackTrace();
        }
         
        //Normalize the document
        document.getDocumentElement().normalize();
         
        //get root node
        Element root = document.getDocumentElement();
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