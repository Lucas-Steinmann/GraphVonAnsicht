package edu.kit.student.graphml;

import edu.kit.student.graphmodel.builder.GraphBuilderException;
import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import edu.kit.student.plugin.Importer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.Objects;

import javax.xml.parsers.*;

/**
 * GraphMLImporter writes the data from a GraphML stream to a graph.
 * @author Jonas Fehrenbach, Lucas Steinmann
 */
public class GraphmlImporter implements Importer {


    /**
     * Returns an IGraphModelBuilder, which can build the set of graphs,
     * specified by the data from the GraphML input stream.
     * 
     * @param builder the builder to build the graph
     * @param graphmlInputStream the input stream defining the GraphML graph
     * @throws ParseException if the file could not be parsed.
     */
    public void importGraph(IGraphModelBuilder builder, FileInputStream graphmlInputStream) throws ParseException, IOException {

        try {
             SAXParserFactory factory = SAXParserFactory.newInstance();
             SAXParser saxParser = factory.newSAXParser();
             saxParser.parse(graphmlInputStream, new GraphMLHandler(builder));
             builder.build();
        } catch (SAXException|ParserConfigurationException  e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (GraphBuilderException e) {
            throw new ParseException("Exception was thrown during build process:\n" + e.getMessage(), 0);
        }
    }

    @Override
    public String getSupportedFileEndings() {
        return "*.graphml";
    }


}