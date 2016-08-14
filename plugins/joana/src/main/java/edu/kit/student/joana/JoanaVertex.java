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
    
    /**
     * Constructs a new JoanaVertex, giving it a name,
     * label and setting what kind of joana vertex it is.
     * @param name  the name
     * @param label the label
     * @param kind  the kind (type)
     */
    public JoanaVertex(String name, String label, VertexKind kind) {
        super(name, label);
        
        nodeKind = new GAnsProperty<VertexKind>("nodeKind", kind);
        nodeSource = new GAnsProperty<String>("nodeSource", "");
        nodeProc = new GAnsProperty<Integer>("nodeProc", 0);
        nodeOperation = new GAnsProperty<String>("nodeOperation", "");
        nodeBcName = new GAnsProperty<String>("nodeBcName", "");
        nodeBcIndex = new GAnsProperty<Integer>("nodeBCIndex", 0);
        nodeSr = new GAnsProperty<Integer>("nodeSr", 0);
        nodeSc = new GAnsProperty<Integer>("nodeSc", 0);
        nodeEr = new GAnsProperty<Integer>("nodeEr", 0);
        nodeEc = new GAnsProperty<Integer>("nodeEc", 0);
        
        this.sizeSet = false;
    }

    /**
     * Sets properties of this joana vertex.
     * @param nodeKind the kind
     * @param nodeSource the source where it is described
     * @param nodeProc
     * @param nodeOperation
     * @param nodeBcName
     * @param nodeBcIndex
     * @param nodeSr
     * @param nodeSc
     * @param nodeEr
     * @param nodeEc
     */
    public void setProperties(VertexKind nodeKind, String nodeSource, 
            Integer nodeProc, String nodeOperation, String nodeBcName,
            Integer nodeBcIndex, Integer nodeSr, Integer nodeSc, 
            Integer nodeEr, Integer nodeEc) {
	    //TODO: Replace setProperties with constructor, as all arguments should not change over time
        this.nodeKind.setValue(nodeKind);
        this.nodeSource.setValue(nodeSource);
        this.nodeProc.setValue(nodeProc);
        this.nodeOperation.setValue(nodeOperation);
        this.nodeBcName.setValue(nodeBcName);
        this.nodeBcIndex.setValue(nodeBcIndex);
        this.nodeSr.setValue(nodeSr);
        this.nodeSc.setValue(nodeSc);
        this.nodeEr.setValue(nodeEr);
        this.nodeEc.setValue(nodeEc);
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
