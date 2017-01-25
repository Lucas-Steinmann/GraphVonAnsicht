package edu.kit.student.sugiyama;


import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.viewable.GenericGraphPlugin;
import edu.kit.student.plugin.AbstractPluginBase;

/**
 * A plugin for GAns that supplies a layout algorithm based on the Sugiyama-framework.
 */
public class SugiyamaPlugin extends AbstractPluginBase {

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
        DefaultDirectedGraph.getDirectedGraphLayoutRegister().addLayoutOption(new SugiyamaLayoutOption());
        GenericGraphPlugin.directedGraphLayoutOptions.addLayoutOption(new SugiyamaLayoutOption());
    }
}
