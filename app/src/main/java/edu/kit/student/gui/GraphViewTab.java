package edu.kit.student.gui;

import javafx.scene.control.Tab;

public class GraphViewTab extends Tab {

	private GraphViewPanes panes;
	
	public GraphViewTab(GraphViewPanes panes) {
		this.panes = panes;
		this.setContent(this.panes.getScrollPane());
	}
	
	public GraphViewPanes getGraphViewPanes() {
		return panes;
	}
}
