package edu.kit.student.joana;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.Edge;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.Workspace;
import edu.kit.student.plugin.WorkspaceOption;

/**
 * A plugin for GAns that supports the creation and visualization of Joana
 * system dependence graphs.
 */
public class JoanaPlugin implements Plugin {

	private static CallGraphLayoutRegister cRegister;
	private static MethodGraphLayoutRegister mRegister;

	private final static String pluginName = "JOANA";

	private List<WorkspaceOption> wsOptions;

	/**
	 * Constructor. The constructor is called by the ServiceLoader.
	 */
	public JoanaPlugin() {
		WorkspaceOption joanaws = new WorkspaceOption() {

			{
				this.setName("JOANA-Workspace");
				this.setID("joana");
				this.ws = new JoanaWorkspace();
			}

			JoanaWorkspace ws;

			@Override
			public Workspace getInstance() {
				ws.initialize();
				return ws;
			}

			@Override
			public Settings getSettings() {
				return this.ws.getSettings();
			}
		};
		wsOptions = new LinkedList<WorkspaceOption>();
		wsOptions.add(joanaws);
	}

	@Override
	public String getName() {
		return pluginName;
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

	public static CallGraphLayoutRegister getCallGraphLayoutRegister() {
		return cRegister;
	}

	public static MethodGraphLayoutRegister getMethodGraphLayoutRegister() {
		return mRegister;
	}

	public static class CallGraphLayoutRegister implements LayoutRegister<CallGraphLayoutOption> {
		@Override
		public void addLayoutOption(CallGraphLayoutOption option) {
		}

		@Override
		public List<LayoutOption> getLayoutOptions() {
			return null;
		}
	}

	public static class MethodGraphLayoutRegister implements LayoutRegister<MethodGraphLayoutOption> {
		@Override
		public void addLayoutOption(MethodGraphLayoutOption option) {
			// TODO Auto-generated method stub
		}

		@Override
		public List<LayoutOption> getLayoutOptions() {
			// TODO Auto-generated method stub
			return null;
		}
	}

}
