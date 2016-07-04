package edu.kit.student.joana;

import edu.kit.student.joana.callgraph.CallGraphLayoutOption;
import edu.kit.student.joana.methodgraph.MethodGraphLayoutOption;
import edu.kit.student.parameter.Settings;
import edu.kit.student.plugin.AbstractPluginBase;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;
import edu.kit.student.plugin.Workspace;
import edu.kit.student.plugin.WorkspaceOption;

import java.util.LinkedList;
import java.util.List;

/**
 * A plugin for GAns that supports the creation and visualization of Joana
 * system dependence graphs.
 */
public class JoanaPlugin extends AbstractPluginBase {

    private static CallGraphLayoutRegister cRegister;
    private static MethodGraphLayoutRegister mRegister;

    private static final String pluginName = "JOANA";

    private List<WorkspaceOption> wsOptions;

    /**
     * Constructor. The constructor is called by the ServiceLoader.
     */
    public JoanaPlugin() {
        WorkspaceOption joanaws = new WorkspaceOption() {

            {
                this.setName("JOANA-Workspace");
                this.setId("joana");
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
    public void load() { }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
        return wsOptions;
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

    public static class MethodGraphLayoutRegister 
        implements LayoutRegister<MethodGraphLayoutOption> {
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
