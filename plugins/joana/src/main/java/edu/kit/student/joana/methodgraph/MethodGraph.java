package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
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
            String methodName) {
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
        this.searchFieldAccesses();
        
        this.fieldAccesses = new HashSet<>();
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
    
    //private method to search all Fieldaccesses in the graph
    private void searchFieldAccesses() {
        
        for (JoanaVertex v1 : this.graph.getVertexSet()) {
   
            if (this.isNormCompoundBase(v1)) {
                //check for field-gets field-sets and arrays
                for (JoanaEdge e1 : this.outgoingEdgesOf(v1)) {
                    if (e1.getEdgeKind() == JoanaEdge.Kind.CF) {
                        JoanaVertex v2 = e1.getTarget();                     
                        if (this.isNormCompoundField(v2)) {
                            //check for field gets
                            this.findFieldGet(v1, v2, e1);
                        } else if (this.isExprModify(v2)) {
                            //check for field sets
                            this.findFieldSet(v1, v2, e1);                           
                        } else if (this.isNormCompoundIndex(v2)) {
                            //check if array field access (Get and Set)
                            this.findArrayFieldAccess(v1, v2, e1);         
                        }
                    }
                }
            } else if (this.isNormCompoundField(v1)) {
                //check for static field get
                this.findStaticFieldGet(v1);
            } else if (this.isExprModify(v1)) {
                //check for static field set
                this.findStaticFieldSet(v1);
            } 
        }  
    }
    
    //function to search fieldGet. Gets the first and second vertex of an possible fieldGet
    private void findFieldGet(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isExprReference(v3)) {
                    //TODO: check if there is an edge back
                    System.out.println("field-get: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName());
                }
            }
        } 
    }
    
    //function to search for fieldSet. Gets the first and second vertex of an possible fieldGet
    private void findFieldSet(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isNormCompoundField(v3)) {
                    //TODO: check if there is an edge back
                    System.out.println("field-set: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName());
                }
            }
        }
    }
    
    //function to search for StaticFieldGet. Gets the first vertex of an possible staticFieldGet
    private void findStaticFieldGet(JoanaVertex v1) {
      
        for (JoanaEdge e : this.outgoingEdgesOf(v1)) {
            if (e.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v2 = e.getTarget();
                if (this.isExprReference(v2)) {                 

                    //TODO: check for outgoing and incoming Edges
                    if (this.isValidStaticField(v1)) {
                        //found static field get
                      System.out.println("static field-get: " + v1.getName() + " : " + v2.getName());
                    }
                }
            }
        }
    }
    
    //function to search for StaticFieldSet. Gets the first vertex of an possible StatiFieldSet
    private void findStaticFieldSet(JoanaVertex v1) {

        for (JoanaEdge e : this.outgoingEdgesOf(v1)) {
            if (e.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v2 = e.getTarget();
                if (this.isNormCompoundField(v2)) {
                    //TODO: check for outgoing and incoming Edges
                    
                    if (this.isValidStaticField(v1)) {
                        //found static field Set
                      System.out.println("static field-set: " + v1.getName() + " : " + v2.getName());
                    }
                }
            }
        }
    }
    
    //function to find array Fieldaccesses. Gets the first and second Vertex of an possible arrayfieldaccess
    private void findArrayFieldAccess(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                //check if array field gets
                if (this.isNormCompoundField(v3)) {
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isExprReference(v4)) {
                                //TODO: check there is an edge back
                                System.out.println("array field-get: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName()+ " : " + v4.getName());
                            }
                        }
                    }
                } else if (this.isExprModify(v3)) {
                    //check if array field sets
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isNormCompoundField(v4)) {
                                //TODO: check there is an edge back
                                System.out.println("array field-set: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName()+ " : " + v4.getName());
                            }
                        }
                    }
                }
            }
        }
    }
    
    //checks if the first vertex of an static field access is subgraph of other field access
    private boolean isValidStaticField(JoanaVertex v) {
        boolean result = true;
        for (JoanaEdge pred : this.incomingEdgesOf(v)) {
            if (pred.getEdgeKind() == JoanaEdge.Kind.CF
                    && (this.isNormCompoundBase(pred.getSource())
                            || this.isNormCompoundIndex(pred.getSource()))) {
                result = false;
                break;
            }
        }
        
        return result;
    }    
    
    //TODO: change names of the checkers and maybe add additional criterions
    private boolean isNormCompoundBase(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.Kind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().equals("base")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isNormCompoundField(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.Kind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().matches("field\\s.*")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isNormCompoundIndex(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.Kind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().equals("index")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isExprReference(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.Kind.EXPR
                && vertex.getNodeOperation().equals("reference")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isExprModify(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.Kind.EXPR
                && vertex.getNodeOperation().equals("modify")) { 
            return true;
        } else {
            return false;
        }
    }
}
