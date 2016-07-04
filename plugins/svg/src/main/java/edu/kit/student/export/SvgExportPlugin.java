package edu.kit.student.export;

import edu.kit.student.plugin.AbstractPluginBase;
import edu.kit.student.plugin.Exporter;

import java.util.LinkedList;
import java.util.List;

/**
 * This class provides the {@link SvgExporter} to the plugin manager.
 */
public class SvgExportPlugin extends AbstractPluginBase {

    private static final String pluginName = "SVG";

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public List<Exporter> getExporter() {
        LinkedList<Exporter> result = new LinkedList<>();
        result.add(new SvgExporter());
        return result;
    }
}
