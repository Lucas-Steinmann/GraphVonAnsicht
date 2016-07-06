package edu.kit.student.joana;

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
    
    // TODO: Add missing.
    public enum Kind {
        CF, CE, CD, CL, DD, PS, PO, PI, HE, RF, SU
    }
}
