package edu.kit.student.sugiyama;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedGraphLayoutOption;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.WorkspaceOption;

/**
 * A plugin for GAns that supplies a layout algorithm based on the Sugiyama-framework.
 */
public class SugiyamaPlugin implements Plugin {

    private static final String pluginName = "Sugiyama";

    /**
     * Getter of the name.
     * @return the name of this plugin
     */
    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public void load() {
        DefaultDirectedGraph.getDirectedGraphLayoutRegister().addLayoutOption(new DirectedGraphLayoutOption() {
            
            SugiyamaLayoutAlgorithm algo;
            
            @Override
            public Settings getSettings() {

                return null;
            }
            
            @Override
            public void chooseLayout() {
                algo = new SugiyamaLayoutAlgorithm();
            }
            
            @Override
            public void applyLayout() {
                //TODO: Remove casting by changing some interface (probably LayoutAlgorithm.layout)
                this.algo.layout((DirectedGraph<Vertex, DirectedEdge<Vertex>>) graph);
            }
        });;
    }

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
        return new LinkedList<>();
    }
}
