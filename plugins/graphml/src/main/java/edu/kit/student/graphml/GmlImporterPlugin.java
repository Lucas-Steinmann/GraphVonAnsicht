package edu.kit.student.graphml;


import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.WorkspaceOption;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the entry point for the {@link GraphmlImporter}-plugin.
 * It provides the Importer to the plugin manager
 * 
 */
public class GmlImporterPlugin implements Plugin {

    private static final String pluginName = "GraphML";

    /**
     * Getter of the name.
     * @return the name of this plugin
     */
    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public void load() { }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
        return new LinkedList<>();
    }

    @Override
    public List<VertexFilter<? extends Vertex>> getVertexFilter() {
        return new LinkedList<>();
    }

    @Override
    public List<EdgeFilter<? extends Edge<? extends Vertex>, ? extends Vertex>> getEdgeFilter() {
        return new LinkedList<>();
    }

    @Override
    public List<Exporter> getExporter() {
        return new LinkedList<>();
    }

    @Override
    public List<Importer> getImporter() {
        LinkedList<Importer> result = new LinkedList<>();
        result.add(new GraphmlImporter());
        return result;
    }
}