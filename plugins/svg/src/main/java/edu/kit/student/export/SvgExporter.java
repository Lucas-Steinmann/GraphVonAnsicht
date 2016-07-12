package edu.kit.student.export;


import edu.kit.student.graphmodel.serialize.SerializedEdge;
import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.graphmodel.serialize.SerializedVertex;
import edu.kit.student.plugin.Exporter;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;
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

    private static String rectStyle = "opacity:0.25868728;"
            + "color:#000000;fill:#04B45F;fill-opacity:1;"
            + "fill-rule:nonzero;marker:none;visibility:visible;"
            + "display:inline;overflow:visible;enable-background:accumulate";
    private static String lineStyle = "stroke:rgb(255,0,0);stroke-width:2";
    
    
    @Override
    public String getSupportedFileEnding() {
        return "*.svg";
    }

    @Override
    public void exportGraph(SerializedGraph graph, FileOutputStream filestream) {
        
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
        
        //add Vertex Elements to DOM
        Set<SerializedVertex> vertices = graph.getVertices();
        for (SerializedVertex v : vertices) {
            Element vertex = this.createVertexElement(document, v.getShapeProperties());
            rootElement.appendChild(vertex);

        }
        
        //add Edges to DOM
        
        Set<SerializedEdge> edges = graph.getEdges();
        for (SerializedEdge e : edges) {
            Element edge = this.createEdgeElement(document, e.getShapeProperties());
            rootElement.appendChild(edge);
        }    
        
        
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
    
    
    /**
     * Creates an edge element which contains the path of the edge as svglines.
     * 
     * @param document
     * @param shapeProp
     * @return
     */
    private Element createEdgeElement(Document document, Map<String, String> shapeProp) {
        
        //create Map for coords
        Map<Integer, String> xcoords = new HashMap<Integer, String>();
        Map<Integer, String> ycoords = new HashMap<Integer, String>();
        String label = "";
        
        //set keys in map
        for (String s: shapeProp.keySet()) {
            
            int num = 0;
            
            try {
                num = Integer.parseInt(s.replaceAll("\\D+",""));
            } catch (NumberFormatException e) {
                //TODO: throw Error
            }
            String key = s.replaceAll("\\d+", "");
            
            switch (key) {
              case "x":
                  xcoords.put(num, shapeProp.get(s));
                  break;
              case "y":
                  ycoords.put(num, shapeProp.get(s));
                  break;
              case "label":
                  label = shapeProp.get(s);
                  break;
              default:                          
            }
        }

        //create group element
        Element group = document.createElement("g");
        //add all lines to this group
        for (int i : xcoords.keySet()) {
            //check if x and y coord exist
            if ((xcoords.containsKey(i) && ycoords.containsKey(i)) 
                    && (xcoords.containsKey(i + 1) && ycoords.containsKey(i + 1))) {
                //create Line
                Element line = document.createElement("line");
                line.setAttribute("x1", xcoords.get(i));
                line.setAttribute("y1", ycoords.get(i));
                line.setAttribute("x2", xcoords.get(i + 1));
                line.setAttribute("y2", ycoords.get(i + 1));
                line.setAttribute("style", SvgExporter.lineStyle);
                
                //add to group
                group.appendChild(line);
            } else {
                //TODO:throw Error?
            }
        }
        
        return group;
    }
    
    /**
     * Creates an vertex Element which contains a rectangle for svg.
     * 
     * @param document
     * @param shapeProp
     * @return element of vertex
     */
    private Element createVertexElement(Document document, Map<String, String> shapeProp) {
        
        //get Properties for this Vertex
        String label = "";   
        double minX = 0.0;
        double maxX = 0.0;
        double minY = 0.0;
        double maxY = 0.0;
        
        for (String s: shapeProp.keySet()) {
            
            switch (s) {
              case "minX":
                  minX = Double.parseDouble(shapeProp.get(s));
                  break;
              case "minY":
                  minY = Double.parseDouble(shapeProp.get(s));
                  break;
              case "maxX":
                  maxX = Double.parseDouble(shapeProp.get(s));
                  break;
              case "maxY":
                  maxY = Double.parseDouble(shapeProp.get(s));
                  break;
              case "label":
                  label = shapeProp.get(s);
                  break;
              default:                          
            }
        }
        
        //TODO: Catch NumberFormatException
        double width = Math.abs(maxX - minX);
        double height = Math.abs(maxY - minY);
        
        
        //create Rectangle
        Element rect = document.createElement("rect");
        
        rect.setAttribute("x", Double.toString(minX));
        rect.setAttribute("y", Double.toString(minY));
        rect.setAttribute("width", Double.toString(width));
        rect.setAttribute("height", Double.toString(height));
        rect.setAttribute("style", SvgExporter.rectStyle);
        
        //create group
        Element group = document.createElement("g");
        group.appendChild(rect);
        
        //create text
        Element text = document.createElement("text");
        text.setAttribute("x", Double.toString(minX + 5.0));
        text.setAttribute("y", Double.toString(minY + 15.0));
        text.appendChild(document.createTextNode(label));
        
        group.appendChild(text);
        
        return group;
    }

}
