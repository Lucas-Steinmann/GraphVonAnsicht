package edu.kit.student.graphmodel.viewable;

import java.util.LinkedList;
import java.util.List;

import edu.kit.student.graphmodel.directed.DirectedGraphLayoutOption;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.Exporter;
import edu.kit.student.plugin.Importer;
import edu.kit.student.plugin.LayoutRegister;
import edu.kit.student.plugin.Plugin;
import edu.kit.student.plugin.VertexFilter;
import edu.kit.student.plugin.Workspace;
import edu.kit.student.plugin.WorkspaceOption;

/**
 * @author Lucas Steinmann
 *
 */
public class GenericGraphPlugin implements Plugin {

    private static final String pluginName = "Generic Graph Plugin";
    public static final LayoutRegister<DirectedGraphLayoutOption> directedGraphLayoutOptions 
    	= new DirectedGraphLayoutOptionRegister();

	@Override
	public String getName() {
		return pluginName;
	}

	@Override
	public void load() {
	}

	@Override
	public List<WorkspaceOption> getWorkspaceOptions() {
		List<WorkspaceOption> options = new LinkedList<>();
		options.add(new WorkspaceOption() {

            {
                this.setName("Generic-Graph-Workspace");
                this.setId("generic");
                this.ws = new GenericWorkspace();
            }
            
            GenericWorkspace ws;
			
			@Override
			public Workspace getInstance() {
                ws.initialize();
				return ws;
			}
			
			@Override
			public Settings getSettings() {
				return ws.getSettings();
			}
		});
		return options;
	}

	@Override
	public List<VertexFilter> getVertexFilter() {
		return new LinkedList<>();
	}

	@Override
	public List<EdgeFilter> getEdgeFilter() {
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

    public static class DirectedGraphLayoutOptionRegister implements LayoutRegister<DirectedGraphLayoutOption> {
        
        List<DirectedGraphLayoutOption> options = new LinkedList<>();
        
        @Override
        public void addLayoutOption(DirectedGraphLayoutOption option) {
            options.add(option);
        }

        @Override
        public List<DirectedGraphLayoutOption> getLayoutOptions() {
            return new LinkedList<>(options);
        }
    }
}
