package edu.kit.student.joana;

import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A Joana specific {@link Edge}. It contains parameters which are only
 * used/usefull in {@link JoanaGraph}.
 */
public class JoanaEdge<V extends JoanaVertex> extends DirectedEdge<V> {
	
	private GAnsProperty<KIND> edgeKind;

	public JoanaEdge(String name, String label, KIND kind) {
        super(name, label);
        edgeKind = new GAnsProperty<KIND>("edgeKind", kind);
    }
	
	public void setProperties(KIND edgeKind) {
		this.edgeKind.setValue(edgeKind);
	}

	/**
	 * Returns the edgeKind of the JoanaEdge.
	 * 
	 * @return The edgeKind of the JoanaEdge.
	 */
	public KIND getEdgeKind() {
		return edgeKind.getValue();
	}
	
	// TODO: Add missing.
	public enum KIND {
	    CF, CE, CD, CL, DD, PS, PO, PI, HE, RF, SU
	}
}
