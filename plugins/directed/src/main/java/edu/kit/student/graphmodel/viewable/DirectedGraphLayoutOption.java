package edu.kit.student.graphmodel.viewable;

import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.LayoutAlgorithm;
import edu.kit.student.plugin.LayoutOption;

/**
 * @author Lucas Steinmann
 *
 */
public abstract class DirectedGraphLayoutOption extends LayoutOption {

	private DirectedGraph graph;
	private LayoutAlgorithm<DirectedGraph> layout;


	@Override
	public void chooseLayout() {
		// TODO Auto-generated method stub
	}

	/**
	 * Sets the {@link DirectedGraph} that will be the target of the
	 * {@link DirectedGraphLayoutOption}.
	 * 
	 * @param graph
	 *            The {@link DirectedGraph} that will be the target of this
	 *            {@link DirectedGraphLayoutOption}.
	 */
	public void setGraph(DirectedGraph graph) {
	    this.graph = graph;
	}

	/**
	 * Sets the LayoutAlgorithm that will be used to layout the set graph.
	 * 
	 * @param layout the LayoutAlgorithm that will be used to layout the set graph.
	 */
	public void setLayout(LayoutAlgorithm<DirectedGraph> layout) {
		this.layout = layout;
	}

	@Override
	public void applyLayout() {
	    if (layout == null) {
	        return;
	    }
		layout.layout(graph);
	}

    @Override
    public Settings getSettings() {
        if (layout == null) {
            this.chooseLayout();
        }
        return layout.getSettings();
    }
}
