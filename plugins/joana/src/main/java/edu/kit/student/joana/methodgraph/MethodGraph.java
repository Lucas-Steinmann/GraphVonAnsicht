package edu.kit.student.joana.methodgraph;

import edu.kit.student.graphmodel.CollapsedVertex;
import edu.kit.student.graphmodel.DefaultGraphLayering;
import edu.kit.student.graphmodel.FastGraphAccessor;
import edu.kit.student.graphmodel.Vertex;
import edu.kit.student.graphmodel.directed.DefaultDirectedGraph;
import edu.kit.student.joana.FieldAccess;
import edu.kit.student.joana.FieldAccessGraph;
import edu.kit.student.joana.JoanaCollapsedVertex;
import edu.kit.student.joana.JoanaCompoundVertex;
import edu.kit.student.joana.JoanaEdge;
import edu.kit.student.joana.JoanaGraph;
import edu.kit.student.joana.JoanaVertex;
import edu.kit.student.joana.JoanaVertex.Kind;
import edu.kit.student.objectproperty.GAnsProperty;
import edu.kit.student.plugin.LayoutOption;
import edu.kit.student.plugin.LayoutRegister;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This is a specific graph representation for a MethodGraph in JOANA .
 */
public class MethodGraph extends JoanaGraph {

    private static final String ENTRY_NAME = "Entry";
    private static LayoutRegister<MethodGraphLayoutOption> register;
    private JoanaVertex entry;
    private Set<FieldAccess> fieldAccesses;
    private List<JoanaCollapsedVertex> collapsedVertices;
    DefaultDirectedGraph<JoanaVertex, JoanaEdge> graph;
    DefaultGraphLayering<JoanaVertex> layering;
    
    private GAnsProperty<Integer> vertexCount;
    private GAnsProperty<Integer> fieldAccessCount;
    private GAnsProperty<Integer> edgeCount;

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
        //TODO: Search for method calls etc.
        this.fieldAccesses = this.searchFieldAccesses();
        this.collapsedVertices = new LinkedList<>();
        
        
        this.vertexCount = new GAnsProperty<Integer>("Vertex count", vertices.size());
        this.fieldAccessCount = new GAnsProperty<Integer>("Field accesses", this.fieldAccesses.size());
        this.edgeCount = new GAnsProperty<Integer>("Edge count", edges.size());
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
	public JoanaCollapsedVertex collapse(Set<Vertex> subset) {
	    Set<JoanaVertex> directedSubset = new HashSet<JoanaVertex>();
	    for (Vertex v : subset) {
	        if (!graph.contains(v)) {
                throw new IllegalArgumentException("Cannot collapse vertices, not contained in this graph.");
	        }
	        else {
	            directedSubset.add(graph.getVertexById(v.getID()));
	        }
	    }
		DefaultDirectedGraph<JoanaVertex, JoanaEdge> collapsedGraph = new DefaultDirectedGraph<>("", directedSubset, new HashSet<JoanaEdge>());
		Set<JoanaEdge> internalEdges = new HashSet<>();

		// Incoming and Outgoing edges from subset
		List<JoanaEdge> outGoing = new LinkedList<>();
		List<JoanaEdge> inComing = new LinkedList<>();

		for (JoanaEdge edge : getEdgeSet()) {
			boolean containsSource = subset.contains(edge.getSource());
			boolean containsTarget = subset.contains(edge.getTarget());

			if (containsSource && containsTarget) {
                graph.removeEdge(edge);
				internalEdges.add(edge);
			} else if (containsSource && !containsTarget) {
                graph.removeEdge(edge);
				outGoing.add(edge);
			} else if (!containsSource && containsTarget) {
                graph.removeEdge(edge);
				inComing.add(edge);
			}
		}
		collapsedGraph.addAllEdges(internalEdges);

		// Construct collapsed vertex
		JoanaCollapsedVertex collapsed = new JoanaCollapsedVertex("Collapsed", "Collapsed (" + collapsedGraph.getVertexSet().size() + ")",
		        collapsedGraph, new HashMap<>());
		graph.addVertex(collapsed); 
		collapsedVertices.add(collapsed);

		// Replace incoming and outgoing edges with new one pointing to the CollapseddVertex
		for (JoanaEdge edge : outGoing) {
            JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), collapsed, edge.getTarget(), edge.getEdgeKind());
            graph.addEdge(newEdge);
            collapsed.addModifiedEdge(newEdge, edge);

            if (collapsedVertices.contains(edge.getTarget())) {
                if (!graph.contains(edge.getTarget())) {
                    System.out.println("This shouldn't have happened");
                }
                JoanaCollapsedVertex otherVertex = getCollapsedVertexByID(edge.getTarget().getID());
                otherVertex.addModifiedEdge(newEdge, edge);
            }
		}
		for (JoanaEdge edge : inComing) {
            JoanaEdge newEdge = new JoanaEdge(edge.getName(), edge.getLabel(), edge.getSource(), collapsed, edge.getEdgeKind());
            graph.addEdge(newEdge);
            collapsed.addModifiedEdge(newEdge, edge);

            if (collapsedVertices.contains(edge.getSource())) {
                if (!graph.contains(edge.getSource())) {
                    System.out.println("This shouldn't have happened");
                }
                JoanaCollapsedVertex otherVertex = getCollapsedVertexByID(edge.getSource().getID());
                otherVertex.addModifiedEdge(newEdge, edge);
            }

		}

		graph.removeAllVertices(directedSubset);
		graph.removeAllEdges(new HashSet<>(inComing));
		graph.removeAllEdges(new HashSet<>(outGoing));
        System.out.println("Collapsed " +  collapsed.getID());
		
		return collapsed;
    }

	private JoanaCollapsedVertex getCollapsedVertexByID(int id)  {
	    for (JoanaCollapsedVertex cVertex : collapsedVertices) {
	        if (id == cVertex.getID()) {
	            return cVertex;
	        }
	    }
	    return null;
	}
	
	@Override
    public Set<JoanaVertex> expand(CollapsedVertex vertex) {
	    if (!collapsedVertices.contains(vertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not collapsed in this graph.");
	    }
        for (JoanaCollapsedVertex jvertex : collapsedVertices) {
            if (vertex.getID() == jvertex.getID()) {
                return this.expand(jvertex);
            }
        }
        assert (false);
        return null;
	}

    private Set<JoanaVertex> expand(JoanaCollapsedVertex vertex) {
        
        if (!graph.contains(vertex)) {
	        throw new IllegalArgumentException("Cannot expand vertex, not in this graph.");
        }
        System.out.println("Expanding " +  vertex.getID());

		Set<JoanaVertex> containedVertices = new HashSet<>(vertex.getGraph().getVertexSet());

		// Add all contained vertices and the edges between them...
		graph.addAllVertices(containedVertices);
		graph.addAllEdges(vertex.getGraph().getEdgeSet());
		// .. as well as the edges between vertices in the collapsed subgraph and other vertices
		for (JoanaEdge edge : outgoingEdgesOf(vertex)) {
            JoanaCollapsedVertex target = getCollapsedVertexByID(edge.getTarget().getID());
            if (target != null) {
                target.removeModifiedEdge(edge);
                System.out.println("Vertex " + target.getID() + " is modified");
            }
            if (!graph.contains(vertex.getModifiedEdge(edge).getTarget()))
            {
                printHierarchy();
                for (JoanaCollapsedVertex v : this.collapsedVertices) {
                    if (v.getGraph().getVertexSet().contains(vertex.getModifiedEdge(edge).getTarget())) {
                        System.out.println("Vertex " + vertex.getModifiedEdge(edge).getTarget().getID() + " is buried");
                    }
                }
                System.out.println("Vertex search ended");
            }
		    graph.addEdge(vertex.getModifiedEdge(edge));
		}
		for (JoanaEdge edge : incomingEdgesOf(vertex)) {
            JoanaCollapsedVertex source = getCollapsedVertexByID(edge.getSource().getID());
            if (source != null) {
                source.removeModifiedEdge(edge);
            }
            if (!graph.contains(vertex.getModifiedEdge(edge).getSource()))
            {
                printHierarchy();
                for (JoanaCollapsedVertex v : this.collapsedVertices) {
                    if (v.getGraph().getVertexSet().contains(vertex.getModifiedEdge(edge).getSource())) {
                        System.out.println("Vertex " + vertex.getModifiedEdge(edge).getSource().getID() + " is buried");
                    }
                }
                System.out.println("Vertex search ended");
            }
		    graph.addEdge(vertex.getModifiedEdge(edge));
		}
		
		// Remove all edges to the collapsed vertex and the collapsed vertex itself.
		for( JoanaEdge edge : graph.edgesOf(vertex)) {
		    graph.removeEdge(edge);
		}
		graph.removeVertex(vertex);
		
		return containedVertices;
    }
    private void printHierarchy() {
        for (JoanaCollapsedVertex v : this.collapsedVertices) {
            if (this.graph.contains(v)) {
                printChilds(v, 0);
            }
        }
    }
    
    private void printChilds(JoanaCollapsedVertex vertex, int indent) {
        for (int i = 0; i < indent; i++)
            System.out.print(" ");
        System.out.println(vertex.getID());
        for (JoanaVertex v : vertex.getGraph().getVertexSet()) {
            if (this.collapsedVertices.contains(v)) {
                printChilds((JoanaCollapsedVertex) v, indent+2);
            }
        }
    }


    // TODO: Not as described in design. Should return true if vertex is of type collapsed 
    // and not if it is contained in a collapsed vertex but if first is not needed, then there is no problem.
	@Override
	public boolean isCollapsed(Vertex vertex) {
		for (JoanaCollapsedVertex collapsed : collapsedVertices) {
			if (collapsed.getGraph().getVertexSet().contains(vertex))
				return true;
		}
		return false;
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
    
    @Override
    public List<GAnsProperty<?>> getStatistics() {
    	List<GAnsProperty<?>> statistics = super.getStatistics();
    	statistics.add(this.vertexCount);
    	statistics.add(this.fieldAccessCount);
    	statistics.add(this.edgeCount);
    	return statistics;
    }
    
    //private method to search all Fieldaccesses in the graph
    private Set<FieldAccess> searchFieldAccesses() {
        
        Set<FieldAccess> fieldAccesses = new HashSet<FieldAccess>();
        
        for (JoanaVertex v1 : this.graph.getVertexSet()) {
   
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
