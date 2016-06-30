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
	
	public JoanaVertex(String name, String label, Integer id) {
		super(name, label, id);
		
		nodeKind = new GAnsProperty<String>("nodeKind", "");
		nodeSource = new GAnsProperty<String>("nodeSource", "");
		nodeProc = new GAnsProperty<Integer>("nodeProc", 0);
		nodeOperation = new GAnsProperty<String>("nodeOperation", "");
		nodeBcName = new GAnsProperty<String>("nodeBcName", "");
		nodeBCIndex = new GAnsProperty<Integer>("nodeBCIndex", 0);
		nodeSr = new GAnsProperty<Integer>("nodeSr", 0);
		nodeSc = new GAnsProperty<Integer>("nodeSc", 0);
		nodeEr = new GAnsProperty<Integer>("nodeEr", 0);
		nodeEc = new GAnsProperty<Integer>("nodeEc", 0);
	}

	public void setProperties(String nodeKind, String nodeSource, Integer nodeProc, String nodeOperation,
			String nodeBcName, Integer nodeBCIndex, Integer nodeSr, Integer nodeSc, Integer nodeEr, Integer nodeEc) {
		this.nodeKind.setValue(nodeKind);
		this.nodeSource.setValue(nodeSource);
		this.nodeProc.setValue(nodeProc);
		this.nodeOperation.setValue(nodeOperation);
		this.nodeBcName.setValue(nodeBcName);
		this.nodeBCIndex.setValue(nodeBCIndex);
		this.nodeSr.setValue(nodeSr);
		this.nodeSc.setValue(nodeSc);
		this.nodeEr.setValue(nodeEr);
		this.nodeEc.setValue(nodeEc);
	}

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
