package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ServiceLoader;

/**
 * The plugin manager manages the access of the main application to the plugins.
 * It loads all plugins at the start of the runtime.
 * When the client needs some service implemented by plugins, it can get a list
 * of all available options.
 */
public class PluginManager {

    private ServiceLoader<Plugin> loader;
    private List<Plugin> plugins;

    private static PluginManager mgr;

    /** Constructs an instance of a plugin manager.
     */
    private PluginManager() {
        loader = ServiceLoader.load(Plugin.class);
        plugins = new LinkedList<>();

        // Saves all plugins in a list.
        for ( Iterator<Plugin> it = loader.iterator(); it.hasNext(); ) {
            plugins.add(it.next());
        }
        for (Plugin plugin : plugins) {
            plugin.load();
        }
    }
    
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
    public List<WorkspaceOption> getWorkspaceOptions() { 
        LinkedList<WorkspaceOption> result = new LinkedList<>();
        plugins.forEach((plugin) -> result.addAll(plugin.getWorkspaceOptions()));
        return result;
    } 
    
    /**
     * Returns all vertex filter provided by plugins.
     * @return a list of all vertex filter
     */
    public List<VertexFilter<? extends Vertex>> getVertexFilter() { 
        LinkedList<VertexFilter<? extends Vertex>> result = new LinkedList<>();
        plugins.forEach((plugin) -> result.addAll(plugin.getVertexFilter()));
        return result;
    }
    
    /**
     * Returns a list of all edge filter provided by plugins.
     * @return a list of all edge filter
     */
    public List<EdgeFilter<? extends Edge>> getEdgeFilter() {
        LinkedList<EdgeFilter<? extends Edge>> result =
                new LinkedList<>();
        plugins.forEach((plugin) -> result.addAll(plugin.getEdgeFilter()));
        return result;
    }
    
    /**
     * Returns a list of all plugins loaded by the ServiceLoader.
     * @return all loaded plugins
     */
    public List<Plugin> getPlugins() {
        return new LinkedList<>(plugins);
    }
    
    /**
     * Returns the {@link Exporter} provided by all plugins. 
     * @return a list of provided exporter
     */
    public List<Exporter> getExporter() { 
        LinkedList<Exporter> result = new LinkedList<>();
        plugins.forEach((plugin) -> result.addAll(plugin.getExporter()));
        return result;
    }
    
    /**
     * Returns the {@link Importer} provided by all plugins. 
     * @return a list of provided importer
     */
    public List<Importer> getImporter() { 
        LinkedList<Importer> result = new LinkedList<>();
        plugins.forEach((plugin) -> result.addAll(plugin.getImporter()));
        return result;
    }
}
