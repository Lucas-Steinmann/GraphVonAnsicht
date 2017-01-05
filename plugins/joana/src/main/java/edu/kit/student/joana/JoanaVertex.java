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
    private GAnsProperty<String> nodeSource;
    private GAnsProperty<Integer> nodeProc;
    private GAnsProperty<String> nodeOperation;
    private GAnsProperty<String> nodeBcName;
    private GAnsProperty<Integer> nodeBcIndex;
    private GAnsProperty<Integer> nodeSr;
    private GAnsProperty<Integer> nodeSc;
    private GAnsProperty<Integer> nodeEr;
    private GAnsProperty<Integer> nodeEc;
    
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
        this(name, label, kind, "", 0, "", "", 0, 0, 0, 0, 0);
    }

    public JoanaVertex(String name, String label, VertexKind nodeKind, String nodeSource, 
            int nodeProc, String nodeOperation, String nodeBcName, int nodeBcIndex, int nodeSr, 
            int nodeSc, int nodeEr, int nodeEc) {

        super(name, label);

        this.nodeKind = new GAnsProperty<VertexKind>("nodeKind", nodeKind);
        this.nodeSource = new GAnsProperty<String>("nodeSource", nodeSource);
        this.nodeProc = new GAnsProperty<Integer>("nodeProc", nodeProc);
        this.nodeOperation = new GAnsProperty<String>("nodeOperation", nodeOperation);
        this.nodeBcName = new GAnsProperty<String>("nodeBcName", nodeBcName);
        this.nodeBcIndex = new GAnsProperty<Integer>("nodeBCIndex", nodeBcIndex);
        this.nodeSr = new GAnsProperty<Integer>("nodeSr", nodeSr);
        this.nodeSc = new GAnsProperty<Integer>("nodeSc", nodeSc);
        this.nodeEr = new GAnsProperty<Integer>("nodeEr", nodeEr);
        this.nodeEc = new GAnsProperty<Integer>("nodeEc", nodeEc);
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
    public String getNodeSource() {
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
    public String getNodeOperation() {
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
        NORM, CALL, EXIT, ENTR,
        ACTI, ACTO, FRMO, FRMI,
        EXPR, PRED, SYNC, FOLD,
        SUMMARY, FIELDACCESS, UNKNOWN;
    	
    	@Override
    	public String toString() {
    		return this.name();
    	}
        
        public Color color() {
        	switch(this) {
        	case NORM: return Color.web("0xFFC125");
        	case CALL: return Color.web("0xBFEFFF");
        	case EXIT: return Color.web("0xFFC125");
        	case ENTR: return Color.web("0xCBCBCB");
        	case ACTI: return Color.web("0x87CEFA");
        	case ACTO: return Color.web("0x00BFFF");
        	case FRMO: return Color.web("0x00EE00");
        	case FRMI: return Color.web("0x98FB98");
        	case EXPR: return Color.web("0xFFFF00");
        	case PRED: return Color.web("0xFFC125");
        	case SYNC: return Color.web("0xFFC125");
        	case FOLD: return Color.BROWN;
        	case SUMMARY: return Color.BROWN;
        	case FIELDACCESS: return Color.LIGHTGREEN;
        	default: return Color.BEIGE;
        	}
        }
        
        public VertexPriority priority() {
        	switch(this) {
        	case FIELDACCESS: return VertexPriority.LOW;
        	default: return VertexPriority.HIGH;
        	}
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
