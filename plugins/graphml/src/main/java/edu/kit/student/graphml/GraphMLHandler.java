package edu.kit.student.graphml;

import edu.kit.student.graphmodel.builder.IEdgeBuilder;
import edu.kit.student.graphmodel.builder.IGraphBuilder;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.graphmodel.builder.IVertexBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.*;

/**
 * Handler for the SAXParser for GraphML.
 *
 * @author Lucas Steinmann
 */
public class GraphMLHandler extends DefaultHandler {

    private final Logger logger = LoggerFactory.getLogger(GraphMLHandler.class);

    private Map<Key, String> graphMLData; // TODO: Use as GraphModelData

    // Backupped data from the nodes and graphs, surrounding the current position
    private Stack<Map<Key, String>> nodeBackupData;
    private Stack<String> parentNodes;
    private Stack<IGraphBuilder> parentGraphs;

    private IGraphBuilder currentGraph;
    private boolean isDirected; // TODO: Use direction

    private IGraphModelBuilder modelBuilder;
    private List<Key> declaredKeys;

    private Key currentKey;
    private String currentValue;
    private Map<Key, String> currentData;
    private String currentId;
    private GmlToken currentObject;
    private String currentSource;
    private String currentTarget;

    // Blacklists ID of nodes (and in future possibly edges) the node should be ignored on further occurences.
    // This is used e.g. if the node contains a graph.
    // Nodes containing a graph are not parsed, since the notion of sub-graphs is here not expressed
    // via the nesting of a graph in a node.
    private Set<String> blackList;

    // Flags for unsupported structures - true if one has been found
    // used for not spamming warnings.
    private boolean foundPort       = false;
    private boolean foundHyperEdge  = false;
    private boolean foundLocator    = false;
    private boolean foundExtraData  = false;
    private boolean foundDynamicKey = false;


    GraphMLHandler(IGraphModelBuilder modelBuilder) {
        this.modelBuilder = modelBuilder;
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        declaredKeys = new LinkedList<>();
        parentGraphs = new Stack<>();
        parentNodes = new Stack<>();
        nodeBackupData = new Stack<>();
        blackList = new HashSet<>();
        graphMLData = new HashMap<>();
    }

    @Override
    public void endDocument() throws SAXException {
        logger.debug("Finished reading GML-File.");
        super.endDocument();
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        super.startPrefixMapping(prefix, uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        if (qName.equalsIgnoreCase(GmlToken.GRAPHML.getXmlTag())) {
            currentObject = GmlToken.GRAPHML;

        } else if (qName.equalsIgnoreCase(GmlToken.KEY.getXmlTag())) {
            boolean dynamic = false;
            String dynamicVal = attributes.getValue(GmlToken.DYNAMIC.getXmlTag());
            if (dynamicVal != null) {
                if (dynamicVal.equalsIgnoreCase("true")) {
                    // TODO: Support dynamic keys
                    dynamic = true;
                    if (!foundDynamicKey) {
                        logger.warn("Dynamic keys are not supported.");
                        foundDynamicKey = true;
                    }
                }
            }
            String id = attributes.getValue(GmlToken.ID.getXmlTag());
            if (id == null) {
                throwMissingField(GmlToken.KEY, GmlToken.ID);
            }

            GmlToken keyForToken = GmlToken.ALL;
            String characters = attributes.getValue(GmlToken.FOR.getXmlTag());
            if (characters != null) {
                found:
                {
                    for (GmlToken token : GmlToken.values()) {
                        if (token.isKeyFor() && token.getXmlTag().equalsIgnoreCase(characters)) {
                            keyForToken = token;
                            break found;
                        }
                    }
                    throwInvalidField(GmlToken.KEY, GmlToken.FOR, characters);
                }
            }

            GmlToken attrTypeToken = null;
            characters = attributes.getValue(GmlToken.ATTRTYPE.getXmlTag());
            if (characters != null) {
                found:
                {
                    for (GmlToken token : GmlToken.values()) {
                        if (token.isKeyType() && token.getXmlTag().equalsIgnoreCase(characters)) {
                            attrTypeToken = token;
                            break found;
                        }
                    }
                    throwInvalidField(GmlToken.KEY, GmlToken.ATTRTYPE, characters);
                }
            }
            String attrName = attributes.getValue(GmlToken.ATTRNAME.getXmlTag());
            declaredKeys.add(new Key(id, attrName, keyForToken, attrTypeToken));

        } else if (qName.equalsIgnoreCase(GmlToken.GRAPH.getXmlTag())) {
            // TODO: add support for <desc>
            String id = "Graph";
            if (attributes.getValue(GmlToken.ID.getXmlTag()) != null) {
                id = attributes.getValue(GmlToken.ID.getXmlTag());
            }

            logger.debug("New Graph: " + id);
            // Edgedirection in graph
            isDirected = true;
            String direction = attributes.getValue(GmlToken.EDGEDEFAULT.getXmlTag());
            if (direction == null) {
                logger.warn("GraphML-Edgedefault not set for graph: " + id);
            } else if (direction.equalsIgnoreCase(GmlToken.UNDIRECTED.getXmlTag())) {
                isDirected = false;
            } else if (direction.equalsIgnoreCase(GmlToken.DIRECTED.getXmlTag())) {
                isDirected = true;
            } else {
                throwInvalidField(GmlToken.GRAPH, GmlToken.EDGEDEFAULT, direction);
            }

            currentObject = GmlToken.GRAPH;
            if (currentGraph == null) {
                // get a root IGraphBuilder from the IGraphModelBuilder
                currentGraph = modelBuilder.getGraphBuilder(id);
            } else {
                // Push current graph on parent stack
                parentGraphs.push(currentGraph);

                // Backup and null node data.
                blackList.add(currentId);
                parentNodes.push(currentId);
                nodeBackupData.push(currentData);
                currentId = null;
                currentData = null;

                // get a new IGraphBuilder.
                currentGraph = currentGraph.getGraphBuilder(id);
            }
        } else if (qName.equalsIgnoreCase(GmlToken.NODE.getXmlTag())) {
            // Node:
            // Fields:
            //   Requires: id
            //   Not supported: node.extra.attribute
            // Elements:
            //   data  [0,unbounded]
            //   graph [0,1]
            //   Not supported: desc, port, locators
            currentData = new HashMap<>();
            String id = attributes.getValue(GmlToken.ID.getXmlTag());
            if (id == null) {
                throwMissingField(GmlToken.NODE, GmlToken.ID);
            } else {
                currentId = id;
            }
            currentObject = GmlToken.NODE;

        } else if (qName.equalsIgnoreCase(GmlToken.EDGE.getXmlTag())) {
            // Edge:
            // Fields:
            //   Requires: source, target.
            //   Optional: id,
            //   Not supported: directed, sourceport, targetport, edge.extra.attribute
            // Elements:
            //   data [0,unbounded]
            //   Not supported: desc, graph

            String source = attributes.getValue(GmlToken.SOURCE.getXmlTag());
            String target = attributes.getValue(GmlToken.TARGET.getXmlTag());
            String id = attributes.getValue(GmlToken.ID.getXmlTag());

            currentData = new HashMap<>();
            if (source == null) {
                throwMissingField(GmlToken.EDGE, GmlToken.SOURCE);
            } else if (target == null) {
                throwMissingField(GmlToken.EDGE, GmlToken.TARGET);
            } else {
                currentSource = source;
                currentTarget = target;
            }
            if (id == null) {
                currentId = "edge";
            } else {
                currentId = id;
            }
            currentObject = GmlToken.EDGE;
        }
        else if (qName.equalsIgnoreCase(GmlToken.DATA.getXmlTag())) {
            String keyID = attributes.getValue(GmlToken.KEY.getXmlTag());
            if (keyID == null) {
                throwMissingField(GmlToken.DATA, GmlToken.KEY);
            } else {
                // TODO: Check if key is valid
                currentKey = null;
                for (Key key : declaredKeys) {
                    if (key.id.equals(keyID))
                        currentKey = key;
                }
                if (currentKey == null) {
                    String message = "Found data element with invalid key.";
                    logger.error(message);
                    throw  new SAXException(message);
                }
            }
            if (!currentObject.isKeyFor()) {
                // Not in object capable of holding data
                String message = "Found data element in an invalid location.";
                logger.error(message);
                throw new SAXException(message);
            }
        }
        // Unsupported tags
        else if (qName.equalsIgnoreCase(GmlToken.PORT.getXmlTag())) {
            if (!foundPort) {
                logger.warn("GraphML-Ports are not supported yet, ignoring element.");
                currentObject = GmlToken.PORT;
                foundPort = true;
            }
        } else if (qName.equalsIgnoreCase(GmlToken.HYPEREDGE.getXmlTag())
                || qName.equalsIgnoreCase(GmlToken.ENDPOINT.getXmlTag())) {
            if (!foundHyperEdge) {
                logger.warn("GraphML-Hyperedges are not supported yet, ignoring element.");
                foundHyperEdge = true;
                 currentObject = (qName.equalsIgnoreCase(GmlToken.HYPEREDGE.getXmlTag())) ?
                      GmlToken.HYPEREDGE : GmlToken.ENDPOINT;
            }
        } else if (qName.equalsIgnoreCase(GmlToken.LOCATOR.getXmlTag())) {
            if (!foundLocator) {
                logger.warn("GraphML-Locators are not supported yet, ignoring element.");
                foundLocator = true;
                currentObject = GmlToken.LOCATOR;
            }
        }
        else { // Unknown tags
            if (currentKey != null) {
                // -> Below <data> tag -> probably extra data type, which is not supported.
                if (!foundExtraData) {
                    logger.warn("GraphML data extension found. Data extensions are not supported. Skipping data..");
                    foundExtraData = true;
                }
                currentValue = "";
            } else {
                String message = "Found invalid tag: " + qName;
                logger.error(message);
                throw new SAXException(message);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (qName.equalsIgnoreCase(GmlToken.GRAPH.getXmlTag())) {
            currentGraph  = parentGraphs.empty()   ? null : parentGraphs.pop();
            currentId     = parentNodes.empty()    ? null :  parentNodes.pop();
            currentData   = nodeBackupData.empty() ? null : nodeBackupData.pop();
            currentObject = parentGraphs.empty()   ? GmlToken.GRAPHML : GmlToken.NODE;

        } else if (qName.equalsIgnoreCase(GmlToken.DATA.getXmlTag())) {
            if (currentObject.equals(GmlToken.GRAPH))
                currentGraph.addData(currentKey.name, currentValue);
            else if (currentObject == GmlToken.GRAPHML)
                graphMLData.put(currentKey, currentValue);
            else
                currentData.put(currentKey, currentValue);
            currentKey = null;
            currentValue = null;

        } else if (qName.equalsIgnoreCase(GmlToken.NODE.getXmlTag())) {
            if (blackList.contains(currentId)) {
                return;
            }
            IVertexBuilder vertexBuilder = currentGraph.getVertexBuilder(currentId);
            for (Map.Entry<Key, String> entry : currentData.entrySet())
                vertexBuilder.addData(entry.getKey().name, entry.getValue());
            currentData = null;
            currentId = null;
            currentObject = GmlToken.GRAPH;

        } else if (qName.equalsIgnoreCase(GmlToken.EDGE.getXmlTag())) {
            if (blackList.contains(currentSource) || blackList.contains(currentTarget)) {
                return;
            }
            IEdgeBuilder edgeBuilder = currentGraph.getEdgeBuilder(currentSource, currentTarget);
            edgeBuilder.newEdge(currentSource, currentTarget);
            edgeBuilder.setID(currentId);
            for (Map.Entry<Key, String> entry : currentData.entrySet())
                edgeBuilder.addData(entry.getKey().name, entry.getValue());
            currentId = null;
            currentData = null;
            currentSource = null;
            currentTarget = null;
            currentObject = GmlToken.GRAPH;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        currentValue = String.copyValueOf(ch, start, length).trim();
    }

    private void throwMissingField(GmlToken element, GmlToken field) throws SAXException {
        SAXException exception = new SAXException("Found GraphML-Element \"" + element.getXmlTag() + "\" "
                                                + "without required field \"" + field.getXmlTag() + "\".");
        logger.error(exception.getMessage());
        throw  exception;
    }

    private void throwInvalidField(GmlToken element, GmlToken field, String value) throws SAXException {
        SAXException exception = new SAXException("Found GraphML-Element \"" + element.getXmlTag() + "\" "
                                                + "with invalid value \"" + value
                                                + "\" in field \"" + field.getXmlTag() + "\".");
        logger.error(exception.getMessage());
        throw  exception;
    }


    private class Key {
        private final String id;
        private final String name;
        private final GmlToken forToken;
        private final GmlToken typeToken;

        private Key(String id, String name, GmlToken forToken, GmlToken typeToken) {
            this.id = id;
            this.name = (name == null) ? id : name;
            this.forToken = forToken;
            this.typeToken = typeToken;
        }
    }
}
