package edu.kit.student.graphml;


import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.plugin.Importer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;

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
     * @throws ParseException 
     */
    public void importGraph(IGraphModelBuilder builder, FileInputStream graphmlInputStream) throws ParseException {
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
        //get all childnodes of root
        NodeList childs = root.getChildNodes();
        
        //iterate through childnodes
        for (int j = 0; j < childs.getLength(); j++) {
            Node node = childs.item(j); 
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node; 
                //check if node is a key
                if (element.getNodeName() == "key") {
                    //TODO:process keys
                } else if (node.getNodeName() == "graph") {
                    //get GraphBuilder and parse graph
                    String graphId = element.getAttribute("id");
                    IGraphBuilder graphBuilder = builder.getGraphBuilder(graphId);
                    parseGraph(graphBuilder, element);                
                } else {
                	throw new ParseException("Selected file has wrong syntax!", 0);
                }
            }
        }
        
        builder.build();
    }

    /**
     * Private method to parse a Graph Element.
     * 
     * @param graphBuilder
     * @param graphElement
     * @return
     * @throws ParseException 
     */
    private void parseGraph(IGraphBuilder builder, Element graphElement) throws ParseException {
        NodeList childs = graphElement.getChildNodes();
        
        //iterate through childnodes
        for (int j = 0; j < childs.getLength(); j++) {
            Node node = childs.item(j); 
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node; 
                //check if node is a key
                if (child.getNodeName() == "node") {

                    //Check if this node contains a graph
                    Element childGraph = getGraphVertex(child);
                    if (childGraph != null) {
                        String graphId = childGraph.getAttribute("id");
                        IGraphBuilder graphBuilder = builder.getGraphBuilder(graphId);
                        this.parseGraph(graphBuilder, childGraph);
                    } else {
                        //is a normal vertex so parse Node
                        String vertexId = child.getAttribute("id");
                        IVertexBuilder vertexBuilder = builder.getVertexBuilder(vertexId);
                        this.parseVertex(vertexBuilder, child);
                    }
                } else if (child.getNodeName() == "edge") {
                    IEdgeBuilder edgeBuilder = builder.getEdgeBuilder(child.getAttribute("source"), child.getAttribute("target"));
                    this.parseEdge(edgeBuilder, child);
                }  else {
                    throw new ParseException("Selected file has wrong syntax!", 0);
                }
            }
        }
    }
    
    /**
     * Private method to parse a vertex.
     * 
     * @param builder
     * @param nodeElement
     * @throws ParseException 
     */
    private void parseVertex(IVertexBuilder builder, Element vertexElement) throws ParseException {
        NodeList childs = vertexElement.getChildNodes();
        
        //iterate through childnodes
        for (int j = 0; j < childs.getLength(); j++) {
            Node node = childs.item(j);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node; 
                //check if node is a data
                if (child.getNodeName() == "data") {
                    //get key and value
                    String key = child.getAttribute("key");
                    String value = child.getTextContent();
                    builder.addData(key, value);
                } else {
                	throw new ParseException("Selected file has wrong syntax!", 0);
                }
            }
        }
    }
    
    /**
     * Private method to parse an edge.
     * 
     * @param builder
     * @param nodeElement
     * @throws ParseException 
     */
    private void parseEdge(IEdgeBuilder builder, Element edgeElement) throws ParseException {
        //get source and target
        String source = edgeElement.getAttribute("source");
        String target = edgeElement.getAttribute("target");        
        builder.newEdge(source, target);
        
      //iterate through childnodes
        NodeList childs = edgeElement.getChildNodes();     
        for (int j = 0; j < childs.getLength(); j++) {
            Node node = childs.item(j);
            
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node; 
                //check for data
                if (child.getNodeName() == "data") {
                    //get key and value
                    String key = child.getAttribute("key");
                    String value = child.getTextContent();
                    builder.addData(key, value);
                } else {
                	throw new ParseException("Selected file has wrong syntax!", 0);
                }
            }
        }
    }
    
    /**
     * Checks if this vertex contains a graph.
     * 
     * @param element
     * @return
     */
    private static Element getGraphVertex(Element element) {
        NodeList childs = element.getChildNodes();
        
        //iterate through childnodes
        for (int j = 0; j < childs.getLength(); j++) {
            Node node = childs.item(j);
            
            //check if contains a element as graph
            if (node.getNodeName() == "graph" && node.getNodeType() == Node.ELEMENT_NODE) {
                return (Element) node;
            }
        }    
        
        return null;
    }
    
    @Override
    public String getSupportedFileEndings() {
        return "*.graphml";
    }
}