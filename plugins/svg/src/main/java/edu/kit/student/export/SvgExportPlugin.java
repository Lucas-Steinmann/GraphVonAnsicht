package edu.kit.student.export;


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
 * This class provides the {@link SvgExporter} to the plugin manager.
 */
public class SvgExportPlugin implements Plugin {

    private static final String pluginName = "SVG";

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
        LinkedList<Exporter> result = new LinkedList<>();
        result.add(new SvgExporter());
        return result;
    }

    @Override
    public List<Importer> getImporter() {
        return new LinkedList<>();
    }
}
