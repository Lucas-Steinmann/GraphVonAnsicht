package edu.kit.student.graphml;


import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.WorkspaceOption;

import java.util.List;

/**
 * This is the entry point for the {@link GraphmlImporter}-plugin.
 * It provides the Importer to the plugin manager
 * 
 */
public class GmlImporterPlugin implements Plugin {
    private String name;

    /**
     * Getter of the name.
     * @return the name of this plugin
     */
    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void load() {
        
    }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
        // TODO Auto-generated method stub
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