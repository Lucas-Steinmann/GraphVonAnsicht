package edu.kit.student.plugin;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractPluginBase implements Plugin {

    @Override
    public void load() { }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
        return new LinkedList<>();
    }

    @Override
    public List<VertexFilter> getVertexFilter() {
        return new LinkedList<>();
    }

    @Override
    public List<EdgeFilter> getEdgeFilter() {
        return new LinkedList<>();
    }

    @Override
    public List<Exporter> getExporter() {
        return new LinkedList<>();
    }

    @Override
    public List<Importer> getImporter() {
        return new LinkedList<>();
    }

}
