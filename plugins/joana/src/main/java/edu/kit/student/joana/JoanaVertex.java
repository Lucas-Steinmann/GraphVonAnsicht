package edu.kit.student.joana;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A Joana specific Vertex. It contains parameters which are only used/useful
 * for Joana.
 */
public class JoanaVertex extends DefaultVertex {

    private GAnsProperty<Kind> nodeKind;
    private GAnsProperty<String> nodeSource;
    private GAnsProperty<Integer> nodeProc;
    private GAnsProperty<String> nodeOperation;
    private GAnsProperty<String> nodeBcName;
    private GAnsProperty<Integer> nodeBcIndex;
    private GAnsProperty<Integer> nodeSr;
    private GAnsProperty<Integer> nodeSc;
    private GAnsProperty<Integer> nodeEr;
    private GAnsProperty<Integer> nodeEc;
    
    /**
     * Constructs a new JoanaVertex, giving it a name,
     * label and setting what kind of joana vertex it is.
     * @param name  the name
     * @param label the label
     * @param kind  the kind (type)
     */
    public JoanaVertex(String name, String label, Kind kind) {
        super(name, label);
        
        nodeKind = new GAnsProperty<Kind>("nodeKind", kind);
        nodeSource = new GAnsProperty<String>("nodeSource", "");
        nodeProc = new GAnsProperty<Integer>("nodeProc", 0);
        nodeOperation = new GAnsProperty<String>("nodeOperation", "");
        nodeBcName = new GAnsProperty<String>("nodeBcName", "");
        nodeBcIndex = new GAnsProperty<Integer>("nodeBCIndex", 0);
        nodeSr = new GAnsProperty<Integer>("nodeSr", 0);
        nodeSc = new GAnsProperty<Integer>("nodeSc", 0);
        nodeEr = new GAnsProperty<Integer>("nodeEr", 0);
        nodeEc = new GAnsProperty<Integer>("nodeEc", 0);
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
    public void setProperties(Kind nodeKind, String nodeSource, 
            Integer nodeProc, String nodeOperation, String nodeBcName,
            Integer nodeBcIndex, Integer nodeSr, Integer nodeSc, 
            Integer nodeEr, Integer nodeEc) {
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
    public Kind getNodeKind() {
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
    
    public enum Kind {
        NORM, CALL, EXIT, ENTR,
        ACTI, ACTO, FRMO, FRMI,
        EXPR, PRED;
        
        @Override
        public String toString() {
        	switch(this) {
        	case NORM: return "NORM";
        	case CALL: return "CALL";
        	case EXIT: return "EXIT";
        	case ENTR: return "ENTR";
        	case ACTI: return "ACTI";
        	case ACTO: return "ACTO";
        	case FRMO: return "FRMO";
        	case FRMI: return "FRMI";
        	case EXPR: return "EXPR";
        	case PRED: return "PRED";
        	default: return "";
        	}
        }
    }
}
