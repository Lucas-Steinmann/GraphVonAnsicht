package edu.kit.student.joana.methodgraph;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.Kind;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

/**
 * This is a specific graph representation for a MethodGraph in JOANA .
 */
public class MethodGraph extends JoanaGraph {

    private static final String ENTRY_NAME = "Entry";
    private static LayoutRegister<MethodGraphLayoutOption> register;

    private JoanaVertex entry;
    private Set<FieldAccess> fieldAccesses;
    
    private GAnsProperty<Integer> vertexCount;
    private GAnsProperty<Integer> fieldAccessCount;

    public MethodGraph(Set<JoanaVertex> vertices, Set<JoanaEdge> edges, 
            String methodName) {
        super(methodName, vertices, edges);
        for(JoanaVertex vertex : vertices) {
        	if(vertex.getNodeKind() == Kind.ENTR) {
        	    this.entry = vertex;
        	    break;
            }
        }
        if (entry == null) {
            throw new IllegalArgumentException("Cannot create MethodGraph without entry vertex!");
        }
        //TODO: Search for method calls etc.
        this.fieldAccesses = this.searchFieldAccesses();
        
        this.vertexCount = new GAnsProperty<Integer>("Vertex count", vertices.size());
        this.fieldAccessCount = new GAnsProperty<Integer>("Field accesses", this.fieldAccesses.size());
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
        layoutOptions.addAll(super.getRegisteredLayouts());
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
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = super.getStatistics();
    	statistics.add(this.vertexCount);
    	statistics.add(this.fieldAccessCount);
    	return statistics;
    }
    
    //private method to search all Fieldaccesses in the graph
    private Set<FieldAccess> searchFieldAccesses() {
        
        Set<FieldAccess> fieldAccesses = new HashSet<FieldAccess>();
        
        for (JoanaVertex v1 : this.getVertexSet()) {
   
            if (this.isNormCompoundBase(v1)) {
                //check for field-gets field-sets and arrays
                for (JoanaEdge e1 : this.outgoingEdgesOf(v1)) {
                    if (e1.getEdgeKind() == JoanaEdge.Kind.CF) {
                        JoanaVertex v2 = e1.getTarget();                     
                        if (this.isNormCompoundField(v2)) {
                            //check for field gets
                            FieldAccess temp = this.findFieldGet(v1, v2, e1);
                            if (temp != null) {
                                fieldAccesses.add(temp);
                            }
                        } else if (this.isExprModify(v2)) {
                            //check for field sets
                            FieldAccess temp = this.findFieldSet(v1, v2, e1); 
                            if (temp != null) {
                                fieldAccesses.add(temp);
                            }
                        } else if (this.isNormCompoundIndex(v2)) {
                            //check if array field access (Get and Set)
                            FieldAccess temp = this.findArrayFieldAccess(v1, v2, e1); 
                            if (temp != null) {
                                fieldAccesses.add(temp);
                            }
                        }
                    }
                }
            } else if (this.isNormCompoundField(v1)) {
                //check for static field get
                FieldAccess temp = this.findStaticFieldGet(v1);
                if (temp != null) {
                    fieldAccesses.add(temp);
                }
            } else if (this.isExprModify(v1)) {
                //check for static field set
                FieldAccess temp = this.findStaticFieldSet(v1);
                if (temp != null) {
                    fieldAccesses.add(temp);
                }
            } 
        } 
        
        return fieldAccesses;
    }
    
    //function to search fieldGet. Gets the first and second vertex of an possible fieldGet
    private FieldAccess findFieldGet(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isExprReference(v3)) {
                    //check if there is an edge back
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF
                                && e3.getTarget() == v1) {
                            
                            //System.out.println("field-get: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName());
                            
                            //create FieldAccess
                            Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                            Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                            vertices.add(v1);
                            vertices.add(v2);
                            vertices.add(v3);
                            edges.add(e1);
                            edges.add(e2);
                            edges.add(e3);
                            
                            FieldAccessGraph graph = new FieldAccessGraph("field-get", vertices, edges);
                            return new FieldAccess(graph, "field-get", "field-get");
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    //function to search for fieldSet. Gets the first and second vertex of an possible fieldGet
    private FieldAccess findFieldSet(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isNormCompoundField(v3)) {
                    //check if there is an edge back
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF
                                && e3.getTarget() == v1) {
                            
                            //System.out.println("field-set: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName());
                            
                            //create FieldAccess
                            Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                            Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                            vertices.add(v1);
                            vertices.add(v2);
                            vertices.add(v3);
                            edges.add(e1);
                            edges.add(e2);
                            edges.add(e3);
                            
                            FieldAccessGraph graph = new FieldAccessGraph("field-set", vertices, edges);
                            return new FieldAccess(graph, "field-set", "field-set");
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    //function to search for StaticFieldGet. Gets the first vertex of an possible staticFieldGet
    private FieldAccess findStaticFieldGet(JoanaVertex v1) {
      
        for (JoanaEdge e1 : this.outgoingEdgesOf(v1)) {
            if (e1.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v2 = e1.getTarget();
                if (this.isExprReference(v2)) {                 

                    //TODO: check for outgoing and incoming Edges?
                    if (this.isValidStaticField(v1)) {
                        //found static field get
                        
                      //System.out.println("static field-get: " + v1.getName() + " : " + v2.getName());
                      
                      //create FieldAccess
                      Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                      Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                      vertices.add(v1);
                      vertices.add(v2);
                      edges.add(e1);
                      
                      FieldAccessGraph graph = new FieldAccessGraph("static field-get", vertices, edges);
                      return new FieldAccess(graph, "static field-get", "static field-get");
                    }
                }
            }
        }
        
        return null;
    }
    
    //function to search for StaticFieldSet. Gets the first vertex of an possible StatiFieldSet
    private FieldAccess findStaticFieldSet(JoanaVertex v1) {

        for (JoanaEdge e1 : this.outgoingEdgesOf(v1)) {
            if (e1.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v2 = e1.getTarget();
                if (this.isNormCompoundField(v2)) {
                    //TODO: check for outgoing and incoming Edges?
                    
                    if (this.isValidStaticField(v1)) {
                        //found static field Set
                      //System.out.println("static field-set: " + v1.getName() + " : " + v2.getName());
                      
                      //create FieldAccess
                      Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                      Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                      vertices.add(v1);
                      vertices.add(v2);
                      edges.add(e1);
                      
                      FieldAccessGraph graph = new FieldAccessGraph("static field-set", vertices, edges);
                      return new FieldAccess(graph, "static field-set", "static field-set");
                    }
                }
            }
        }
        
        return null;
    }
    
    //function to find array Fieldaccesses. Gets the first and second Vertex of an possible arrayfieldaccess
    private FieldAccess findArrayFieldAccess(JoanaVertex v1, JoanaVertex v2, JoanaEdge e1) {
        
        for (JoanaEdge e2 : this.outgoingEdgesOf(v2)) {
            if (e2.getEdgeKind() == JoanaEdge.Kind.CF) {
                JoanaVertex v3 = e2.getTarget();
                //check if array field gets
                if (this.isNormCompoundField(v3)) {
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isExprReference(v4)) {
                                //check there is an edge back
                                for (JoanaEdge e4 : this.outgoingEdgesOf(v4)) {
                                    if (e4.getEdgeKind() == JoanaEdge.Kind.CF
                                            && e4.getTarget() == v1) {
                                        
                                        //System.out.println("array field-get: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName()+ " : " + v4.getName());
                                        
                                        //create FieldAccess
                                        Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                                        Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                                        vertices.add(v1);
                                        vertices.add(v2);
                                        vertices.add(v3);
                                        vertices.add(v4);
                                        edges.add(e1);
                                        edges.add(e2);
                                        edges.add(e3);
                                        edges.add(e4);
                                        
                                        FieldAccessGraph graph = new FieldAccessGraph("array field-get", vertices, edges);
                                        return new FieldAccess(graph, "array field-get", "array field-get");
                                    }
                                }
                            }
                        }
                    }
                } else if (this.isExprModify(v3)) {
                    //check if array field sets
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.Kind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isNormCompoundField(v4)) {
                                //check there is an edge back
                                for (JoanaEdge e4 : this.outgoingEdgesOf(v4)) {
                                    if (e4.getEdgeKind() == JoanaEdge.Kind.CF
                                            && e4.getTarget() == v1) {
                                        
                                        //System.out.println("array field-set: " + v1.getName() + " : " + v2.getName() + " : " + v3.getName()+ " : " + v4.getName());
                                        
                                        //create FieldAccess
                                        Set<JoanaVertex> vertices = new LinkedHashSet<JoanaVertex>();
                                        Set<JoanaEdge> edges = new LinkedHashSet<JoanaEdge>();                   
                                        vertices.add(v1);
                                        vertices.add(v2);
                                        vertices.add(v3);
                                        vertices.add(v4);
                                        edges.add(e1);
                                        edges.add(e2);
                                        edges.add(e3);
                                        edges.add(e4);
                                        
                                        FieldAccessGraph graph = new FieldAccessGraph("array field-set", vertices, edges);
                                        return new FieldAccess(graph, "array field-set", "array field-set");
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        return null;
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
