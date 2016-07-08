package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.joana.JoanaCompoundVertex;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph<JoanaCompoundVertex,JoanaEdge<JoanaCompoundVertex>> {

    private static LayoutRegister<CallGraphLayoutOption> register;

    public CallGraph(String name, Set<JoanaCompoundVertex> vertices, Set<JoanaEdge<JoanaCompoundVertex>> edges) {
        super(name, vertices, edges);
    }

    public CallGraph(String name) {
        super(name);
    }

    @Override
    public List<LayeredGraph<JoanaCompoundVertex, JoanaEdge<JoanaCompoundVertex>>> getSubgraphs() {
        return new LinkedList<LayeredGraph<JoanaCompoundVertex, JoanaEdge<JoanaCompoundVertex>>>();
    }
    public List<MethodGraph> getMethodgraphs() {
    	List<MethodGraph> list = new ArrayList<MethodGraph>();
    	getVertexSet().forEach((vertex) -> list.add((MethodGraph)vertex.getGraph()));
    	return list;
    }

    public static void setRegister(LayoutRegister<CallGraphLayoutOption> register) {
        CallGraph.register = register;
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        List<CallGraphLayoutOption> callGraphLayouts = new LinkedList<>();
        if (CallGraph.register != null) {
            callGraphLayouts.addAll(CallGraph.register.getLayoutOptions());
        }
        for (CallGraphLayoutOption option : callGraphLayouts) {
            option.setGraph(this);
        }
        List<LayoutOption> layoutOptions = new LinkedList<>(callGraphLayouts);
        layoutOptions.addAll(super.getRegisteredLayouts());
        return layoutOptions;
    }
}
