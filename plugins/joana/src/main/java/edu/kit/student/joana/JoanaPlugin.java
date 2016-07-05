package edu.kit.student.joana;

import edu.kit.student.joana.callgraph.CallGraph;
import edu.kit.student.joana.callgraph.CallGraphLayout;
import edu.kit.student.joana.callgraph.CallGraphLayoutOption;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.joana.methodgraph.MethodGraphLayout;
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

    private static CallGraphLayoutRegister cRegister = new CallGraphLayoutRegister();
    private static MethodGraphLayoutRegister mRegister = new MethodGraphLayoutRegister();

    private static final String pluginName = "JOANA";

    /**
     * Constructor. The constructor is called by the ServiceLoader.
     */
    public JoanaPlugin() {
    }

    @Override
    public String getName() {
        return pluginName;
    }

    @Override
    public void load() { 
        MethodGraph.setRegister(mRegister);
        CallGraph.setRegister(cRegister);
        cRegister.addLayoutOption(new CallGraphLayoutOption() {
            {
                this.setName("Call-Graph-Layout");
                this.setId("CGL");
            }
            
            @Override
            public void chooseLayout() {
                this.setLayout(new CallGraphLayout());
            }
        });
        mRegister.addLayoutOption(new MethodGraphLayoutOption() {

            {
                this.setName("Method-Graph-Layout");
                this.setId("MGL");
            }
            
            @Override
            public void chooseLayout() {
                this.setLayout(new MethodGraphLayout());
            }
        });
    }

    @Override
    public List<WorkspaceOption> getWorkspaceOptions() {
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
        LinkedList<WorkspaceOption> wsOptions = new LinkedList<WorkspaceOption>();
        wsOptions.add(joanaws);
        return wsOptions;
    }

    public static CallGraphLayoutRegister getCallGraphLayoutRegister() {
        return cRegister;
    }

    public static MethodGraphLayoutRegister getMethodGraphLayoutRegister() {
        return mRegister;
    }

    public static class CallGraphLayoutRegister implements LayoutRegister<CallGraphLayoutOption> {
        
        List<CallGraphLayoutOption> options = new LinkedList<>();
        
        @Override
        public void addLayoutOption(CallGraphLayoutOption option) {
            options.add(option);
        }

        @Override
        public List<CallGraphLayoutOption> getLayoutOptions() {
            return new LinkedList<>(options);
        }
    }

    public static class MethodGraphLayoutRegister 
        implements LayoutRegister<MethodGraphLayoutOption> {

        List<MethodGraphLayoutOption> options = new LinkedList<>();

        @Override
        public void addLayoutOption(MethodGraphLayoutOption option) {
            options.add(option);
        }

        @Override
        public List<MethodGraphLayoutOption> getLayoutOptions() {
            return new LinkedList<>(options);
        }
    }

}
