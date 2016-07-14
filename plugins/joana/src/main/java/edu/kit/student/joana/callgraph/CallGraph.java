package edu.kit.student.joana.callgraph;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.JoanaCompoundVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.methodgraph.MethodGraph;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is a specified graph representation for the Callgraph in Joana.
 */
public class CallGraph extends JoanaGraph {

    private static LayoutRegister<CallGraphLayoutOption> register;
    DefaultDirectedGraph<JoanaCompoundVertex, JoanaEdge> graph;
    DefaultGraphLayering<JoanaCompoundVertex> layering;
    
    private GAnsProperty<Integer> vertexCount;
    private GAnsProperty<Integer> edgeCount;

    public CallGraph(String name, Set<JoanaCompoundVertex> vertices, Set<JoanaEdge> edges) {
        super(name);
        //TODO: Add MethodGraphs as subgraphs?
        this.graph = new DefaultDirectedGraph<>("", vertices, edges);
        this.layering = new DefaultGraphLayering<>(vertices);
        
        this.vertexCount = new GAnsProperty<Integer>("Vertex count", vertices.size());
        this.edgeCount = new GAnsProperty<Integer>("Edge count", edges.size());
    }

    public CallGraph(String name) {
        super(name);
    }

    public List<MethodGraph> getMethodgraphs() {
    	List<MethodGraph> list = new ArrayList<MethodGraph>();
    	//TODO: Remove cast
    	getVertexSet().forEach((vertex) -> list.add((MethodGraph)vertex.getGraph()));
    	return list;
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
        layoutOptions.addAll(graph.getRegisteredLayouts());

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
    public Integer outdegreeOf(Vertex vertex) {
        return graph.outdegreeOf(vertex);
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return graph.indegreeOf(vertex);
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return graph.outgoingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return graph.incomingEdgesOf(vertex);
    }

    @Override
    public Set<JoanaCompoundVertex> getVertexSet() {
        return graph.getVertexSet();
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return graph.getEdgeSet();
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return graph.edgesOf(vertex);
    }

    @Override
    public FastGraphAccessor getFastGraphAccessor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void addToFastGraphAccessor(FastGraphAccessor fga) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getLayerCount() {
        return this.layering.getLayerCount();
    }

    @Override
    public int getVertexCount(int layerNum) {
        return layering.getVertexCount(layerNum);
    }

    @Override
    public int getLayerFromVertex(Vertex vertex) {
        return layering.getLayerFromVertex(vertex);
    }

    @Override
    public List<? extends Vertex> getLayer(int layerNum) {
        return layering.getLayer(layerNum);
    }

    @Override
    public List<List<JoanaCompoundVertex>> getLayers() {
        return layering.getLayers();
    }

    @Override
    public int getHeight() {
        return layering.getHeight();
    }

    @Override
    public int getLayerWidth(int layerN) {
        return layering.getLayerWidth(layerN);
    }

    @Override
    public int getMaxWidth() {
        return layering.getMaxWidth();
    }

    @Override
    public CollapsedVertex collapse(Set<Vertex> subset) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<? extends Vertex> expand(CollapsedVertex vertex) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isCollapsed(Vertex vertex) {
        // TODO Auto-generated method stub
        return false;
    }
    
    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = super.getStatistics();
    	statistics.add(this.vertexCount);
    	statistics.add(this.edgeCount);
    	return statistics;
    }
}
