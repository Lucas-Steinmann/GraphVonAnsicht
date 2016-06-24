package edu.kit.student.joana;

import edu.kit.student.graphmodel.DirectedEdge;
import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A Joana specific {@link Edge}. It contains parameters which are only
 * used/usefull in {@link JoanaGraph}.
 */
public class JoanaEdge extends DirectedEdge<JoanaVertex> {

	private GAnsProperty<String> edgeKind;

	/**
	 * Returns the edgeKind of the JoanaEdge.
	 * 
	 * @return The edgeKind of the JoanaEdge.
	 */
	public String getEdgeKind() {
		return edgeKind.getValue();
	}

}
