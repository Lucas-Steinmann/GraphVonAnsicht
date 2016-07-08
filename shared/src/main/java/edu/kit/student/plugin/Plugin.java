package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

import java.util.List;

/**
 * This is the main entry point for plugins.
 * Plugins have to register their content extensions via this interface.
 * The plugin manager will load classes implementing this interface  with a service loader, when
 * they are correctly described in the plugins META-INF.
 */
public interface Plugin {
    
    /**
     * Returns the name of the plugin.
     * Uniqueness can't be assumed. 
     * @return the name of the plugin
     */
    public String getName();
    
    /**
     * Called after all plugins have been constructed.
     * "Inter-Plugin" communication, like registering of layouts for 
     * graphs in other plugins should be executed in here.
     */
    public void load();
    
    /**
     * Returns all provided by the plugin {@link WorkspaceOption}.
     * If none are provided returns {@code null} or an empty list.
     * @return The list of provided workspace options
     */
    public List<WorkspaceOption> getWorkspaceOptions();
    
    /**
     * Returns all by the plugin provided {@link VertexFilter}.
     * If none are provided returns {@code null} or an empty list.
     * @return the list of provided vertex filter
     */
    public List<VertexFilter<? extends Vertex>> getVertexFilter();
    

    /**
     * Returns all by the plugin provided {@link EdgeFilter}.
     * If none are provided returns {@code null} or an empty list.
     * @return the list of provided edge filter
     */
    public List<EdgeFilter<? extends Edge, ? extends Vertex>> getEdgeFilter();
    
    /**
     * Returns all by the plugin provided {@link Exporter}.
     * If none are provided returns {@code null} or an empty list.
     * @return a list of provided exporter
     */
    public List<Exporter> getExporter();
    
    /**
     * Returns all by the plugin provided {@link Importer}.
     * If none are provided returns {@code null} or an empty list.
     * @return a list of provided importer
     */
    public List<Importer> getImporter();
}
