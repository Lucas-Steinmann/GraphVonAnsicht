package edu.kit.student.plugin;

import java.util.List;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;

public abstract class AbstractPluginBase implements Plugin {

    @Override
    public void load() { }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
        return null;
    }

    @Override
    public List<VertexFilter<? extends Vertex>> getVertexFilter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<EdgeFilter<? extends Edge<? extends Vertex>, ? extends Vertex>> getEdgeFilter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Exporter> getExporter() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<Importer> getImporter() {
        // TODO Auto-generated method stub
        return null;
    }

}
