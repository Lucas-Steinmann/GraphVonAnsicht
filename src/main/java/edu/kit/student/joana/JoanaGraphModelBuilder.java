package edu.kit.student.joana;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.IGraphBuilder;
import edu.kit.student.graphmodel.IGraphModelBuilder;

/**
 * The JoanaGraphModelBuilder implements the {@link IGraphModelBuilder} and
 * creates a {@link JoanaGraphModel}.
 */
public class JoanaGraphModelBuilder implements IGraphModelBuilder {
	
	private JoanaWorkspace workspace;
	
	public JoanaGraphModelBuilder(JoanaWorkspace workspace) {
		this.workspace = workspace;
	}

	@Override
	public IGraphBuilder getGraphBuilder(String graphID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public GraphModel build() {
		//TODO: vor dem Return die spezielle JoanaGraphModel-Instanz im workspace.setGraphModel(JoanaGraphModel model) setzen.
		return null;
	}

}
