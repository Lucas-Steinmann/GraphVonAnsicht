package edu.kit.student.graphml;

/**
 * Lists valid GraphML Tokens and allows queries about properties/classes of individual tokens.
 *
 * @author  Lucas Steinmann
 */
public enum GmlToken {
    GRAPHML("graphml"),
    KEY("key"), ATTRNAME("attr.name"), ATTRTYPE("attr.type"), DEFAULT("default"), DYNAMIC("dynamic"), FOR("for"),
    GRAPH("graph"), EDGEDEFAULT("edgedefault"), DIRECTED("directed"), UNDIRECTED("undirected"),
    NODE("node"), EDGE("edge"), LOCATOR("locator"), HYPEREDGE("hyperedge"), ENDPOINT("endpoint"), PORT("port"), ALL("all"),
    ID("id"),
    SOURCE("source"), TARGET("target"),
    DATA("data"), TYPE("type"),
    BOOLEAN("boolean"), INT("int"), LONG("long"), FLOAT("float"), DOUBLE("double"), STRING("string");

    private final String xmlTag;

    GmlToken(final String s) {
        xmlTag = s;
    }

    /**
     * Returns the XML-tag of the GmlToken
     * @return the XML-tag
     */
    public String getXmlTag() {
        return xmlTag;
    }

    /**
     * Returns true if the {@link GmlToken} is a valid entry in the key.type field.
     * @return true if the {@link GmlToken} is a key.type.
     */
    public boolean isKeyType() {
        switch (this) {
            case BOOLEAN:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case STRING:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns true if the {@link GmlToken} is a valid entry in the key.for field.
     * @return true if the {@link GmlToken} is a key.for type.
     */
    public boolean isKeyFor() {
        switch (this) {
            case GRAPH:
            case NODE:
            case EDGE:
            case LOCATOR:
            case HYPEREDGE:
            case ENDPOINT:
            case GRAPHML:
            case PORT:
            case ALL:
                return true;
            default:
                return false;
        }
    }

    /**
     * Returns true if the GmlToken is supported.
     *
     * @return true if the GmlToken is supported
     */
    public boolean isSupported() {
        switch (this) {
            case HYPEREDGE:
            case LOCATOR:
            case ENDPOINT:
            case PORT:
                return false;
            default:
                return true;
        }
    }
}
