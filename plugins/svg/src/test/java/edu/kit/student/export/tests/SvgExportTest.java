package edu.kit.student.export.tests;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import edu.kit.student.export.SvgExporter;
import edu.kit.student.graphmodel.serialize.SerializedEdge;
import edu.kit.student.graphmodel.serialize.SerializedGraph;
import edu.kit.student.graphmodel.serialize.SerializedVertex;

public class SvgExportTest {
    SvgExporter exporter;

    @Before
    public void setUp() throws Exception {
        this.exporter = new SvgExporter();
    }
    
    //check fileEnding descriptions
    @Test
    public void fileEndingTest(){
        assertTrue(exporter.getFileEndingDescription().equals("SVG"));
        
        List<String> supportedEndings = exporter.getSupportedFileEndings();
        assertTrue(supportedEndings.size() == 1);
        assertTrue(supportedEndings.get(0).equals("svg"));
    }

    
    //check
    @Test
    public void exportSuccessfullTest() throws Exception {
        SerializedGraph graph = createSerializedGraph();
              
        File output = new File("src/test/resources/output.svg");
        FileOutputStream filestream = new FileOutputStream(output);
           
        exporter.exportGraph(graph, filestream, "svg");
    } 
    
    
    //help function to create serializedGraph
    private SerializedGraph createSerializedGraph() {
        
        //create serialized hardcoded vertices
        Set<SerializedVertex> serializedVertices = new HashSet<SerializedVertex>();
        
        for(int i = 0; i < 1; i++) {
            Map<String,String> shapePropertiesV = new HashMap<String,String>();
            shapePropertiesV.put("label", "testlabel");
            shapePropertiesV.put("color", "#232323");
            
            shapePropertiesV.put("border-color", "#333333");
            
            shapePropertiesV.put("minX", Double.toString(1.2));
            shapePropertiesV.put("minY", Double.toString(1.2));
            shapePropertiesV.put("maxX", Double.toString(2.43));
            shapePropertiesV.put("maxY", Double.toString(2.43));
            //shapeProperties.put("arcWidth", Double.toString(shape.getElementShape().getArcWidth()));
            //shapeProperties.put("arcHeight", Double.toString(shape.getElementShape().getArcHeight()));
            
            SerializedVertex serializedV = new SerializedVertex(shapePropertiesV, new HashMap<String, String>());
            serializedVertices.add(serializedV);
        }
        
        //create serialized hardcoded edges
        Set<SerializedEdge> serializedEdges = new HashSet<SerializedEdge>();
        
        for(int i = 0; i < 1; i++) {
            Map<String,String> shapePropertiesE = new HashMap<String,String>();
            
            shapePropertiesE.put("label", "testEdge");
            shapePropertiesE.put("color", "#444444");
            
            for(int j = 0; j < 3; j++) {
                shapePropertiesE.put(j + "x", Double.toString(3 + 2*j));
                shapePropertiesE.put(j + "y", Double.toString(2 + 2.4*j));
            }
            

            SerializedEdge serializedE = new SerializedEdge(shapePropertiesE, new HashMap<String,String>());
            serializedEdges.add(serializedE);
        }
        
        return new SerializedGraph(new HashMap<String, String>(), new HashMap<String, String>(),
                serializedVertices, serializedEdges);
    }
    
    /*
    public List<String> getSupportedFileEndings() {
        List<String> endings = new LinkedList<>();
        endings.add(fileExtension);
        return endings;
    }
    */
    
}
