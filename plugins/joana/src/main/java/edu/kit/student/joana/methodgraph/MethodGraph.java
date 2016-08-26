package edu.kit.student.joana.methodgraph;

import java.util.*;
import java.util.stream.Collectors;

import edu.kit.student.graphmodel.SubGraph;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.ViewableVertex;
import edu.kit.student.graphmodel.action.SubGraphAction;
import edu.kit.student.graphmodel.action.VertexAction;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessCollapser;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaCollapsedVertex;
import edu.kit.student.joana.JoanaCollapser;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaPlugin;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.VertexKind;
import edu.kit.student.joana.graphmodel.DirectedOnionPath;
import edu.kit.student.joana.graphmodel.JoanaCompoundVertex;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.EdgeFilter;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.VertexFilter;

/**
 * This is a specific graph representation for a MethodGraph in JOANA .
 */
public class MethodGraph extends JoanaGraph {

    private static final String ENTRY_NAME = "Entry";
    private JoanaVertex entry;
    private Set<FieldAccess> fieldAccesses = new HashSet<>();
    private DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    
    private GAnsProperty<Integer> fieldAccessCount;
    private Map<JoanaCollapsedVertex, VertexAction> expandActions;
    
    private FieldAccessCollapser fcollapser;
    private JoanaCollapser collapser;

    public MethodGraph(Set<JoanaVertex> vertices, Set<JoanaEdge> edges, 
            String methodName) {
        super(methodName, vertices, edges);
        this.graph = new DefaultDirectedGraph<>(vertices, edges);
        for (JoanaVertex vertex : vertices) {
        	if(vertex.getNodeKind() == VertexKind.ENTR) {
        	    this.entry = vertex;
        	    break;
            }
        }
        if (entry == null) {
            throw new IllegalArgumentException("Cannot create MethodGraph without entry vertex!");
        }
        //TODO: Search for method calls etc.
        this.fieldAccesses = this.searchFieldAccesses();
        this.fieldAccessCount = new GAnsProperty<Integer>("Field accesses", this.fieldAccesses.size());

        Map<JoanaEdge, DirectedOnionPath<JoanaEdge, JoanaCompoundVertex>> onionEdges = new HashMap<>();
        this.fcollapser = new FieldAccessCollapser(graph, onionEdges);
        this.collapser = new JoanaCollapser(graph, onionEdges);
        this.expandActions = new HashMap<>();
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
            if (Objects.equals(v.getName(), ENTRY_NAME)) {
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
        return fieldAccesses.stream()
                            .filter(fa -> fa.getGraph().getVertexSet().size() != 0)
                            .filter(fa -> fa.getGraph().getVertexSet().stream()
                                                                      .anyMatch(v -> getVertexSet().contains(v)))
                            .collect(Collectors.toList());
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

    @Override
    public List<VertexAction> getVertexActions(Vertex vertex) {
        List<VertexAction> actions = new LinkedList<>();
        if (collapser.getCollapsedVertices().contains(vertex) 
                           && getVertexSet().contains(vertex)) {
            actions.add(expandActions.get(vertex));
        }
        getFieldAccesses().stream()
                          .filter(fa -> fa.getGraph().getVertexSet().contains(vertex))
                          .forEach(fa -> actions.add(newCollapseFieldAccessAction(fa)));
                          
        return actions;
    }

    @Override
    public List<SubGraphAction> getSubGraphActions(Set<ViewableVertex> vertices) {
        List<SubGraphAction> actions = new LinkedList<>();
        if (getVertexSet().containsAll(vertices) && 
        		vertices.size() > 1) {
            // TODO: Decide between cases:
            // a) vertices subset of one field access -> collapse in field access
            // b) vertices intersect one ore more field accesses -> no collapse possible
            // c) vertices are disjoint of all field access or all intersected field accesses are subsets -> collapse
            List<FieldAccess> cuttedFAs = new LinkedList<>();
            for (FieldAccess fa : this.getFieldAccesses()) {
                if (fa.getGraph().getVertexSet().stream().anyMatch(v -> vertices.contains(v))) {
                    cuttedFAs.add(fa);
                }
            } 
            if (cuttedFAs.size() == 1) {
                if (cuttedFAs.iterator().next().getGraph().getVertexSet().containsAll(vertices)) {
                    //actions.add(newCollapseAction(vertices, cuttedFAs.iterator().next()));
                    return actions;
                }
            }
            for (FieldAccess fa : cuttedFAs) {
                if (fa.getGraph().getVertexSet().stream().anyMatch(v -> !vertices.contains(v))) {
                    // b) some cut field access is not fully contained
                    return actions;
                }
            }
            // c) all cut field accesses are fully contained
            actions.add(newCollapseAction(vertices, null));
        }
        return actions;
    }

    private VertexAction newCollapseFieldAccessAction(FieldAccess fa) {
	    return new VertexAction("Collapse Field access", 
	            "Collapses the field access.") {
            
            @Override
            public void handle() {
                JoanaCollapsedVertex collapsed = collapse(fa.getGraph().getVertexSet());
                expandActions.put(collapsed, newExpandAction(collapsed, fa));
            }
        };
    }

	private VertexAction newExpandAction(JoanaCollapsedVertex vertex, FieldAccess fa) {
	    return new VertexAction("Expand", 
	            "Adds all vertices contained in this Summary-Vertex to the graph and removes the Summary-Vertex.") {
            
            @Override
            public void handle() {
                expand(vertex);
                if (fa != null) {
//                    fa.getGraph().expand(vertex);
                }
            }
        };
	}
	
	private SubGraphAction newCollapseAction(Set<ViewableVertex> vertices, FieldAccess fa) {
	    return new SubGraphAction("Collapse", "Collapses all vertices into one Summary-Vertex.") {
            
            @Override
            public void handle() {
                JoanaCollapsedVertex collapsed = collapse(vertices);
                expandActions.put(collapsed, newExpandAction(collapsed, fa));
                if (fa != null) {
//                    fa.getGraph().collapse(vertices, collapsed);
                }
            }
        };
	}
	
	/**
	 * Collapses all visible field access.
	 * This will result in the set of vertices which build up the field access being replaced by one representative vertex.
	 * Field Access are not visible if they are (partially) contained in a collapsed vertex.
	 * @return all collapsed field access
	 */
	public List<FieldAccess> collapseFieldAccesses() {
	    List<FieldAccess> collapsedFas = new LinkedList<>();
	    for (FieldAccess fa : this.getFieldAccesses()) {
	        // If all vertices are contained in the graph replace the fieldAccess
	        if (fa.getGraph().getVertexSet().size() != 0) {
	            fcollapser.collapseFieldAccess(fa);
	            collapsedFas.add(fa);
	        }
	    }
	    return collapsedFas;
	}
	
	/**
	 * Expands all visible field access, which have been collapsed earlier.
	 * This will result in the field access being represented by single field access
	 * vertices instead of one representing vertex.
	 * Field Access are not visible if they are contained in a collapsed vertex.
	 * @return all expanded field accesses
	 */
	public List<FieldAccess> expandFieldAccesses() {
	    return expandFieldAccesses(new LinkedList<>(fieldAccesses));
	}
	
	
	/**
	 * Expands all visible field accesses contained in the specified list.
	 * This will result in the field access being represented by single
	 * field access vertices instead of one representing vertex.
	 * Field Access are not visible if they are contained in a collapsed vertex.
	 * @return all expanded field accesses
	 */
	public List<FieldAccess> expandFieldAccesses(List<FieldAccess> fieldAccesses) {
	    List<FieldAccess> collapsedFas = new LinkedList<>();
	    for (FieldAccess fa : fieldAccesses) {
	        // If field access is contained in the graph (and not collapsed for example) it is expanded
	        if (graph.contains(fa)) {
	            fcollapser.expandFieldAccess(fa);
	            collapsedFas.add(fa);
	        }
	    }
	    return collapsedFas;
	}

	public JoanaCollapsedVertex collapse(Set<? extends ViewableVertex> subset) {
        Set<JoanaVertex> directedSubset = new HashSet<JoanaVertex>();
	    for (Vertex v : subset) {
	        if (!graph.contains(v)) {
                throw new IllegalArgumentException("Cannot collapse vertices, not contained in this graph.");
	        } else {
	            directedSubset.add(graph.getVertexById(v.getID()));
	        }
	    }
	    JoanaCollapsedVertex collapsed = collapser.collapse(directedSubset);
		return collapsed;
	}
	
    public Set<JoanaVertex> expand(JoanaCollapsedVertex vertex) {
        return collapser.expand(vertex);
    }

    @Override
    public Integer outdegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex)).size();
    }

    @Override
    public Integer indegreeOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex)).size();
    }

    @Override
    public Integer selfLoopNumberOf(Vertex vertex) {
        return graph.selfLoopNumberOf(vertex);
    }

    @Override
    public Set<JoanaEdge> outgoingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.outgoingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> incomingEdgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.incomingEdgesOf(vertex));
    }

    @Override
    public Set<JoanaEdge> selfLoopsOf(Vertex vertex) {
        return graph.selfLoopsOf(vertex);
    }

    @Override
    public Set<JoanaVertex> getVertexSet() {
        Set<JoanaVertex> result = removeFilteredVertices(graph.getVertexSet());
        return result;
    }

    @Override
    public Set<JoanaEdge> getEdgeSet() {
        return removeFilteredEdges(graph.getEdgeSet());
    }

    @Override
    public Set<JoanaEdge> edgesOf(Vertex vertex) {
        return removeFilteredEdges(graph.edgesOf(vertex));
    }

    @Override
    public Set<? extends SubGraph> getSubGraphs() {
        return getFieldAccesses().stream()
                                 .map(fa -> fa.getGraph())
                                 .collect(Collectors.toSet());
    }
    
    @Override
    public void addVertexFilter(VertexFilter filter) {
        super.addVertexFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().addVertexFilter(filter));
    }


    @Override
    public void setVertexFilter(List<VertexFilter> filter) {
        super.setVertexFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().setVertexFilter(filter));
    }


    @Override
    public void addEdgeFilter(EdgeFilter filter) {
        super.addEdgeFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().addEdgeFilter(filter));
    }


    @Override
    public void setEdgeFilter(List<EdgeFilter> filter) {
        super.setEdgeFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().setEdgeFilter(filter));
    }


    @Override
    public void removeVertexFilter(VertexFilter filter) {
        super.removeVertexFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().removeVertexFilter(filter));
    }


    @Override
    public void removeEdgeFilter(EdgeFilter filter) {
        super.removeEdgeFilter(filter);
        fieldAccesses.forEach((fa) -> fa.getGraph().removeEdgeFilter(filter));
    }

    @Override
    public List<LayoutOption> getRegisteredLayouts() {
        List<MethodGraphLayoutOption> methodGraphLayouts = new LinkedList<>();
        if (JoanaPlugin.getMethodGraphLayoutRegister().getLayoutOptions() != null) {
            methodGraphLayouts.addAll(JoanaPlugin.getMethodGraphLayoutRegister().getLayoutOptions());
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
    	statistics.add(this.fieldAccessCount);
    	return statistics;
    }

    @Override
    public String toString() {
        return graph.toString();
    }
    
    //private method to search all Fieldaccesses in the graph
    private Set<FieldAccess> searchFieldAccesses() {
        
        Set<FieldAccess> fieldAccesses = new HashSet<FieldAccess>();
        
        for (JoanaVertex v1 : this.getVertexSet()) {
   
            if (this.isNormCompoundBase(v1)) {
                //check for field-gets field-sets and arrays
                for (JoanaEdge e1 : this.outgoingEdgesOf(v1)) {
                    if (e1.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
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
            if (e2.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isExprReference(v3)) {
                    //check if there is an edge back
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.EdgeKind.CF
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
                            addAdditionalFieldAccessEdges(vertices, edges);
                            
                            FieldAccessGraph graph = new FieldAccessGraph("field-get", vertices, edges);
                            graph.setFieldEntry(v1);
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
            if (e2.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
                JoanaVertex v3 = e2.getTarget();
                if (this.isNormCompoundField(v3)) {
                    //check if there is an edge back
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.EdgeKind.CF
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
                            addAdditionalFieldAccessEdges(vertices, edges);
                            
                            FieldAccessGraph graph = new FieldAccessGraph("field-set", vertices, edges);
                            graph.setFieldEntry(v1);
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
            if (e1.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
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
                      addAdditionalFieldAccessEdges(vertices, edges);
                      
                      FieldAccessGraph graph = new FieldAccessGraph("static field-get", vertices, edges);
                      graph.setFieldEntry(v1);
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
            if (e1.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
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
                      addAdditionalFieldAccessEdges(vertices, edges);
                      
                      FieldAccessGraph graph = new FieldAccessGraph("static field-set", vertices, edges);
                      graph.setFieldEntry(v1);
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
            if (e2.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
                JoanaVertex v3 = e2.getTarget();
                //check if array field gets
                if (this.isNormCompoundField(v3)) {
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isExprReference(v4)) {
                                //check there is an edge back
                                for (JoanaEdge e4 : this.outgoingEdgesOf(v4)) {
                                    if (e4.getEdgeKind() == JoanaEdge.EdgeKind.CF
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
                                        addAdditionalFieldAccessEdges(vertices, edges);
                                        
                                        FieldAccessGraph graph = new FieldAccessGraph("array field-get", vertices, edges);
                                        graph.setFieldEntry(v1);
                                        return new FieldAccess(graph, "array field-get", "array field-get");
                                    }
                                }
                            }
                        }
                    }
                } else if (this.isExprModify(v3)) {
                    //check if array field sets
                    for (JoanaEdge e3 : this.outgoingEdgesOf(v3)) {
                        if (e3.getEdgeKind() == JoanaEdge.EdgeKind.CF) {
                            JoanaVertex v4 = e3.getTarget();
                            if (this.isNormCompoundField(v4)) {
                                //check there is an edge back
                                for (JoanaEdge e4 : this.outgoingEdgesOf(v4)) {
                                    if (e4.getEdgeKind() == JoanaEdge.EdgeKind.CF
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
                                        addAdditionalFieldAccessEdges(vertices, edges);
                                        
                                        FieldAccessGraph graph = new FieldAccessGraph("array field-set", vertices, edges);
                                        graph.setFieldEntry(v1);
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
            if (pred.getEdgeKind() == JoanaEdge.EdgeKind.CF
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
        if (vertex.getNodeKind() == JoanaVertex.VertexKind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().equals("base")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isNormCompoundField(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.VertexKind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().matches("field\\s.*")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isNormCompoundIndex(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.VertexKind.NORM 
                && vertex.getNodeOperation().equals("compound") 
                && vertex.getLabel().equals("index")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isExprReference(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.VertexKind.EXPR
                && vertex.getNodeOperation().equals("reference")) { 
            return true;
        } else {
            return false;
        }
    }
    
    private boolean isExprModify(JoanaVertex vertex) {
        if (vertex.getNodeKind() == JoanaVertex.VertexKind.EXPR
                && vertex.getNodeOperation().equals("modify")) { 
            return true;
        } else {
            return false;
        }
    }

	/**
	 * Adds all edges between two vertices of the set of vertices to the set of edges to make (vertices, faEdges) the
	 * vertex induced subgraph
	 * @param vertices the vertices 
	 * @param faEdges the set of edges which should contain all edges between two vertices of param vertices
	 */
	private void addAdditionalFieldAccessEdges(Set<JoanaVertex> vertices, Set<JoanaEdge> faEdges){
        for (JoanaVertex v : vertices) {
            for(JoanaEdge e : graph.outgoingEdgesOf(v)){
                if(vertices.contains(e.getTarget())){
                    faEdges.add(e);
                }
            }
		}
	}
}
