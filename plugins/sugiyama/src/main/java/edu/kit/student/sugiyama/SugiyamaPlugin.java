package edu.kit.student.sugiyama;

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
 * A plugin for GAns that supplies a layout algorithm based on the Sugiyama-framework.
 */
public class SugiyamaPlugin implements Plugin {
	private String name;

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
