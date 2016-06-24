package edu.kit.student.joana;

import edu.kit.student.graphmodel.DefaultVertex;
import edu.kit.student.objectproperty.GAnsProperty;

/**
 * A Joana specific Vertex. It contains parameters which are only used/useful
 * for Joana.
 */
public class JoanaVertex extends DefaultVertex {

	private GAnsProperty<String> nodeKind;
	private GAnsProperty<String> nodeSource;
	private GAnsProperty<Integer> nodeProc;
	private GAnsProperty<String> nodeOperation;
	private GAnsProperty<String> nodeBcName;
	private GAnsProperty<Integer> nodeBCIndex;
	private GAnsProperty<Integer> nodeSr;
	private GAnsProperty<Integer> nodeSc;
	private GAnsProperty<Integer> nodeEr;
	private GAnsProperty<Integer> nodeEc;

	/**
	 * Returns the nodeKind of the JoanaVertex.
	 * 
	 * @return The nodeKind of the JoanaVertex.
	 */
	public String getNodeKind() {
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
	public Integer getNodeBCIndex() {
		return nodeBCIndex.getValue();
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
}
