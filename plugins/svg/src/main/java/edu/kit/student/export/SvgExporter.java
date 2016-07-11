package edu.kit.student.export;


import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.graphmodel.serialize.SerializedVertex;
import edu.kit.student.plugin.Exporter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.Set;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class SvgExporter implements Exporter {

    @Override
    public String getSupportedFileEnding() {
        return "*.svg";
    }

    @Override
    public void exportGraph(SerializedGraph graph, FileOutputStream filestream) {
        //get vertices
        /*
        Set<SerializedVertex> vertices = graph.getVertices();
        for(SerializedVertex v : vertices) {
            Map<String, String> props = v.getShapeProperties();
            for(String s: props.keySet()){
                System.out.print(s + ": ");
                System.out.print(props.get(s));
                System.out.println("");
            }
            System.out.println("");
        }
        
        System.out.println("test");
        */
        
        //Create new DOM document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        Document document = docBuilder.newDocument();
        
        //create Nodes for DOM
        String root = "svg";
        Element rootElement = document.createElement(root);
        
        document.appendChild(rootElement);
        
        Element em = document.createElement("rect");
        em.setAttribute("width", "200");
        em.setAttribute("height", "300");
        rootElement.appendChild(em);
        
        
        //Transform DOM tree to writeable file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = transformerFactory.newTransformer();
        } catch (TransformerConfigurationException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        DOMSource source = new DOMSource(document);

        StreamResult result =  new StreamResult(new StringWriter());

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "5");
        try {
            transformer.transform(source, result);
        } catch (TransformerException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        
        
        //write to Fileoutputstream and save file
        try {

            // get the content in bytes
            String xmlString = result.getWriter().toString();
            byte[] contentInBytes = xmlString.getBytes();

            filestream.write(contentInBytes);
            filestream.flush();
            filestream.close();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (filestream != null) {
                    filestream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
