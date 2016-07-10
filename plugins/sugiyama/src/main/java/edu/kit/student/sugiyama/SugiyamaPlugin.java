package edu.kit.student.sugiyama;


import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedGraphLayoutOption;
import edu.kit.student.parameter.Settings;
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
        DefaultDirectedGraph.getDirectedGraphLayoutRegister().addLayoutOption(new DirectedGraphLayoutOption() {
            
            SugiyamaLayoutAlgorithm algo;
            
            {
                this.setName("Sugiyama");
                this.setId("SUG");
            }

            @Override
            public Settings getSettings() {
                if (algo == null) {
                    chooseLayout();
                }
                return algo.getSettings();
            }
            
            @Override
            public void chooseLayout() {
                algo = new SugiyamaLayoutAlgorithm();
            }
            
            @Override
            public void applyLayout() {
                //TODO: Remove casting by changing some interface (probably LayoutAlgorithm.layout)
                this.algo.layout((DirectedGraph) graph);
            }
        });;
    }
}
