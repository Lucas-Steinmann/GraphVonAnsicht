package edu.kit.student.graphml;

import edu.kit.student.plugin.AbstractPluginBase;
import edu.kit.student.plugin.Importer;

import java.util.LinkedList;
import java.util.List;

/**
 * This is the entry point for the {@link GraphmlImporter}-plugin.
 * It provides the Importer to the plugin manager
 * 
 */
public class GmlImporterPlugin extends AbstractPluginBase {

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
    public List<Importer> getImporter() {
        LinkedList<Importer> result = new LinkedList<>();
        result.add(new GraphmlImporter());
        return result;
    }
}