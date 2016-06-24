package edu.kit.student.plugin;

import java.util.List;
import java.util.ServiceLoader;

/**
 * The plugin manager manages the access of the main application to the plugins.
 * It loads all plugins at the start of the runtime.
 * When the client needs some service implemented by plugins, it can get a list
 * of all available options.
 */
public class PluginManager {

	private List<WorkspaceOption> workspaceOptions;
	private List<Importer> importers;
	private List<Exporter> exporters;
    private ServiceLoader<Plugin> loader;	
    private List<LayoutAlgorithm> layoutAlgorithms;

	private static PluginManager mgr;

	/**
	 * constructs an instance of a plugin manager
	 */
	private PluginManager() {}
	
	/**
	 * Returns the singleton instance of the plugin manager.
	 * @return the plugin manager
	 */
	public static PluginManager getPluginManager() {
		if (mgr == null) {
			mgr = new PluginManager();
		}
		return mgr;
	}

	/**
	 * Returns all {@link WorkspaceOption}s provided by plugins.
	 * @return a list of all workspace options
	 */
	public List<WorkspaceOption> getWorkspaceOptions() { return null; } 
	
	/**
	 * Returns all vertex filter provided by plugins.
	 * @return a list of all vertex filter
	 */
	public List<VertexFilter> getVertexFilter() { return null; }
	
	/**
	 * Returns a list of all edge filter provided by plugins.
	 * @return a list of all edge filter
	 */
	public List<EdgeFilter> getEdgeFilter() { return null; }
	
	/**
	 * Returns a list of all plugins loaded by the ServiceLoader.
	 * @return all loaded plugins
	 */
	public List<Plugin> getPlugins() { return null; }
	
	/**
	 * Returns the {@link Exporter} provided by all plugins. 
	 * @return a list of provided exporter
	 */
	public List<Exporter> getExporter() { return null; }
	
	/**
	 * Returns the {@link Importer} provided by all plugins. 
	 * @return a list of provided importer
	 */
	public List<Importer> getImporter() { return null; }	
}
