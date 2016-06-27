package edu.kit.student.export;

import java.util.List;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.WorkspaceOption;

/**
 * This class provides the {@link SvgExporter} to the plugin manager
 *
 */
public class SVGExportPlugin implements Plugin{

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void load() {
		// TODO Auto-generated method stub
		
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
