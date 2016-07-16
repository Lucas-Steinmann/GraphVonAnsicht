package edu.kit.student.joana.callgraph;

import edu.kit.student.joana.CallGraphVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph {

    private static LayoutRegister<CallGraphLayoutOption> register;
    private List<MethodGraph> methodGraphs = new LinkedList<>();
    
    public CallGraph(String name, Set<CallGraphVertex> vertices, Set<JoanaEdge> edges) {
        super(name, vertices.stream().collect(Collectors.toSet()), edges);
        for (CallGraphVertex vertex : vertices) {
            methodGraphs.add(vertex.getGraph());
        }
    }

    public CallGraph(String name) {
        this(name, new HashSet<>(), new HashSet<>());
    }

    public List<MethodGraph> getMethodgraphs() {
    	return methodGraphs;
    }

    public static void setRegister(LayoutRegister<CallGraphLayoutOption> register) {
        CallGraph.register = register;
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {

        // Retrieve callgraphLayouts from register
        List<CallGraphLayoutOption> callGraphLayouts = new LinkedList<>();
        if (CallGraph.register != null) {
            callGraphLayouts.addAll(CallGraph.register.getLayoutOptions());
        }
        for (CallGraphLayoutOption option : callGraphLayouts) {
            option.setGraph(this);
        }

        // Add default directed graph layouts;
        List<LayoutOption> layoutOptions = new LinkedList<>(callGraphLayouts);
        layoutOptions.addAll(super.getRegisteredLayouts());

        return layoutOptions;
    }
    
    @Override
	public LayoutOption getDefaultLayout() {
		return new CallGraphLayoutOption() {
			{
                this.setName("Call-Graph-Layout");
                this.setId("CGL");
                this.setGraph(CallGraph.this);
            }
            
            @Override
            public void chooseLayout() {
                this.setLayout(new CallGraphLayout());
            }
		};
	}

    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = super.getStatistics();
    	return statistics;
    }
}
