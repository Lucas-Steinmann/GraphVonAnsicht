package edu.kit.student.plugin;


import edu.kit.student.graphmodel.GraphModel;
import edu.kit.student.graphmodel.builder.IGraphModelBuilder;
import edu.kit.student.parameter.Settings;

/**
 * A workspace contains a set of default actions and options for displaying a specific domain of graphs.
 * The workspace manages the graphs instantiation through providing an {@link IGraphModelBuilder}.
 * He also provides a list of layout options for graphs in his model.
 */
public interface Workspace {

	/**
	 * Initializes this workspace with the settings if they have been adjusted.
	 * If the settings have not been adjusted, default values will be used.
	 */
	public void initialize();

	/**
	 * Returns a builder to build a graph model in this workspace.
	 * 
	 * @return the builder
	 */
	public IGraphModelBuilder getGraphModelBuilder();

	/**
	 * Returns the {@link GraphModel} stored in the workspace.
	 * @return the graph model
	 */
	public GraphModel getGraphModel();
	
	/**
	 * Returns a set of parameters to initialize this workspace.
	 * When the settings have been adjusted, the client has
	 * to call {@code initialize()}. To initialize the workspace
	 * with the settings.
	 * @return the settings
	 */
	public abstract Settings getSettings();
}
