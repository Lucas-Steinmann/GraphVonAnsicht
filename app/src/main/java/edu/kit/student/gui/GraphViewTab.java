package edu.kit.student.gui;

import javafx.scene.control.Tab;

public class GraphViewTab extends Tab {

	private GraphViewPaneStack panes;
	
	public GraphViewTab(GraphViewPaneStack panes) {
		this.panes = panes;
		this.setContent(this.panes.getRoot());
	}
	
	public GraphViewPaneStack getGraphViewPanes() {
		return panes;
	}
}
