package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.GraphLayering;
import edu.kit.student.graphmodel.LayeredGraph;
import edu.kit.student.graphmodel.SerializedGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.graphmodel.directed.DirectedEdge;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.Kind;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This is a specific graph representation for a MethodGraph in JOANA .
 */
public class MethodGraph extends JoanaGraph {

    private static final String ENTRY_NAME = "Entry";
    private static LayoutRegister<MethodGraphLayoutOption> register;
    private JoanaVertex entry;
    private Set<FieldAccess> fieldAccesses;
    DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    DefaultGraphLayering<JoanaVertex> layering;

    public MethodGraph(Set<JoanaVertex> vertices, Set<JoanaEdge> edges, 
            Set<FieldAccess> fieldAccesses, String methodName) {
        super(methodName);
        for(JoanaVertex vertex : vertices) {
        	if(vertex.getNodeKind() == Kind.ENTR) {
        	    this.entry = vertex;
        	    break;
            }
        }
        if (entry == null) {
            throw new IllegalArgumentException("Cannot create MethodGraph without entry vertex!");
        }
        graph = new DefaultDirectedGraph<>(methodName, vertices, edges);
        //TODO: Search for method calls, field accesses, etc.
        this.fieldAccesses = new HashSet<>(fieldAccesses);
    }
    
    /**
     * Returns the entry vertex of a method.
     * 
     * @return The entry vertex of a method.
     */
    public JoanaVertex getEntryVertex() { 
        if (entry == null) {
            return searchEntry();
        }
        return entry;
    }

    private JoanaVertex searchEntry() {
        for (JoanaVertex v : getVertexSet()) {
            if (v.getName() == ENTRY_NAME) {
                entry = v;
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns a list of all {@link FieldAccess} in the MethodGraph.
     * 
     * @return A list of all {@link FieldAccess} in the MethodGraph.
     */
    public List<FieldAccess> getFieldAccesses() { 
        return new LinkedList<>(fieldAccesses);
    }

    /**
     * Returns a list of all {@link JoanaVertex} which are method calls in the MethodGraph.
     * 
     * @return A list of all method calls.
     */
    public List<JoanaVertex> getMethodCalls() { 
        // TODO Auto-generated method
        return null;
    } 

    /**
     * Sets the {@link LayoutRegister}, which stores the available 
     * {@link LayoutOption} for all method graphs statically.
     * @param register The {@link LayoutRegister} that will be set.
     */
    public static void setRegister(LayoutRegister<MethodGraphLayoutOption> register) {
        MethodGraph.register = register;
    }

//    @Override
//    public List<LayeredGraph> getSubgraphs() {
//        List<LayeredGraph faGraphs = new LinkedList<>();
//        this.getFieldAccesses().forEach((fa) -> faGraphs.add(fa.getGraph()));
//        return faGraphs;
//    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        List<MethodGraphLayoutOption> methodGraphLayouts = new LinkedList<>();
        if (MethodGraph.register != null) {
            methodGraphLayouts.addAll(MethodGraph.register.getLayoutOptions());
        }
        for (MethodGraphLayoutOption option : methodGraphLayouts) {
            option.setGraph(this);
        }
        List<LayoutOption> layoutOptions = new LinkedList<>(methodGraphLayouts);
        layoutOptions.addAll(graph.getRegisteredLayouts());
        return layoutOptions;
    }
    
	@Override
	public LayoutOption getDefaultLayout() {
		return new MethodGraphLayoutOption() {
			{
                this.setName("Method-Graph-Layout");
                this.setId("MGL");
                this.setGraph(MethodGraph.this);
            }
			
			@Override
			public void chooseLayout() {
				setLayout(new MethodGraphLayout());
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
    public Set<JoanaVertex> getVertexSet() {
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
    public SerializedGraph serialize() {
        // TODO Auto-generated method stub
        return null;
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
    public List<List<JoanaVertex>> getLayers() {
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
}
