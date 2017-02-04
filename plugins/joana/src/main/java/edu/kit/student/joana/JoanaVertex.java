package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.util.DoublePoint;
import javafx.scene.paint.Color;

/**
 * A Joana specific Vertex. It contains parameters which are only used/useful
 * for Joana.
 */
public class JoanaVertex extends DefaultVertex implements ViewableVertex {

    private GAnsProperty<VertexKind> nodeKind;
    private GAnsProperty<JavaSource> nodeSource;
    private GAnsProperty<Integer> nodeProc;
    private GAnsProperty<Operation> nodeOperation;
    private GAnsProperty<String> nodeBcName;
    private GAnsProperty<Integer> nodeBcIndex;
    private GAnsProperty<Integer> nodeSr;
    private GAnsProperty<Integer> nodeSc;
    private GAnsProperty<Integer> nodeEr;
    private GAnsProperty<Integer> nodeEc;
    private GAnsProperty<String>  nodeLocalDef;
    private GAnsProperty<String>  nodeLocalUse;

    private DoublePoint setSize;
    private boolean sizeSet;
    private String nodeLabel;
    
    /**
     * Constructs a new JoanaVertex, giving it a name,
     * label and setting what kind of joana vertex it is.
     * @param name  the name
     * @param label the label
     * @param kind  the kind (type)
     */
    public JoanaVertex(String name, String label, VertexKind kind) {
        this(name, label, kind,null, 0, Operation.EMPTY, "", 0, 0, 0, 0, 0, "", "");
    }

    public JoanaVertex(String name, String label, VertexKind nodeKind, JavaSource source,
            int nodeProc, Operation nodeOperation, String nodeBcName, int nodeBcIndex, int nodeSr,
            int nodeSc, int nodeEr, int nodeEc,
            String nodeLocalDef, String nodeLocalUse) {

        super(name, label);

        this.nodeKind = new GAnsProperty<>("nodeKind", nodeKind);
        this.nodeSource = new GAnsProperty<>("nodeSource", source);
        this.nodeProc = new GAnsProperty<>("nodeProc", nodeProc);
        this.nodeOperation = new GAnsProperty<>("nodeOperation", nodeOperation);
        this.nodeBcName = new GAnsProperty<>("nodeBcName", nodeBcName);
        this.nodeBcIndex = new GAnsProperty<>("nodeBCIndex", nodeBcIndex);
        this.nodeSr = new GAnsProperty<>("nodeSr", nodeSr);
        this.nodeSc = new GAnsProperty<>("nodeSc", nodeSc);
        this.nodeEr = new GAnsProperty<>("nodeEr", nodeEr);
        this.nodeEc = new GAnsProperty<>("nodeEc", nodeEc);
        this.nodeLocalDef = new GAnsProperty<>("nodeLocalDef", nodeLocalDef);
        this.nodeLocalUse = new GAnsProperty<>("nodeLocalUse", nodeLocalUse);
        this.sizeSet = false;
        this.nodeLabel = label;
    }


    @Override
	public List<GAnsProperty<?>> getProperties() {
    	List<GAnsProperty<?>> properties = super.getProperties();
		properties.add(nodeKind);
		properties.add(nodeSource);
		properties.add(nodeProc);
		properties.add(nodeOperation);
		properties.add(nodeBcName);
		properties.add(nodeBcIndex);
		properties.add(nodeSr);
		properties.add(nodeSc);
		properties.add(nodeEr);
		properties.add(nodeEc);
		if (nodeLocalDef.getValue() != null)
            properties.add(nodeLocalDef);
        if (nodeLocalUse.getValue() != null)
            properties.add(nodeLocalUse);
		return properties;
	}

    /**
     * Returns the nodeKind of the JoanaVertex.
     * 
     * @return The nodeKind of the JoanaVertex.
     */
    public VertexKind getNodeKind() {
        return nodeKind.getValue();
    }

    /**
     * Returns the nodeSource of the JoanaVertex.
     * 
     * @return The nodeSource of the JoanaVertex.
     */
    public JavaSource getNodeSource() {
        return nodeSource.getValue();
    }

    /**
     * Returns the nodeProc of the JoanaVertex.
     * 
     * @return The nodeProc of the JoanaVertex.
     */
    public Integer getNodeProc() {
        return nodeProc.getValue();
    }

    /**
     * Returns the nodeOperation of the JoanaVertex.
     * 
     * @return The nodeOperation of the JoanaVertex.
     */
    public Operation getNodeOperation() {
        return nodeOperation.getValue();
    }

    /**
     * Returns the nodeBcName of the JoanaVertex.
     * 
     * @return The nodeBcName of the JoanaVertex.
     */
    public String getNodeBcName() {
        return nodeBcName.getValue();
    }

    /**
     * Returns the nodeBCIndex of the JoanaVertex.
     * 
     * @return The nodeBCIndex of the JoanaVertex.
     */
    public Integer getNodeBcIndex() {
        return nodeBcIndex.getValue();
    }

    /**
     * Returns the nodeSr of the JoanaVertex.
     * 
     * @return The nodeSr of the JoanaVertex.
     */
    public Integer getNodeSr() {
        return nodeSr.getValue();
    }

    /**
     * Returns the nodeSc of the JoanaVertex.
     * 
     * @return The nodeSc of the JoanaVertex.
     */
    public Integer getNodeSc() {
        return nodeSc.getValue();
    }

    /**
     * Returns the nodeEr of the JoanaVertex.
     * 
     * @return The nodeEr of the JoanaVertex.
     */
    public Integer getNodeEr() {
        return nodeEr.getValue();
    }

    /**
     * Returns the nodeEc of the JoanaVertex.
     * 
     * @return The nodeEc of the JoanaVertex.
     */
    public Integer getNodeEc() {
        return nodeEc.getValue();
    }

    /**
     * Returns the nodeLocalDef of the JoanaVertex.
     *
     * @return The nodeLocalDef of the JoanaVertex.
     */
    public String getNodelLocalDef() {
        return nodeLocalDef.getValue();
    }

    /**
     * Returns the nodeLocalUse of the JoanaVertex.
     *
     * @return The nodeLocalUse of the JoanaVertex.
     */
    public String getNodeLocalUse() {
        return nodeLocalUse.getValue();
    }


    @Override
	public DoublePoint getSize() {
		// TODO: calculating size with different max/min values depending on KIND
		if(this.sizeSet){
			return this.setSize;
		} else{
			return super.getSize();
		}
	}
	
	@Override
	public String getLabel(){
		return this.getName() + "  " + this.getNodeKind().toString() + '\n' + this.nodeLabel;
	}

	@Override
	public Color getColor() {
		return nodeKind.getValue().color();
	}
    
    public enum VertexKind {
        NORM(Color.web("0xFFC125")), CALL(Color.web("0xBFEFFF")), EXIT(Color.web("0xFFC125")),
        ENTR(Color.web("0xCBCBCB")), ACTI(Color.web("0x87CEFA")), ACTO(Color.web("0x00BFFF")),
        FRMO(Color.web("0x00EE00")), FRMI(Color.web("0x98FB98")), EXPR(Color.web("0xFFFF00")),
        PRED(Color.web("0xFFC125")), SYNC(Color.web("0xFFC125")), FOLD(Color.BROWN),
        SUMMARY(Color.BROWN), FIELDACCESS(Color.LIGHTGREEN);

        public final Color backgroundColor;

        VertexKind(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

    	@Override
    	public String toString() {
    		return this.name();
    	}
        
        public Color color() {
            return this.backgroundColor;
        }
        
        public VertexPriority priority() {
        	switch(this) {
        	case FIELDACCESS: return VertexPriority.LOW;
        	default: return VertexPriority.HIGH;
        	}
        }
    }

    public enum Operation {

        EMPTY("empty", VertexKind.EXPR, VertexKind.FOLD), //
        INT_CONST("intconst", VertexKind.EXPR, VertexKind.PRED, VertexKind.ACTI),
        FLOAT_CONST("floatconst", VertexKind.EXPR),
        CHAR_CONST("charconst", VertexKind.EXPR),
        STRING_CONST("stringconst", VertexKind.EXPR),
        FUNCTION_CONST("functionconst", VertexKind.EXPR),
        SHORTCUT("shortcut", VertexKind.EXPR, VertexKind.PRED, VertexKind.ACTI), // && ||
        QUESTION("question", VertexKind.EXPR), // ? :
        BINARY("binary", VertexKind.EXPR, VertexKind.PRED),
        UNARY("unary", VertexKind.EXPR),
        //			//	     | "lookup"
        DEREFER("derefer", VertexKind.EXPR, VertexKind.PRED, VertexKind.ACTI),
        REFER("refer", VertexKind.EXPR, VertexKind.PRED, VertexKind.ACTI),
        ARRAY("array", VertexKind.EXPR),
        SELECT("select", VertexKind.EXPR),
        REFERENCE("reference", VertexKind.EXPR, VertexKind.PRED, VertexKind.ACTI),
        DECLARATION("declaration", VertexKind.NORM),
        MODIFY("modify", VertexKind.EXPR, VertexKind.PRED), // ++ --
        MODASSIGN("modassign", VertexKind.EXPR), // += etc.
        ASSIGN("assign", VertexKind.EXPR),
        IF("IF", VertexKind.PRED, VertexKind.NORM),
        LOOP("loop", VertexKind.NORM),
        JUMP("jump", VertexKind.NORM),
        COMPOUND("compound", VertexKind.NORM),
        CALL("call", VertexKind.CALL),
        ENTRY("entry", VertexKind.ENTR),
        EXIT("exit", VertexKind.EXIT),
        FORMAL_IN("form-in", VertexKind.FRMI),
        FORMAL_ELLIP("form-ellip", VertexKind.FRMI), // Ellipse-Parameter "..."
        FORMAL_OUT("form-out", VertexKind.FRMO),
        ACTI("act-in", VertexKind.ACTI),
        ACTUAL_OUT("act-out", VertexKind.ACTO),
        //		NONE("None"
        MONITOR("monitor", VertexKind.SYNC),
        SUMMARY("summary", VertexKind.SUMMARY);

        private final String value;
        private final VertexKind[] kind;

        Operation(String s, VertexKind k) { value = s; kind = new VertexKind[]{k}; }

        Operation(String s, VertexKind k1, VertexKind k2) {
            value = s;
            kind = new VertexKind[]{k1, k2};
        }

        Operation(String s, VertexKind k1, VertexKind k2, VertexKind k3) {
            value = s;
            kind = new VertexKind[]{k1, k2, k3};
        }

        public VertexKind getKind(int i) {
            return kind[i];
        }

        public String toString() {
            return value;
        }

        VertexKind[] getCorrespondingKind() {
            return kind;
        }

        public static Operation getOperationByValue(String value) {
            for (Operation op : values()) {
                if (op.value.equals(value)) {
                    return op;
                }
            }
            return null;
        }
    }

    @Override
    public int getLink() {
        return -1;
    }
    
    public void setSize(DoublePoint newSize){
    	this.setSize = newSize;
    	this.sizeSet = true;
    }

	@Override
	public VertexPriority getPriority() {
		return this.nodeKind.getValue().priority();
	}
    
}
