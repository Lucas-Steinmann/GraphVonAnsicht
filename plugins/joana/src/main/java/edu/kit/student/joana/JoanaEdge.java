package edu.kit.student.joana;

import java.util.List;

import edu.kit.student.graphmodel.directed.DefaultDirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A Joana specific {@link Edge}. It contains parameters which are only
 * used/usefull in {@link JoanaGraph}.
 */
public class JoanaEdge<V extends JoanaVertex> extends DefaultDirectedEdge<V> {
    
    private GAnsProperty<Kind> edgeKind;

    public JoanaEdge(String name, String label, Kind kind) {
        super(name, label);
        edgeKind = new GAnsProperty<Kind>("edgeKind", kind);
    }

    public JoanaEdge(V source, V target, Kind edgeKind) {
        super(edgeKind.toString(), edgeKind.toString(), source, target);
        this.edgeKind = new GAnsProperty<Kind>("edgeKind", edgeKind);
    }

    public void setProperties(Kind edgeKind) {
        this.edgeKind.setValue(edgeKind);
    }

    /**
     * Returns the edgeKind of the JoanaEdge.
     * 
     * @return The edgeKind of the JoanaEdge.
     */
    public Kind getEdgeKind() {
        return edgeKind.getValue();
    }
    
    @Override
    public List<GAnsProperty<?>> getProperties() {
    	List<GAnsProperty<?>> properties = super.getProperties();
    	properties.add(edgeKind);
    	return properties;
    }
    
    // TODO: Add missing.
    public enum Kind {
    	CD, CE, CF, CL, DD, DH, HE, PI, PO, PS, RF, SU, NF, JF, UN, CC, JD, NTSCD,
    	SD, DA, DL, VD, RD, SH, SF, ID, IW, RY, FORK, FORK_IN, FORK_OUT, JOIN, JOIN_OUT,
    	CONFLICT_DATA, CONFLICT_ORDER, FD, FI
    }
}
