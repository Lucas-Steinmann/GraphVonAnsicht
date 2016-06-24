package edu.kit.student.joana;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.IGraphModelBuilder;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.Workspace;

/**
 * The {@link JoanaWorkspace} is the workspace for Joana graphs. It is used to
 * define parameters, provides an {@link IGraphModelBuilder} and contains a
 * {@link JoanaGraphModel}.
 */
public class JoanaWorkspace implements Workspace {

	@Override
	public void initialize() {
	}

	@Override
	public IGraphModelBuilder getGraphModelBuilder() {
		return null;
	}

	@Override
	public GraphModel getGraphModel() {
		return null;
	}

	@Override
	public Settings getSettings() {
		return null;
	}
}
