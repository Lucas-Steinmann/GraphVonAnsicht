package edu.kit.student.joana;

import java.util.HashMap;
import java.util.Map;

import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.parameter.Parameter;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.Workspace;

/**
 * The {@link JoanaWorkspace} is the workspace for Joana graphs. It is used to
 * define parameters, provides an {@link IGraphModelBuilder} and contains a
 * {@link JoanaGraphModel}.
 */
public class JoanaWorkspace implements Workspace {
	
	private Settings settings;
	private JoanaGraphModelBuilder builder;
	private JoanaGraphModel model;

	public JoanaWorkspace() {
		Map<String, Parameter<?,?>> parameters = new HashMap<String, Parameter<?,?>>();
		//TODO: Welche Parameter werden in den Settings ben�tigt?
		
		settings = new Settings(parameters);
		
		builder = new JoanaGraphModelBuilder(this);
	}
	
	@Override
	public void initialize() {
		//TODO: Setzen der werte aus den Parametern(falls ben�tigt)
	}

	@Override
	public IGraphModelBuilder getGraphModelBuilder() {
		return builder;
	}
	
	/**
	 * Sets the specialized GraphModel in the workspace.
	 * Should only be called from inside of the builder!
	 * 
	 * @param model The model that will be set.
	 */
	public void setGraphModel(JoanaGraphModel model) {
		this.model = model;
	}

	@Override
	public GraphModel getGraphModel() {
		return model;
	}

	@Override
	public Settings getSettings() {
		return settings;
	}
}
